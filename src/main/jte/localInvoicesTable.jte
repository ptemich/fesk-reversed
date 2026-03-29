@import pl.ptemich.ksef.local.LocalInvoice
@import pl.ptemich.ksef.local.LocalInvoicesPackage
@import java.util.Set

@param LocalInvoicesPackage invoicesPackage
@param Set<String> downloadedInvoices

<div id="invoices">
    <div>
        <p class="pl-2">Pobrano ${invoicesPackage.getFormatedTime()}</p>
        <span hx-get="/reloadLocal"
              hx-target="#invoices"
              hx-swap="outerHTML"
              class="button is-black ml-2">
            ODŚWIEŻ
        </span>
    </div>

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
        @for(LocalInvoice invoice : invoicesPackage.invoices())
            @template.localInvoiceRow(invoice = invoice, availableLocally = downloadedInvoices.contains(invoice.getKsefNumber()))
        @endfor
    </table>

</div>