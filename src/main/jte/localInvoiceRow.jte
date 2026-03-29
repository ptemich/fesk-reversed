@import pl.ptemich.ksef.local.LocalInvoice

@param LocalInvoice invoice

<tr>
    <td>${invoice.getSeller()}</td>
    <td>${invoice.getBuyer()}</td>
    <td id="lcl_${invoice.getFileId()}">
        @template.sendOrNumber(invoice = invoice)
    </td>
</tr>