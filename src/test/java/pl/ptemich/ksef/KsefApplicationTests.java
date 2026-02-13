package pl.ptemich.ksef;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoiceQueryFiltersBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.CertificateService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.*;
import pl.akmf.ksef.sdk.client.model.invoice.*;
import pl.akmf.ksef.sdk.client.model.util.SortOrder;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.OffsetDateTime;
import java.util.Base64;

@SpringBootTest
@Disabled
class KsefApplicationTests {

    @Autowired
    private KsefClientConfig ksefClientConfig;

    @Test
    void contextLoads() {
        String ksefToken = "20260206-EC-1CB1D61000-E2845DA36A-8F|nip-6421014942|16f6ccfe85b5436a94538697b3562c073583672b019b4de8ad845fb9403be0c2";
        String nip = "6421014942";

        DefaultKsefClient ksefClient = ksefClientConfig.initDefaultKsefClient();
        DefaultCryptographyService cryptographyService = ksefClientConfig.initDefaultCryptographyService(ksefClient);

        try {
            AuthenticationChallengeResponse authChallenge = ksefClient.getAuthChallenge();
            byte[] encryptedToken = cryptographyService.encryptKsefTokenUsingPublicKey(ksefToken, authChallenge.getTimestamp());

            AuthKsefTokenRequest authKsefTokenRequest = new AuthKsefTokenRequestBuilder()
                    .withChallenge(authChallenge.getChallenge())
                    .withContextIdentifier(new ContextIdentifier(ContextIdentifier.IdentifierType.NIP, nip))
                    .withEncryptedToken(Base64.getEncoder().encodeToString(encryptedToken))
                    .build();

            SignatureResponse signatureResponse = ksefClient.authenticateByKSeFToken(authKsefTokenRequest);
            String tempAccessToken = signatureResponse.getAuthenticationToken().getToken();
            System.out.println("AC:" + tempAccessToken);

            AuthStatus authStatus = ksefClient.getAuthStatus(signatureResponse.getReferenceNumber(), tempAccessToken);
            System.out.println(authStatus.getStatus());

            AuthOperationStatusResponse authOperationStatusResponse = ksefClient.redeemToken(tempAccessToken);
            String accessToken = authOperationStatusResponse.getAccessToken().getToken();
            System.out.println("AC-R:" + accessToken);

            InvoiceQueryFilters filter = new InvoiceQueryFiltersBuilder()
                    .withSubjectType(InvoiceQuerySubjectType.SUBJECT2)
                    .withDateRange(new InvoiceQueryDateRange(InvoiceQueryDateType.INVOICING, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5)))
                    .build();

            QueryInvoiceMetadataResponse queryInvoiceMetadataResponse = ksefClient.queryInvoiceMetadata(
                    0,
                    10,
                    SortOrder.ASC,
                    filter,
                    accessToken
            );

            System.out.println(queryInvoiceMetadataResponse.getInvoices());


            InvoiceMetadata firstInvoice = queryInvoiceMetadataResponse.getInvoices().getFirst();
            byte[] invoice = ksefClient.getInvoice(firstInvoice.getKsefNumber(), accessToken);


            File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
            File newDirectory = new File(TEMP_DIRECTORY, "faktury");
            if (!newDirectory.exists()) {
                newDirectory.mkdir();
            }

            Path path = Paths.get("./faktury/faktura.xml");

            try {
                File outputFile = new File(newDirectory, firstInvoice.getKsefNumber() + ".xml");
                outputFile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(invoice);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

}
