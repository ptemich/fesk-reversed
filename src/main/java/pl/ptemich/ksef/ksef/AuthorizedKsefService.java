package pl.ptemich.ksef.ksef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoiceQueryFiltersBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.*;
import pl.akmf.ksef.sdk.client.model.invoice.*;
import pl.akmf.ksef.sdk.client.model.util.SortOrder;
import pl.ptemich.ksef.KsefClientConfig;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.time.OffsetDateTime;
import java.util.Base64;
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

    private List<InvoiceMetadata> invoices;
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

    public InvoicesPackage loadInvoices(boolean forceRefresh, InvoicesFilter invoicesFilter) {
        if (forceRefresh || invoices == null) {
            String accessToken = getAccessToken();

            InvoiceQueryFilters filter = new InvoiceQueryFiltersBuilder()
                    .withSubjectType(InvoiceQuerySubjectType.SUBJECT2)
                    .withDateRange(new InvoiceQueryDateRange(InvoiceQueryDateType.INVOICING, invoicesFilter.from(), invoicesFilter.to()))
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

                invoices = queryInvoiceMetadataResponse.getInvoices();

                while (queryInvoiceMetadataResponse.getHasMore()) {
                    pageOffset++;
                    queryInvoiceMetadataResponse = ksefClient.queryInvoiceMetadata(
                            pageOffset,
                            INVOICES_PAGE_SIZE,
                            SortOrder.DESC,
                            filter,
                            accessToken
                    );

                    invoices.addAll(queryInvoiceMetadataResponse.getInvoices());
                }

                loadedOn = OffsetDateTime.now();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }

        return new InvoicesPackage(loadedOn, invoices);
    }

    public byte[] loadInvoiceXml(String ksefNumber) {
        String accessToken = getAccessToken();
        try {
            return ksefClient.getInvoice(ksefNumber, accessToken);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken() {
        if (ksefToken == null || ksefToken.expiresOn().isAfter(OffsetDateTime.now())) {
            ksefToken = refreshToken();
            if (ksefToken == null) {
                ksefToken = getNewToken(); // refresh failed - get a new one
            }
        }

        return ksefToken.token();
    }

    private KsefToken refreshToken() {
        if (ksefToken != null && ksefToken.refreshExpiresOn().isBefore(OffsetDateTime.now())) {
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
