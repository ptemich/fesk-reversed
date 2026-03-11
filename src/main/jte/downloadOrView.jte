@param boolean availableLocally
@param String ksefNumber


@if(availableLocally)
    <a href="/details/acard/${ksefNumber}" target="_blank">
        <span class="button is-success has-text-white is-size-7">POKAZ</span>
    </a>
@else
    <span hx-get="/copy/${ksefNumber}"
          hx-target="#tr_${ksefNumber}"
          hx-swap="innerHTML"
          class="button is-black is-size-7">POBIERZ</span>
@endif
