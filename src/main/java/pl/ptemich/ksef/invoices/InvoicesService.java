package pl.ptemich.ksef.invoices;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class InvoicesService {

    private static final Logger log = LoggerFactory.getLogger(InvoicesService.class);

    public Faktura convert(byte[] xmlBytes) {
        try {
            JAXBContext context = JAXBContext.newInstance(Faktura.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            try (ByteArrayInputStream input = new ByteArrayInputStream(xmlBytes)) {
                return (Faktura) unmarshaller.unmarshal(input);
            }
        } catch (IOException e) {
            log.error("Failed to convert invoice", e);
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            log.error("Failed to convert invoice", e);
            throw new RuntimeException(e);
        }
    }

}
