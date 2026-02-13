package pl.ptemich.ksef;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.acard.AcardService;

import java.util.Set;

@Disabled
@SpringBootTest
class AcardServiceTests {

    @Autowired
    private AcardService acardService;

    @Test
    public void loadList() {
        Set<String> xmlInvoices = acardService.loadAcardList();
        System.out.println(xmlInvoices);
    }

    @Test
    public void loadSingle() {
        byte[] invoice = acardService.load("6770004439-20260212-310020E63D9C-EA");
        System.out.println("test");
    }

}
