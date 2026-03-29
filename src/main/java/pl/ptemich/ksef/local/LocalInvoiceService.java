package pl.ptemich.ksef.local;

import org.springframework.stereotype.Service;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.ksef.KsefUploadResultDto;
import pl.ptemich.ksef.util.InvoicesConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class LocalInvoiceService {

    private final DiskOperationsService diskOperationsService;
    private final LocalInvoicesRepository localInvoicesRepository;
    private final AuthorizedKsefService authorizedKsefService;

    public LocalInvoiceService(
            DiskOperationsService diskOperationsService,
            LocalInvoicesRepository localInvoicesRepository,
            AuthorizedKsefService authorizedKsefService
    ) {
        this.diskOperationsService = diskOperationsService;
        this.localInvoicesRepository = localInvoicesRepository;
        this.authorizedKsefService = authorizedKsefService;
    }

    public LocalInvoicesPackage loadInvoices(LocalInvoicesFilter filter) {
        Set<String> localInvoiceFileIds = diskOperationsService.listLocalInvoices(InvoiceSource.LOCAL_TO_KSEF);
        Map<String, LocalInvoice> localInvoiceByFileId = localInvoicesRepository.findAllById(localInvoiceFileIds).stream()
                .collect(Collectors.toMap(LocalInvoice::getFileId, Function.identity()));

        List<LocalInvoice> addedInvoices = localInvoiceFileIds.stream()
                .filter(Predicate.not(localInvoiceByFileId::containsKey))
                .map(fileId -> {
                    byte[] invoiceContent = diskOperationsService.loadFromDisk(InvoiceSource.LOCAL_TO_KSEF, fileId);
                    Faktura invoice = InvoicesConverter.convert(invoiceContent);

                    LocalInvoice localInvoice = new LocalInvoice();
                    localInvoice.setFileId(fileId);
                    localInvoice.setInvoiceNumber(invoice.getFa().getP2());
                    localInvoice.setGeneratedOn(invoice.getFa().getP1().toGregorianCalendar().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    localInvoice.setSeller(invoice.getPodmiot1().getDaneIdentyfikacyjne().getNazwa());
                    localInvoice.setBuyer(invoice.getPodmiot2().getDaneIdentyfikacyjne().getNazwa());
                    localInvoice = localInvoicesRepository.save(localInvoice);
                    return localInvoice;
                })
                .toList();

        addedInvoices.forEach(localInvoice -> localInvoiceByFileId.put(localInvoice.getFileId(), localInvoice));

        List<LocalInvoice> localInvoices = localInvoiceByFileId.values()
                .stream()
                .sorted(Comparator.comparing(LocalInvoice::getGeneratedOn).reversed())
                .toList();

        return new LocalInvoicesPackage(OffsetDateTime.now(), localInvoices);
    }

    public LocalInvoice sendToKsef(String fileId) {
        LocalInvoice localInvoice = localInvoicesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Ksef upload failed"));

        byte[] invoiceContent = diskOperationsService.loadFromDisk(InvoiceSource.LOCAL_TO_KSEF, fileId);
        KsefUploadResultDto ksefUploadResultDto = authorizedKsefService.sendInvoice(invoiceContent);

        localInvoice.setProcessingCode(ksefUploadResultDto.processingCode());
        localInvoice.setProcessingDescription(ksefUploadResultDto.processingCodeDescription());
        localInvoice.setKsefNumber(ksefUploadResultDto.ksefNumber());

        localInvoice = localInvoicesRepository.save(localInvoice);

        if (ksefUploadResultDto.ksefNumber() != null) {
            byte[] bytes = authorizedKsefService.loadInvoiceXml(ksefUploadResultDto.ksefNumber());
            diskOperationsService.saveToDisk(InvoiceSource.KSEF_TO_LOCAL_PROCESSED, ksefUploadResultDto.ksefNumber(), bytes);
            diskOperationsService.saveToDisk(InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY, ksefUploadResultDto.ksefNumber(), bytes);
        }

        return localInvoice;
    }
}
