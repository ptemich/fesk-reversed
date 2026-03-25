package pl.ptemich.ksef.local;

import org.springframework.stereotype.Service;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.util.InvoicesConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class LocalInvoiceService {

    private final DiskOperationsService diskOperationsService;
    private final LocalInvoicesRepository localInvoicesRepository;

    public LocalInvoiceService(DiskOperationsService diskOperationsService, LocalInvoicesRepository localInvoicesRepository) {
        this.diskOperationsService = diskOperationsService;
        this.localInvoicesRepository = localInvoicesRepository;
    }

    public List<LocalInvoice> loadInvoices(LocalInvoicesFilter filter) {
        Set<String> localInvoiceFileIds = diskOperationsService.listLocalInvoices(InvoiceSource.GENERATED);
        Map<String, LocalInvoice> localInvoiceByFileId = localInvoicesRepository.findAllById(localInvoiceFileIds).stream()
                .collect(Collectors.toMap(LocalInvoice::getFileId, Function.identity()));

        List<LocalInvoice> addedInvoices = localInvoiceFileIds.stream()
                .filter(Predicate.not(localInvoiceByFileId::containsKey))
                .map(fileId -> {
                    byte[] invoiceContent = diskOperationsService.loadFromDisk(InvoiceSource.GENERATED, fileId);
                    Faktura invoice = InvoicesConverter.convert(invoiceContent);

                    LocalInvoice localInvoice = new LocalInvoice();
                    localInvoice.setFileId(fileId);
                    localInvoice.setSeller(invoice.getPodmiot1().getDaneIdentyfikacyjne().getNazwa());
                    localInvoice.setBuyer(invoice.getPodmiot2().getDaneIdentyfikacyjne().getNazwa());
                    localInvoice = localInvoicesRepository.save(localInvoice);
                    return localInvoice;
                })
                .toList();

        addedInvoices.forEach(localInvoice -> localInvoiceByFileId.put(localInvoice.getFileId(), localInvoice));

        return localInvoiceByFileId.values()
                .stream()
                .toList();
    }

}
