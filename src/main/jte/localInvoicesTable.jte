
@import pl.ptemich.ksef.local.LocalInvoice
@import java.util.List

@param List<LocalInvoice> localInvoices


<div id="invoices">
<%--    <div>--%>
<%--        <p class="pl-2">Pobrano ${invoicesPackage.getFormatedTime()}</p>--%>
<%--        <span hx-get="/reload"--%>
<%--              hx-target="#invoices"--%>
<%--              hx-swap="outerHTML"--%>
<%--              class="button is-black ml-2">--%>
<%--            ODŚWIEŻ--%>
<%--        </span>--%>
<%--    </div>--%>

    <table class="table is-striped is-fullwidth">
        <thead>
        <tr>
            <th>Sprzedawca</th>
            <th>Nabywca</th>
            <th>Nr fakt.</th>
            <th>Data wystawienia</th>
            <th>KSEF</th>
            <th>Plik</th>
        </tr>
        </thead>
        @for(LocalInvoice invoice : localInvoices)
            @template.localInvoiceRow(invoice = invoice)
        @endfor
    </table>

</div>