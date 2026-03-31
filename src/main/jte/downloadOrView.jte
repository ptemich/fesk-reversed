@import pl.ptemich.ksef.local.InvoiceSource

@param boolean availableLocally
@param String ksefNumber
@param InvoiceSource invoiceSource

@if(availableLocally)
    <a href="/details/local/${ksefNumber}/${invoiceSource}" target="_blank">
        <span class="button is-success has-text-white is-size-7">POKAZ</span>
    </a>
@else
    @if(invoiceSource != InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY)
        <span hx-get="/copy/${ksefNumber}"
              hx-target="#tr_${ksefNumber}"
              hx-swap="innerHTML"
              class="button is-black is-size-7">POBIERZ</span>
    @endif
@endif
