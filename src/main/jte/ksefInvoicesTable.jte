@import pl.ptemich.ksef.ksef.InvoiceOverviewDto
@import pl.ptemich.ksef.ksef.InvoicesPackage
@import java.util.Set

@param InvoicesPackage invoicesPackage
@param Set<String> localInvoices


<div id="invoices">
    <div>
        <p class="pl-2">Pobrano ${invoicesPackage.getFormatedTime()}</p>
        <span hx-get="/reload"
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
            <th class="has-text-centered">Data wystawienia</th>
            <th class="has-text-right">Netto</th>
            <th class="has-text-right">Vat</th>
            <th class="has-text-right">Brutto</th>
            <th>Numer KSEF</th>
            <th class="has-text-centered">AKORD</th>
            <th class="has-text-centered">KSEF</th>
        </tr>
        </thead>
        @for(InvoiceOverviewDto invoice : invoicesPackage.invoices())
            @template.ksefInvoiceRow(invoice = invoice, availableLocally = localInvoices.contains(invoice.ksefNumber()))
        @endfor
    </table>

</div>