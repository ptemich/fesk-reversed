package pl.ptemich.ksef.ksef;

import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record InvoicesPackage(OffsetDateTime loadedOn, List<InvoiceMetadata> invoices) {

    public String getFormatedTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm");
        return dtf.format(loadedOn);
    }

}
