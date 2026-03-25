package pl.ptemich.ksef.ksef;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record InvoicesPackage(
        OffsetDateTime loadedOn,
        List<InvoiceOverviewDto> invoices
) {

    public String getFormatedTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm");
        return dtf.format(loadedOn);
    }

}
