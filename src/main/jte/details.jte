@import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata
@import java.util.List
@import pl.ptemich.ksef.localconf.LocalConfig

@param LocalConfig localConfig
@param List<InvoiceMetadata> invoices

<!DOCTYPE html>
<html lang="en">
<meta charset="UTF-8">
<title>KSEF</title>
<meta name="viewport" content="width=device-width,initial-scale=1">
<link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/bulma@1.0.4/css/bulma.min.css"
>
<style>
</style>
<script src="https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js"></script>
<body>

<div>
    <h1 class="h1">AUTOMOBIL - KSEF</h1>
</div>

<div>
  @if(invoices != null)
      <table class="table">
          <thead>
            <tr>
                <th>Sprzedawca</th>
                <th>Data wystawienia</th>
                <th>Netto</th>
                <th>Vat</th>
                <th>Brutto</th>
                <th>Numer KSEF</th>
                <th></th>
            </tr>
          </thead>
        @for(InvoiceMetadata invoice : invoices)
          <tr>
              <td>${invoice.getSeller().getName()}</td>
              <td>${invoice.getIssueDate().toString()}</td>
              <td>${invoice.getNetAmount()}</td>
              <td>${invoice.getVatAmount()}</td>
              <td>${invoice.getGrossAmount()}</td>
              <td><a href="/details/${invoice.getKsefNumber()}">${invoice.getKsefNumber()}</a></td>
              <td><a href="/download/xml/${invoice.getKsefNumber()}">XML</a></td>
          </tr>
        @endfor
      </table>
  @endif
</div>

<div>
    <p><span class="has-text-weight-bold">Scieżka exportu:</span> ${localConfig.getExportPath()}</p>
    <p><span class="has-text-weight-bold">Token:</span> ${localConfig.getKsefToken()}</p>
</div>

</body>
</html>