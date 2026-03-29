package pl.ptemich.ksef.ksef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.batch.OpenBatchSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoiceQueryFiltersBuilder;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.session.SendInvoiceOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.StatusInfo;
import pl.akmf.ksef.sdk.client.model.UpoVersion;
import pl.akmf.ksef.sdk.client.model.auth.*;
import pl.akmf.ksef.sdk.client.model.invoice.*;
import pl.akmf.ksef.sdk.client.model.session.*;
import pl.akmf.ksef.sdk.client.model.session.batch.BatchPartSendingInfo;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceResponse;
import pl.akmf.ksef.sdk.client.model.util.SortOrder;
import pl.ptemich.ksef.KsefClientConfig;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Service
public class AuthorizedKsefService {

    private final static int INVOICES_PAGE_SIZE = 50; // max 250
    private static final Logger log = LoggerFactory.getLogger(AuthorizedKsefService.class);

    private final LocalConfigService localConfigService;
    private final KsefClientConfig ksefClientConfig;

    private LocalConfig localConfig = null;
    private DefaultKsefClient ksefClient = null;
    private DefaultCryptographyService cryptographyService = null;

    private boolean initialized = false;
    private KsefToken ksefToken = null;

    private List<InvoiceOverviewDto> receivedInvoices;
    private List<InvoiceOverviewDto> generatedInvoices;
    private OffsetDateTime loadedOn;

    public AuthorizedKsefService(LocalConfigService localConfigService, KsefClientConfig ksefClientConfig) {
        this.localConfigService = localConfigService;
        this.ksefClientConfig = ksefClientConfig;
    }

    private void init() {
        if (!initialized) {
            localConfig = localConfigService.loadFromDisk();
            ksefClient = ksefClientConfig.initDefaultKsefClient();
            cryptographyService = ksefClientConfig.initDefaultCryptographyService(ksefClient);
            initialized = true;
        }
    }

    public InvoicesPackage loadInvoices(
            boolean forceRefresh,
            KsefInvoicesFilter ksefInvoicesFilter
    ) {
        if (forceRefresh || loadedOn == null) {
            //generatedInvoices = loadInvoicesOfType(invoicesFilter, InvoiceQuerySubjectType.SUBJECT1);
            receivedInvoices = loadInvoicesOfType(ksefInvoicesFilter, InvoiceQuerySubjectType.SUBJECT2);

            loadedOn = OffsetDateTime.now();
        }

        return new InvoicesPackage(loadedOn, receivedInvoices);
    }

    public List<InvoiceOverviewDto> loadInvoicesOfType(
            KsefInvoicesFilter ksefInvoicesFilter,
            InvoiceQuerySubjectType invoiceSubjectType
    ) {


        String accessToken = getAccessToken();

        InvoiceQueryFilters filter = new InvoiceQueryFiltersBuilder()
                .withSubjectType(invoiceSubjectType)
                .withDateRange(new InvoiceQueryDateRange(InvoiceQueryDateType.INVOICING, ksefInvoicesFilter.from(), ksefInvoicesFilter.to()))
                .build();

        try {
            int pageOffset = 0;

            QueryInvoiceMetadataResponse queryInvoiceMetadataResponse = ksefClient.queryInvoiceMetadata(
                    pageOffset,
                    INVOICES_PAGE_SIZE,
                    SortOrder.DESC,
                    filter,
                    accessToken
            );

            List<InvoiceMetadata> ksefInvoices = queryInvoiceMetadataResponse.getInvoices();

            while (queryInvoiceMetadataResponse.getHasMore()) {
                pageOffset++;
                queryInvoiceMetadataResponse = ksefClient.queryInvoiceMetadata(
                        pageOffset,
                        INVOICES_PAGE_SIZE,
                        SortOrder.DESC,
                        filter,
                        accessToken
                );

                ksefInvoices.addAll(queryInvoiceMetadataResponse.getInvoices());
            }

            var invoices = ksefInvoices.stream()
                    .map(InvoiceOverviewDto::fromKsefInvoiceMetadata)
                    .toList();

            return invoices;

        } catch (ApiException e) {
            log.error("Failed to fetch invoices", e);
            throw new RuntimeException(e);
        }
    }

