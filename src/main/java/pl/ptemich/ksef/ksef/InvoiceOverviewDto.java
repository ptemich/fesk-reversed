package pl.ptemich.ksef.ksef;

import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;

import java.time.LocalDate;

public record InvoiceOverviewDto(
        String ksefNumber,
        String sellerName,
        Double netAmount,
        Double vatAmount,
        Double grossAmount,
        LocalDate issueDate
) {

    public static InvoiceOverviewDto fromKsefInvoiceMetadata(InvoiceMetadata invoiceMetadata) {
        return new InvoiceOverviewDto(
                invoiceMetadata.getKsefNumber(),
                invoiceMetadata.getSeller().getName(),
                invoiceMetadata.getNetAmount(),
                invoiceMetadata.getVatAmount(),
                invoiceMetadata.getGrossAmount(),
                invoiceMetadata.getIssueDate()
        );
    }

    public static InvoiceOverviewDto fromKsefInvoice(String ksefNumber, Faktura invoice) {
        return new InvoiceOverviewDto(
                ksefNumber,
                invoice.getPodmiot1().getDaneIdentyfikacyjne().getNazwa(),
                invoice.getFa().getP131().doubleValue(),
                invoice.getFa().getP141().doubleValue(),
                invoice.getFa().getP15().doubleValue(),
                invoice.getFa().getP1().toGregorianCalendar().toZonedDateTime().toLocalDate()
        );
    }
}
