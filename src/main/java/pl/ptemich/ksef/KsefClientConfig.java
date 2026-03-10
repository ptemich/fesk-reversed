package pl.ptemich.ksef;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.DefaultLighthouseKsefClient;
import pl.akmf.ksef.sdk.api.services.*;
import pl.akmf.ksef.sdk.client.interfaces.*;
import pl.ptemich.ksef.util.ExampleApiProperties;
import pl.ptemich.ksef.util.HttpClientBuilder;
import pl.ptemich.ksef.util.HttpClientConfig;


import java.net.http.HttpClient;

@Configuration
//@RequiredArgsConstructor
public class KsefClientConfig {

    private final ExampleApiProperties apiProperties;

    public KsefClientConfig(ExampleApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }

    @Bean
    public CertificateService initDefaultCertificateService() {
        return new DefaultCertificateService();
    }

    @Bean
    public SignatureService initDefaultSignatureService() {
        return new DefaultSignatureService();
    }

    @Bean
    public VerificationLinkService initDefaultVerificationLinkService(@Value("${sdk.config.qr-uri}") String qrUri) {
        return new DefaultVerificationLinkService(qrUri);
    }

    @Bean
    public QrCodeService initDefaultQrCodeService() {
        return new DefaultQrCodeService();
    }

    @Bean
    public DefaultKsefClient initDefaultKsefClient() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        HttpClient apiClient = HttpClientBuilder.createHttpBuilder(new HttpClientConfig()).build();
        return new DefaultKsefClient(
                apiClient,
                apiProperties,
                objectMapper);
    }

    @Bean
    public DefaultLighthouseKsefClient initDefaultLighthouseClient(@Value("${sdk.config.lighthouse-base-uri}") String lighthouseBaseUri) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        HttpClient httpClient = HttpClientBuilder.createHttpBuilder(new HttpClientConfig()).build();
        return new DefaultLighthouseKsefClient(
                objectMapper,
                httpClient,
                lighthouseBaseUri,
                apiProperties.getRequestTimeout(),
                apiProperties.getDefaultHeaders()
        );
    }

    @Bean
    public DefaultCryptographyService initDefaultCryptographyService(KSeFClient kSeFClient) {
        return new DefaultCryptographyService(kSeFClient);
    }
}
