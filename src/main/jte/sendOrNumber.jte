@import pl.ptemich.ksef.local.LocalInvoice

@param LocalInvoice invoice

@if(invoice.getProcessingCode() != 0)
    ${invoice.getProcessingCode()} / ${invoice.getProcessingDescription()} / ${invoice.getKsefNumber()}
@else
    <span hx-post="/send/${invoice.getFileId()}"
          hx-target="#lcl_${invoice.getFileId()}"
          hx-swap="innerHTML"
          class="button is-black is-size-7">WYSLIJ</span>
@endif


