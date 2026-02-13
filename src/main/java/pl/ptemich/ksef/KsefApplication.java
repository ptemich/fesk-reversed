package pl.ptemich.ksef;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("pl.ptemich.ksef.util")
@SpringBootApplication
public class KsefApplication {

    public static void main(String[] args) {
        SpringApplication.run(KsefApplication.class, args);
    }

}
