@import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata
@import pl.ptemich.ksef.ksef.InvoicesPackage
@import java.util.Set

@param InvoicesPackage invoicesPackage
@param Set<String> acardInvoices

<div id="invoices">
    @if(invoicesPackage != null)
        <p class="pl-2">Lista pobrana o: ${invoicesPackage.getFormatedTime()}</p>
        @if(invoicesPackage.invoices() != null)
            <table class="table is-striped is-fullwidth">
                <thead>
                <tr>
                    <th>Sprzedawca</th>
                    <th class="has-text-centered">Data wystawienia</th>
                    <th class="has-text-right">Netto</th>
                    <th class="has-text-right">Vat</th>
                    <th class="has-text-right">Brutto</th>
                    <th>Numer KSEF</th>
                    <th class="has-text-centered">ACARD</th>
                    <th></th>
                </tr>
                </thead>
                @for(InvoiceMetadata invoice : invoicesPackage.invoices())
                    <tr>
                        <td>${invoice.getSeller().getName()}</td>
                        <td class="has-text-centered">${invoice.getIssueDate().toString()}</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getNetAmount())} zł</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getVatAmount())} zł</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getGrossAmount())} zł</td>
                        <td>${invoice.getKsefNumber()}</td>
                        <td class="has-text-centered">
                            @if(acardInvoices.contains(invoice.getKsefNumber()))
                                <a href="/details/acard/${invoice.getKsefNumber()}" target="_blank">✅</a>
                            @else
                                <span hx-get="/copy/${invoice.getKsefNumber()}"
                                      hx-target="#invoices"
                                      hx-swap="outerHTML"
                                      class="button is-black is-size-7">KOPIUJ</span>
                            @endif
                        </td>
                        <td><a href="/download/xml/${invoice.getKsefNumber()}">XML</a></td>
                    </tr>
                @endfor
            </table>
        @endif
    @endif
    <span hx-get="/reload"
          hx-target="#invoices"
          hx-swap="outerHTML"
          class="button is-black ml-2">
        ODŚWIERZ
    </span>
</div>