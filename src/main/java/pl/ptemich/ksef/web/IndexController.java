package pl.ptemich.ksef.web;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;

import pl.ptemich.ksef.acard.AcardService;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.ksef.InvoicesPackage;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.util.List;
import java.util.Set;

@Controller
public class IndexController {

    private final LocalConfigService localConfigService;
    private final AuthorizedKsefService authorizedKsefService;
    private final AcardService acardService;

    public IndexController(LocalConfigService localConfigService, AuthorizedKsefService authorizedKsefService, AcardService acardService) {
        this.localConfigService = localConfigService;
        this.authorizedKsefService = authorizedKsefService;
        this.acardService = acardService;
    }

    @GetMapping
    public String index(Model model) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(false);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> acardInvoices = acardService.loadAcardList();
        model.addAttribute("acardInvoices", acardInvoices);

        return "index";
    }

    @GetMapping("/reload")
    public String reload(Model model) {
        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(true);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> acardInvoices = acardService.loadAcardList();
        model.addAttribute("acardInvoices", acardInvoices);

        return "table";
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

//    @GetMapping("/details/{ksefNumber}")
//    public String details(@PathVariable String ksefNumber, Model model) {
//        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);
//
//        Faktura invoice = invoicesService.load(content);
//        model.addAttribute("invoice", invoice);
//
//        return "details";
//    }

}
