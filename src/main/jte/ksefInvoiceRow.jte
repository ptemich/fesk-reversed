@import pl.ptemich.ksef.ksef.InvoiceOverviewDto
@import pl.ptemich.ksef.local.InvoiceSource

@param InvoiceOverviewDto invoice
@param boolean availableLocally

!{
    String rowClass = invoice.invoiceSource() == InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY ? "has-background-grey-lighter" : "";
}

<tr class="${rowClass} hiddenByDefault" hidden="${invoice.invoiceSource() == InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY}">
    <td>${invoice.sellerName()}</td>
    <td>${invoice.buyerName()}</td>
    <td class="has-text-centered">${invoice.issueDate().toString()}</td>
    <td class="has-text-right">${String.format("%.2f", invoice.netAmount())} zł</td>
    <td class="has-text-right">${String.format("%.2f", invoice.vatAmount())} zł</td>
    <td class="has-text-right">${String.format("%.2f", invoice.grossAmount())} zł</td>
    <td>
        ${invoice.ksefNumber()}
    </td>
    <td class="has-text-centered" id="tr_${invoice.ksefNumber()}">
        @template.downloadOrView(availableLocally = availableLocally, ksefNumber = invoice.ksefNumber(), invoiceSource = invoice.invoiceSource())
    </td>

    <td class="has-text-centered">
        <a href="/details/ksef/${invoice.ksefNumber()}" target="_blank">
            <span class="button is-success has-text-white is-size-7">POKAZ</span>
        </a>
    </td>

    <td class="has-text-centered">
        <a href="/download/xml/${invoice.ksefNumber()}" target="_blank">
            <span class="button is-success has-text-white is-size-7">XML</span>
        </a>
    </td>
</tr>