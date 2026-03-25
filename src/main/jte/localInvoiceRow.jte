@import pl.ptemich.ksef.local.LocalInvoice

@param LocalInvoice invoice

<tr>
    <td>${invoice.getSeller()}</td>
    <td>${invoice.getBuyer()}</td>
<%--    <td class="has-text-centered">${invoice.issueDate().toString()}</td>--%>
<%--    <td class="has-text-right">${String.format("%.2f", invoice.netAmount())} zł</td>--%>
<%--    <td class="has-text-right">${String.format("%.2f", invoice.vatAmount())} zł</td>--%>
<%--    <td class="has-text-right">${String.format("%.2f", invoice.grossAmount())} zł</td>--%>
<%--    <td>--%>
<%--        ${invoice.ksefNumber()}--%>
<%--    </td>--%>
<%--    <td class="has-text-centered" id="tr_${invoice.ksefNumber()}">--%>
<%--        -----%>
<%--    </td>--%>

<%--    <td class="has-text-centered">--%>
<%--        <a href="/details/ksef/${invoice.ksefNumber()}" target="_blank">--%>
<%--            <span class="button is-success has-text-white is-size-7">POKAZ</span>--%>
<%--        </a>--%>
<%--    </td>--%>
</tr>