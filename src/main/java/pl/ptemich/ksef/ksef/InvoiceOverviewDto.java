package pl.ptemich.ksef.ksef;

import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;

import java.time.LocalDate;

public record InvoiceOverviewDto(
        String ksefNumber,
        String sellerName,
        String buyerName,
        Double netAmount,
        Double vatAmount,
        Double grossAmount,
        LocalDate issueDate
) {

    public static InvoiceOverviewDto fromKsefInvoiceMetadata(InvoiceMetadata invoiceMetadata) {
        return new InvoiceOverviewDto(
                invoiceMetadata.getKsefNumber(),
                invoiceMetadata.getSeller().getName(),
                invoiceMetadata.getBuyer().getName(),
                invoiceMetadata.getNetAmount(),
                invoiceMetadata.getVatAmount(),
                invoiceMetadata.getGrossAmount(),
                invoiceMetadata.getIssueDate()
        );
    }

}
