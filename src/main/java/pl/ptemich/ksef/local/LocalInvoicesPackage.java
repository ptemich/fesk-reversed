package pl.ptemich.ksef.local;



import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record LocalInvoicesPackage(
        OffsetDateTime loadedOn,
        List<LocalInvoice> invoices
) {

    public String getFormatedTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm");
        return dtf.format(loadedOn);
    }

}
