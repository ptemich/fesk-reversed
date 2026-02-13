package pl.ptemich.ksef;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.ptemich.ksef.acard.AcardService;

import java.util.Set;

@Disabled
@SpringBootTest
class AcardServiceTests {

    @Autowired
    private AcardService acardService;

    @Test
    void contextLoads() {
        Set<String> xmlInvoices = acardService.loadAcardList();
        System.out.println(xmlInvoices);
    }

}
