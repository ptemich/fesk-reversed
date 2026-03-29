@import pl.ptemich.ksef.ksef.InvoiceOverviewDto

@param InvoiceOverviewDto invoice
@param boolean availableLocally

<tr>
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
        @template.downloadOrView(availableLocally = availableLocally, ksefNumber = invoice.ksefNumber())
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