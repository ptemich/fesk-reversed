package pl.ptemich.ksef.ksef;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

    private boolean initialized = false;
    private KsefToken ksefToken = null;

    public AuthorizedKsefService(LocalConfigService localConfigService, KsefClientConfig ksefClientConfig) {
        this.localConfigService = localConfigService;
        this.ksefClientConfig = ksefClientConfig;
    }

    private final LocalConfigService localConfigService;
    private final KsefClientConfig ksefClientConfig;

    private LocalConfig localConfig = null;// = localConfigService.loadFromDisk();
    private DefaultKsefClient ksefClient = null;// = ksefClientConfig.initDefaultKsefClient();
    private DefaultCryptographyService cryptographyService = null;// = ksefClientConfig.initDefaultCryptographyService(ksefClient);

    private List<InvoiceMetadata> invoices;
    private OffsetDateTime loadedOn;

    private void init() {
        if (!initialized) {
            localConfig = localConfigService.loadFromDisk();
            ksefClient = ksefClientConfig.initDefaultKsefClient();
            cryptographyService = ksefClientConfig.initDefaultCryptographyService(ksefClient);
            initialized = true;
        }
    }

    public InvoicesPackage loadInvoices(boolean forceRefresh) {
        if (forceRefresh || invoices == null) {
            String accessToken = getAccessToken();

            InvoiceQueryFilters filter = new InvoiceQueryFiltersBuilder()
                    .withSubjectType(InvoiceQuerySubjectType.SUBJECT2)
                    .withDateRange(new InvoiceQueryDateRange(InvoiceQueryDateType.INVOICING, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5)))
                    .build();

            try {
                QueryInvoiceMetadataResponse queryInvoiceMetadataResponse = ksefClient.queryInvoiceMetadata(
                        0,
                        20,
                        SortOrder.ASC,
                        filter,
                        accessToken
                );

                invoices = queryInvoiceMetadataResponse.getInvoices().reversed();
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
            ksefToken = getFreshToken();
        }

        return ksefToken.token();
    }

    private KsefToken getFreshToken() {
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

            // TODO add waiting
            AuthStatus authStatus = ksefClient.getAuthStatus(signatureResponse.getReferenceNumber(), tempAccessToken);

            AuthOperationStatusResponse authOperationStatusResponse = ksefClient.redeemToken(tempAccessToken);
            TokenInfo accessToken = authOperationStatusResponse.getAccessToken();
            TokenInfo refreshToken = authOperationStatusResponse.getRefreshToken();

            return new KsefToken(accessToken.getToken(), accessToken.getValidUntil(), refreshToken.getToken());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
