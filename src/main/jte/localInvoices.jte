@import pl.ptemich.ksef.local.LocalInvoicesPackage
@import pl.ptemich.ksef.localconf.LocalConfig
@import java.util.Set

@param LocalConfig localConfig
@param LocalInvoicesPackage invoicesPackage
@param Set<String> downloadedInvoices

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
    <h1 class="is-size-1 pl-2 has-text-weight-bold">AUTOMOBIL - KSEF</h1>
</div>

<a href="/">
    <span class="button is-link ml-2">
    POBIERANIE
    </span>
</a>

@template.localInvoicesTable(invoicesPackage = invoicesPackage, downloadedInvoices = downloadedInvoices)

</body>
</html>