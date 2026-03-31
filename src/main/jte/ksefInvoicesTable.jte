@import pl.ptemich.ksef.ksef.InvoiceOverviewDto
@import pl.ptemich.ksef.ksef.KsefInvoicesPackage
@import pl.ptemich.ksef.local.InvoiceSource
@import java.util.Set

@param KsefInvoicesPackage ksefInvoicesPackage
@param Set<String> localInvoices
@param Set<String> localInvoiceConfirmations


<div id="invoices">
    <div>
        <p class="pl-2">Pobrano ${ksefInvoicesPackage.getFormatedTime()}</p>
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
            <th class="has-text-centered">XML</th>
        </tr>
        </thead>
        @for(InvoiceOverviewDto invoice : ksefInvoicesPackage.invoices())
            @template.ksefInvoiceRow(
                invoice = invoice,
                availableLocally = invoice.invoiceSource() == InvoiceSource.KSEF_TO_LOCAL ? localInvoices.contains(invoice.ksefNumber()) : localInvoiceConfirmations.contains(invoice.ksefNumber())
            )
        @endfor
    </table>

    <div class="button is-black ml-2" onclick="Array.from(document.getElementsByClassName('hiddenByDefault')).forEach(e => e.removeAttribute('hidden'))">
        POKAZ POTWIERDZENIA
    </div>

</div>