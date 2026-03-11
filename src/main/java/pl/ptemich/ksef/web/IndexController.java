package pl.ptemich.ksef.web;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.acard.AcardService;
import pl.ptemich.ksef.invoices.InvoicesService;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.ksef.InvoiceOverviewDto;
import pl.ptemich.ksef.ksef.InvoicesFilter;
import pl.ptemich.ksef.ksef.InvoicesPackage;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.time.OffsetDateTime;
import java.util.Set;

@Controller
public class IndexController {

    private final LocalConfigService localConfigService;
    private final AuthorizedKsefService authorizedKsefService;
    private final AcardService acardService;
    private final InvoicesService invoicesService;
    
    public IndexController(
            LocalConfigService localConfigService,
            AuthorizedKsefService authorizedKsefService,
            AcardService acardService,
            InvoicesService invoicesService
    ) {
        this.localConfigService = localConfigService;
        this.authorizedKsefService = authorizedKsefService;
        this.acardService = acardService;
        this.invoicesService = invoicesService;
    }

    @GetMapping
    public String index(Model model) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

        InvoicesFilter invoicesFilter = new InvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(false, invoicesFilter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> acardInvoices = acardService.loadAcardList();
        model.addAttribute("acardInvoices", acardInvoices);

        return "index";
    }

    @GetMapping("/reload")
    public String reload(Model model) {
        InvoicesFilter invoicesFilter = new InvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(true, invoicesFilter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> acardInvoices = acardService.loadAcardList();
        model.addAttribute("acardInvoices", acardInvoices);

        return "table";
    }

    @GetMapping("/copy/{ksefNumber}")
    public String copy(@PathVariable String ksefNumber, Model model) {
        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);
        acardService.save(ksefNumber, content);

        InvoiceOverviewDto invoice = InvoiceOverviewDto.fromKsefInvoice(ksefNumber, invoicesService.convert(content));
        model.addAttribute("invoice", invoice);
        //InvoicesFilter invoicesFilter = new InvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        //InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(false, invoicesFilter);
        //model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> acardInvoices = acardService.loadAcardList();
        // model.addAttribute("acardInvoices", acardInvoices);
        model.addAttribute("availableLocally", acardInvoices.contains(ksefNumber));

        return "invoiceRow";
    }

    @GetMapping("/download/xml/{ksefNumber}")
    public ResponseEntity<byte[]> downloadXml(@PathVariable String ksefNumber) {
        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        // This header forces the browser to download the file instead of opening it
        headers.setContentDispositionFormData("filename", ksefNumber + ".xml");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @GetMapping("/details/acard/{ksefNumber}")
    public String detailsAcard(@PathVariable String ksefNumber, Model model) {
        model.addAttribute("ksefNumber", ksefNumber);

        byte[] xmlBytes = acardService.load(ksefNumber);
        Faktura invoice = invoicesService.convert(xmlBytes);
        model.addAttribute("invoice", invoice);

        return "details";
    }

    @GetMapping("/details/ksef/{ksefNumber}")
    public String detailsKsef(@PathVariable String ksefNumber, Model model) {
        model.addAttribute("ksefNumber", ksefNumber);

        byte[] xmlBytes = authorizedKsefService.loadInvoiceXml(ksefNumber);
        Faktura invoice = invoicesService.convert(xmlBytes);
        model.addAttribute("invoice", invoice);

        return "details";
    }

}
