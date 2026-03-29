@import pl.ptemich.ksef.local.LocalInvoice
@import java.time.format.DateTimeFormatter

@param LocalInvoice invoice

<tr>
    <td>${invoice.getSeller()}</td>
    <td>${invoice.getBuyer()}</td>
    <td>${invoice.getInvoiceNumber()}</td>
    <td>
        !{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dataWystawienia = formatter.format(invoice.getGeneratedOn());
        }
        ${dataWystawienia}
    </td>
    <td id="lcl_${invoice.getFileId()}">
        @template.sendOrNumber(invoice = invoice)
    </td>
    <td>${invoice.getFileId()}</td>
</tr>