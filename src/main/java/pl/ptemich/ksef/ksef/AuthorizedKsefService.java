package pl.ptemich.ksef.ksef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoiceQueryFiltersBuilder;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.session.SendInvoiceOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.UpoVersion;
import pl.akmf.ksef.sdk.client.model.auth.*;
import pl.akmf.ksef.sdk.client.model.invoice.*;
import pl.akmf.ksef.sdk.client.model.session.*;
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
import java.util.Comparator;
import java.util.List;


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

    private List<InvoiceOverviewDto> invoices = new ArrayList<>();
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
            invoices.clear();

            List<InvoiceOverviewDto> receivedInvoices = loadInvoicesOfType(ksefInvoicesFilter, InvoiceQuerySubjectType.SUBJECT2);
            invoices.addAll(receivedInvoices);

            List<InvoiceOverviewDto> generatedInvoices = loadInvoicesOfType(ksefInvoicesFilter, InvoiceQuerySubjectType.SUBJECT1);
            invoices.addAll(generatedInvoices);

            invoices.sort(Comparator.comparing(InvoiceOverviewDto::issueDate).reversed());

            loadedOn = OffsetDateTime.now();
        }

        return new InvoicesPackage(loadedOn, invoices);
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

            KsefUploadResultDto ksefUploadResultDto = waitAndVerify(accessToken, sessionReferenceNumber, sendInvoiceResponse.getReferenceNumber());
            ksefClient.closeOnlineSession(sessionReferenceNumber, accessToken);

            return ksefUploadResultDto;
        } catch (ApiException e) {
            log.error("Failed send invoices", e);
            throw new RuntimeException(e);
        }
    }

    private KsefUploadResultDto waitAndVerify(String accessToken, String sessionReferenceNumber, String invoiceReferenceNumber) throws ApiException {
        SessionInvoiceStatusResponse sessionInvoiceStatus;
        do {
            exceptionSafeWait(10000);
            sessionInvoiceStatus = ksefClient.getSessionInvoiceStatus(sessionReferenceNumber, invoiceReferenceNumber, accessToken);
        } while (sessionInvoiceStatus.getStatus().getCode().equals(ProcessingStatusCodes.PROCESSING));

        return new KsefUploadResultDto(
                sessionInvoiceStatus.getInvoiceNumber(),
                sessionInvoiceStatus.getKsefNumber(),
                sessionInvoiceStatus.getUpoDownloadUrl(),
                sessionInvoiceStatus.getStatus().getCode(),
                sessionInvoiceStatus.getStatus().getDescription()
        );
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
        return openOnlineSessionResponse.getReferenceNumber();
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

}
