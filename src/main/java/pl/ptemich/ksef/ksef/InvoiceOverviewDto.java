package pl.ptemich.ksef.ksef;

import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;
import pl.ptemich.ksef.local.InvoiceSource;

import java.time.LocalDate;

public record InvoiceOverviewDto(
        String ksefNumber,
        String sellerName,
        String buyerName,
        Double netAmount,
        Double vatAmount,
        Double grossAmount,
        LocalDate issueDate,
        InvoiceSource invoiceSource
) {

    public static InvoiceOverviewDto fromKsefInvoiceMetadata(InvoiceMetadata invoiceMetadata, InvoiceSource invoiceSource) {
        return new InvoiceOverviewDto(
                invoiceMetadata.getKsefNumber(),
                invoiceMetadata.getSeller().getName(),
                invoiceMetadata.getBuyer().getName(),
                invoiceMetadata.getNetAmount(),
                invoiceMetadata.getVatAmount(),
                invoiceMetadata.getGrossAmount(),
                invoiceMetadata.getIssueDate(),
                invoiceSource
        );
    }

}
