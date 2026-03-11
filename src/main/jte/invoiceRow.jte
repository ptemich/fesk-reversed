@import pl.ptemich.ksef.ksef.InvoiceOverviewDto

@param InvoiceOverviewDto invoice
@param boolean availableLocally

<tr id="invoice_${invoice.ksefNumber()}">
    <td>${invoice.sellerName()}</td>
    <td class="has-text-centered">${invoice.issueDate().toString()}</td>
    <td class="has-text-right">${String.format("%.2f", invoice.netAmount())} zł</td>
    <td class="has-text-right">${String.format("%.2f", invoice.vatAmount())} zł</td>
    <td class="has-text-right">${String.format("%.2f", invoice.grossAmount())} zł</td>
    <td>
        ${invoice.ksefNumber()}
    </td>
    <td class="has-text-centered">
        @if(availableLocally)
            <a href="/details/acard/${invoice.ksefNumber()}" target="_blank">
                <span class="button is-success has-text-white is-size-7">POKAZ</span>
            </a>
        @else
            <span hx-get="/copy/${invoice.ksefNumber()}"
                  hx-target="#invoice_${invoice.ksefNumber()}"
                  hx-swap="outerHTML"
                  class="button is-black is-size-7">POBIERZ</span>
        @endif
    </td>

    <td class="has-text-centered">
        <%--                            <a href="/download/xml/${invoice.getKsefNumber()}">--%>
        <%--                                <span class="button is-info is-size-7">POBIEZ</span>--%>
        <%--                            </a>--%>
        <a href="/details/ksef/${invoice.ksefNumber()}" target="_blank">
            <span class="button is-success has-text-white is-size-7">POKAZ</span>
        </a>
    </td>
</tr>