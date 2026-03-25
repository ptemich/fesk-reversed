package pl.ptemich.ksef.local;

import java.time.OffsetDateTime;

public record LocalInvoicesFilter(OffsetDateTime from, OffsetDateTime to) {

}
