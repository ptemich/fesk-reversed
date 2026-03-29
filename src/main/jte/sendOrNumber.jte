@import pl.ptemich.ksef.ksef.ProcessingStatusCodes
@import pl.ptemich.ksef.local.InvoiceSource
@import pl.ptemich.ksef.local.LocalInvoice

@param LocalInvoice invoice
@param boolean availableLocally

@if(invoice.getProcessingCode() != 0)
    @if(invoice.getProcessingCode() != ProcessingStatusCodes.SUCCESS)
        ${invoice.getProcessingCode()} / ${invoice.getProcessingDescription()}
    @else
        @if(invoice.getKsefNumber() != null)
            <a href="/details/local/${invoice.getKsefNumber()}/${InvoiceSource.KSEF_TO_LOCAL_PROCESSED_COPY}" target="_blank">
                <span class="button is-success has-text-white is-size-7">POKAZ</span>
            </a>
        @endif
    @endif
@else
    <span hx-post="/send/${invoice.getFileId()}"
          hx-target="#lcl_${invoice.getFileId()}"
          hx-swap="innerHTML"
          class="button is-black is-size-7">WYSLIJ</span>
@endif


