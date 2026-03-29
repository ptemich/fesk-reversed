package pl.ptemich.ksef.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.local.*;
import pl.ptemich.ksef.util.InvoicesConverter;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.ksef.KsefInvoicesFilter;
import pl.ptemich.ksef.ksef.KsefInvoicesPackage;
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
        KsefInvoicesPackage ksefInvoicesPackage = authorizedKsefService.loadInvoices(false, ksefInvoicesFilter);
        model.addAttribute("ksefInvoicesPackage", ksefInvoicesPackage);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL);
        model.addAttribute("localInvoices", localInvoices);

        Set<String> localInvoiceConfirmations = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY);
        model.addAttribute("localInvoiceConfirmations", localInvoiceConfirmations);

        return "ksefInvoices";
    }

    @GetMapping("/reload")
    public String reload(Model model) {
        KsefInvoicesFilter ksefInvoicesFilter = new KsefInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        KsefInvoicesPackage ksefInvoicesPackage = authorizedKsefService.loadInvoices(true, ksefInvoicesFilter);
        model.addAttribute("ksefInvoicesPackage", ksefInvoicesPackage);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL);
        model.addAttribute("localInvoices", localInvoices);

        Set<String> localInvoiceConfirmations = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY);
        model.addAttribute("localInvoiceConfirmations", localInvoiceConfirmations);

        return "ksefInvoicesTable";
    }

    @GetMapping("/copy/{ksefNumber}/{invoiceSource}")
    public String copy(@PathVariable String ksefNumber, @PathVariable InvoiceSource invoiceSource, Model model) {
        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);
        diskOperationsService.saveToDisk(invoiceSource, ksefNumber, content);

        //Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL);
        model.addAttribute("availableLocally", true);
        model.addAttribute("invoiceSource", invoiceSource);

        return "downloadOrView";
    }

//    @GetMapping("/download/xml/{ksefNumber}")
//    public ResponseEntity<byte[]> downloadXml(@PathVariable String ksefNumber) {
//        byte[] content = authorizedKsefService.loadInvoiceXml(ksefNumber);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_XML);
//        // This header forces the browser to download the file instead of opening it
//        headers.setContentDispositionFormData("filename", ksefNumber + ".xml");
//        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//
//        return new ResponseEntity<>(content, headers, HttpStatus.OK);
//    }

    @GetMapping("/details/local/{ksefNumber}/{invoiceSource}")
    public String detailsAcard(@PathVariable String ksefNumber, @PathVariable InvoiceSource invoiceSource, Model model) {
        model.addAttribute("ksefNumber", ksefNumber);

        byte[] xmlBytes = diskOperationsService.loadFromDisk(invoiceSource, ksefNumber);
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
        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

        LocalInvoicesFilter filter = new LocalInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        LocalInvoicesPackage invoicesPackage = localInvoiceService.loadInvoices(filter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> downloadedInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL_PROCESSED);
        model.addAttribute("downloadedInvoices", downloadedInvoices);

        return "localInvoices";
    }

    @GetMapping("/reloadLocal")
    public String reloadLocal(Model model) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        model.addAttribute("localConfig", localConfig);

        LocalInvoicesFilter filter = new LocalInvoicesFilter(OffsetDateTime.now().minusDays(14), OffsetDateTime.now().plusDays(1));
        LocalInvoicesPackage invoicesPackage = localInvoiceService.loadInvoices(filter);
        model.addAttribute("invoicesPackage", invoicesPackage);

        Set<String> downloadedInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL_PROCESSED);
        model.addAttribute("downloadedInvoices", downloadedInvoices);

        return "localInvoicesTable";
    }

    @PostMapping("/send/{fileId}")
    public String sendToKsef(Model model, @PathVariable String fileId) {
        LocalInvoice localInvoice = localInvoiceService.sendToKsef(fileId);
        model.addAttribute("invoice", localInvoice);

        Set<String> localInvoices = diskOperationsService.listLocalInvoices(InvoiceSource.KSEF_TO_LOCAL_PROCESSED);
        model.addAttribute("availableLocally", localInvoices.contains(localInvoice.getKsefNumber()));
        return "sendOrNumber";
    }
}
