@import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata
@import pl.ptemich.ksef.ksef.InvoicesPackage
@import java.util.Set

@param InvoicesPackage invoicesPackage
@param Set<String> acardInvoices

<div id="invoices">
    @if(invoicesPackage != null)
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
                    <th class="has-text-centered">AKORD</th>
                    <th class="has-text-centered">KSEF</th>
                </tr>
                </thead>
                @for(InvoiceMetadata invoice : invoicesPackage.invoices())
                    <tr>
                        <td>${invoice.getSeller().getName()}</td>
                        <td class="has-text-centered">${invoice.getIssueDate().toString()}</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getNetAmount())} zł</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getVatAmount())} zł</td>
                        <td class="has-text-right">${String.format("%.2f", invoice.getGrossAmount())} zł</td>
                        <td>
                            ${invoice.getKsefNumber()}
                        </td>
                        <td class="has-text-centered">
                            @if(acardInvoices.contains(invoice.getKsefNumber()))
                                <a href="/details/acard/${invoice.getKsefNumber()}" target="_blank">
                                    <span class="button is-success has-text-white is-size-7">POKAZ</span>
                                </a>
                            @else
                                <span hx-get="/copy/${invoice.getKsefNumber()}"
                                      hx-target="#invoices"
                                      hx-swap="outerHTML"
                                      class="button is-black is-size-7">POBIERZ</span>
                            @endif
                        </td>

                        <td class="has-text-centered">
<%--                            <a href="/download/xml/${invoice.getKsefNumber()}">--%>
<%--                                <span class="button is-info is-size-7">POBIEZ</span>--%>
<%--                            </a>--%>
                            <a href="/details/ksef/${invoice.getKsefNumber()}" target="_blank">
                                <span class="button is-success has-text-white is-size-7">POKAZ</span>
                            </a>
                        </td>
                    </tr>
                @endfor
            </table>
        @endif

        <p class="pl-2">Pobrano ${invoicesPackage.getFormatedTime()}</p>
    @endif
    <span hx-get="/reload"
          hx-target="#invoices"
          hx-swap="outerHTML"
          class="button is-black ml-2">
        ODŚWIEŻ
    </span>
</div>