    public KsefUploadResultDto sendInvoice(byte[] invoiceContent) {
        try {
            UpoVersion upoVersion = UpoVersion.UPO_4_3;
            String accessToken = getAccessToken();

            EncryptionData encryptionData = cryptographyService.getEncryptionData();

            String sessionReferenceNumber = openOnlineSession(
                    encryptionData,
                    SystemCode.FA_3,
                    SchemaVersion.VERSION_1_0E,
                    SessionValue.FA,
                    accessToken
            );

            byte[] encryptedInvoice = cryptographyService.encryptBytesWithAES256(
                    invoiceContent,
                    encryptionData.cipherKey(),
                    encryptionData.cipherIv()
            );

            FileMetadata invoiceMetadata = cryptographyService.getMetaData(invoiceContent);
            FileMetadata encryptedInvoiceMetadata = cryptographyService.getMetaData(encryptedInvoice);

            SendInvoiceOnlineSessionRequest sendInvoiceOnlineSessionRequest = new SendInvoiceOnlineSessionRequestBuilder()
                    .withInvoiceHash(invoiceMetadata.getHashSHA())
                    .withInvoiceSize(invoiceMetadata.getFileSize())
                    .withEncryptedInvoiceHash(encryptedInvoiceMetadata.getHashSHA())
                    .withEncryptedInvoiceSize(encryptedInvoiceMetadata.getFileSize())
                    .withEncryptedInvoiceContent(Base64.getEncoder().encodeToString(encryptedInvoice))
                    .build();

            SendInvoiceResponse sendInvoiceResponse = ksefClient.onlineSessionSendInvoice(
                    sessionReferenceNumber,
                    sendInvoiceOnlineSessionRequest,
                    accessToken
            );

            KsefUploadResultDto ksefUploadResultDto = waitAndVeify(accessToken, sessionReferenceNumber, sendInvoiceResponse.getReferenceNumber());
            ksefClient.closeOnlineSession(sessionReferenceNumber, accessToken);

            return ksefUploadResultDto;
        } catch (ApiException e) {
            log.error("Failed send invoices", e);
            throw new RuntimeException(e);
        }
    }

    private KsefUploadResultDto waitAndVeify(String accessToken, String sessionReferenceNumber, String invoiceReferenceNumber) throws ApiException {
        StatusInfo status;

        do {
            exceptionSafeWait(10000);
            SessionInvoiceStatusResponse sessionInvoiceStatus = ksefClient.getSessionInvoiceStatus(sessionReferenceNumber, invoiceReferenceNumber, accessToken);
            status = sessionInvoiceStatus.getStatus();
        } while (status.getCode().equals(ProcessingStatusCodes.PROCESSING));

        return new KsefUploadResultDto(null, status.getCode(), status.getDescription());
    }

