package pl.ptemich.ksef.ksef;

import java.time.OffsetDateTime;

public record InvoicesFilter(OffsetDateTime from, OffsetDateTime to) {

}
