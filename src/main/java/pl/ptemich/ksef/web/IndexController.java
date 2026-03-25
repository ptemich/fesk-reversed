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
import pl.ptemich.ksef.local.*;
import pl.ptemich.ksef.util.InvoicesConverter;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.ksef.KsefInvoicesFilter;
import pl.ptemich.ksef.ksef.InvoicesPackage;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class IndexController {

    private final LocalConfigService localConfigService;
    private final AuthorizedKsefService authorizedKsefService;
    private final DiskOperationsService diskOperationsService;
    private final LocalInvoiceService localInvoiceService;
    
    public IndexController(
            LocalConfigService localConfigService,
            AuthorizedKsefService authorizedKsefService,
            DiskOperationsService diskOperationsService,
            LocalInvoiceService localInvoiceService
    ) {
        this.localConfigService = localConfigService;
        this.authorizedKsefService = authorizedKsefService;
        this.diskOperationsService = diskOperationsService;
        this.localInvoiceService = localInvoiceService;
    }

    @GetMapping
    public String listKsefInvoices(Model model) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

        KsefInvoicesFilter ksefInvoicesFilter = new KsefInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(false, ksefInvoicesFilter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.RECEIVED);
        model.addAttribute("localInvoices", localInvoices);

        return "ksefInvoices";
    }

    @GetMapping("/reload")
    public String reload(Model model) {
        KsefInvoicesFilter ksefInvoicesFilter = new KsefInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(true, ksefInvoicesFilter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.RECEIVED);
        model.addAttribute("localInvoices", localInvoices);

        return "table";
    }

    @GetMapping("/copy/{ksefNumber}")
    public String copy(@PathVariable String ksefNumber, Model model) {
        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);
        diskOperationsService.saveToDisk(ksefNumber, content);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.RECEIVED);
        model.addAttribute("availableLocally", localInvoices.contains(ksefNumber));

        return "downloadOrView";
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

        byte[] xmlBytes = diskOperationsService.loadFromDisk(InvoiceSource.RECEIVED, ksefNumber);
        Faktura invoice = InvoicesConverter.convert(xmlBytes);
        model.addAttribute("invoice", invoice);

        return "details";
    }

    @GetMapping("/details/ksef/{ksefNumber}")
    public String detailsKsef(@PathVariable String ksefNumber, Model model) {
        model.addAttribute("ksefNumber", ksefNumber);

        byte[] xmlBytes = authorizedKsefService.loadInvoiceXml(ksefNumber);
        Faktura invoice = InvoicesConverter.convert(xmlBytes);
        model.addAttribute("invoice", invoice);

        return "details";
    }

    @GetMapping("/local")
    public String listLocalInvoices(Model model) {
        //String invoiceNumber = "__-0316FSV-26000170";
        //byte[] invoiceContent = diskOperationsService.loadFromDisk(InvoiceSource.GENERATED, invoiceNumber);
        //Map<String, byte[]> invoicesContentByName = Map.of(invoiceNumber, invoiceContent);
        //authorizedKsefService.sendInvoice(invoiceContent);

        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

//        InvoicesFilter invoicesFilter = new InvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
//        InvoicesPackage invoicesPackage = authorizedKsefService.loadInvoices(false, invoicesFilter);
//        model.addAttribute("invoicesPackage", invoicesPackage);

        LocalInvoicesFilter filter = new LocalInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        List<LocalInvoice> localInvoices = localInvoiceService.loadInvoices(filter);

        //Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.GENERATED);
        model.addAttribute("localInvoices", localInvoices);

        return "localInvoices";
    }
}