    private void exceptionSafeWait(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            log.error("Failed to wait for session invoice status", e);
        }
    }

    private String openOnlineSession(EncryptionData encryptionData, SystemCode systemCode,
                                     SchemaVersion schemaVersion,
                                     SessionValue value,
                                     String accessToken) throws ApiException {
        OpenOnlineSessionRequest request = new OpenOnlineSessionRequestBuilder()
                .withFormCode(new FormCode(systemCode, schemaVersion, value))
                .withEncryptionInfo(encryptionData.encryptionInfo())
                .build();

        OpenOnlineSessionResponse openOnlineSessionResponse = ksefClient.openOnlineSession(request, UpoVersion.UPO_4_3, accessToken);
        //Assertions.assertNotNull(openOnlineSessionResponse);
        //Assertions.assertNotNull(openOnlineSessionResponse.getReferenceNumber());
        return openOnlineSessionResponse.getReferenceNumber();
    }

    private boolean isInvoicesInSessionProcessed(String sessionReferenceNumber, String accessToken) {
        try {
            SessionStatusResponse statusResponse = ksefClient.getSessionStatus(sessionReferenceNumber, accessToken);
            return statusResponse != null &&
                    statusResponse.getSuccessfulInvoiceCount() != null &&
                    statusResponse.getSuccessfulInvoiceCount() > 0;
        } catch (Exception e) {
            //Assertions.fail(e.getMessage());
        }
        return false;
    }

    public byte[] loadInvoiceXml(String ksefNumber) {
        String accessToken = getAccessToken();
        try {
            return ksefClient.getInvoice(ksefNumber, accessToken);
        } catch (ApiException e) {
            log.error("Failed to fetch invoice " + ksefNumber, e);
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken() {
        if (ksefToken == null || ksefToken.expiresOn().isBefore(OffsetDateTime.now())) {
            ksefToken = refreshToken();
            if (ksefToken == null) {
                ksefToken = getNewToken(); // refresh failed - get a new one
            }
        }

        return ksefToken.token();
    }

    private KsefToken refreshToken() {
        if (ksefToken != null && ksefToken.refreshExpiresOn().isAfter(OffsetDateTime.now())) {
            try {
                AuthenticationTokenRefreshResponse authenticationTokenRefreshResponse = ksefClient.refreshAccessToken(ksefToken.refreshToken());
                TokenInfo accessToken = authenticationTokenRefreshResponse.getAccessToken();
                return new KsefToken(accessToken.getToken(), accessToken.getValidUntil(), ksefToken.refreshToken(), ksefToken.refreshExpiresOn());
            } catch (ApiException e) {
                log.error("Failed to refresh token", e);
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private KsefToken getNewToken() {
        init();
        try {
            AuthenticationChallengeResponse authChallenge = ksefClient.getAuthChallenge();
            byte[] encryptedToken = cryptographyService.encryptKsefTokenUsingPublicKey(localConfig.getKsefToken(), authChallenge.getTimestamp());

            AuthKsefTokenRequest authKsefTokenRequest = new AuthKsefTokenRequestBuilder()
                    .withChallenge(authChallenge.getChallenge())
                    .withContextIdentifier(new ContextIdentifier(ContextIdentifier.IdentifierType.NIP, localConfig.getNip()))
                    .withEncryptedToken(Base64.getEncoder().encodeToString(encryptedToken))
                    .build();

            SignatureResponse signatureResponse = ksefClient.authenticateByKSeFToken(authKsefTokenRequest);
            String tempAccessToken = signatureResponse.getAuthenticationToken().getToken();

            AuthStatus authStatus = ksefClient.getAuthStatus(signatureResponse.getReferenceNumber(), tempAccessToken);
            int maxTries = 5;
            while (maxTries > 0 && !authStatus.getStatus().getCode().equals(200)) {
                maxTries--;
                Thread.sleep(1000);
                authStatus = ksefClient.getAuthStatus(signatureResponse.getReferenceNumber(), tempAccessToken);
            }

            AuthOperationStatusResponse authOperationStatusResponse = ksefClient.redeemToken(tempAccessToken);

            TokenInfo accessToken = authOperationStatusResponse.getAccessToken();
            TokenInfo refreshToken = authOperationStatusResponse.getRefreshToken();

            return new KsefToken(accessToken.getToken(), accessToken.getValidUntil(), refreshToken.getToken(), refreshToken.getValidUntil());

        } catch (ApiException e) {
            log.error("Failed to fetch invoices", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("Failed to fetch invoices", e);
            throw new RuntimeException(e);
        }
    }






    private List<BatchPartSendingInfo> encryptZipParts(List<byte[]> zipParts, byte[] cipherKey, byte[] cipherIv) {
        List<BatchPartSendingInfo> encryptedZipParts = new ArrayList<>();
        for (int i = 0; i < zipParts.size(); i++) {
            byte[] encryptedZipPart = cryptographyService.encryptBytesWithAES256(
                    zipParts.get(i),
                    cipherKey,
                    cipherIv
            );
            FileMetadata zipPartMetadata = cryptographyService.getMetaData(encryptedZipPart);
            encryptedZipParts.add(new BatchPartSendingInfo(encryptedZipPart, zipPartMetadata, (i + 1)));
        }
        return encryptedZipParts;
    }

    private OpenBatchSessionRequest buildOpenBatchSessionRequest(
            FileMetadata zipMetadata,
            List<BatchPartSendingInfo> encryptedZipParts,
            EncryptionData encryptionData
    ) {
        OpenBatchSessionRequestBuilder builder = OpenBatchSessionRequestBuilder.create()
                .withFormCode(SystemCode.FA_2, SchemaVersion.VERSION_1_0E, SessionValue.FA)
                .withOfflineMode(false)
                .withBatchFile(zipMetadata.getFileSize(), zipMetadata.getHashSHA());

        for (int i = 0; i < encryptedZipParts.size(); i++) {
            BatchPartSendingInfo part = encryptedZipParts.get(i);
            builder = builder.addBatchFilePart(i + 1,
                    part.getMetadata().getFileSize(), part.getMetadata().getHashSHA());
        }

        return builder.endBatchFile()
                .withEncryption(
                        encryptionData.encryptionInfo().getEncryptedSymmetricKey(),
                        encryptionData.encryptionInfo().getInitializationVector()
                )
                .build();
    }

}
