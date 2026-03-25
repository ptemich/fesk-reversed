package pl.ptemich.ksef.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class InvoicesConverter {

    private static final Logger log = LoggerFactory.getLogger(InvoicesConverter.class);

    public static Faktura convert(byte[] xmlBytes) {
        try {
            JAXBContext context = JAXBContext.newInstance(Faktura.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            try (ByteArrayInputStream input = new ByteArrayInputStream(xmlBytes)) {
                return (Faktura) unmarshaller.unmarshal(input);
            }
        } catch (IOException | JAXBException e) {
            log.error("Failed to convert invoice", e);
            throw new RuntimeException(e);
        }
    }

}
