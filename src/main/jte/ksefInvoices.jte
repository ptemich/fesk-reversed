@import pl.ptemich.ksef.localconf.LocalConfig
@import pl.ptemich.ksef.ksef.KsefInvoicesPackage
@import java.util.Set

@param LocalConfig localConfig
@param KsefInvoicesPackage ksefInvoicesPackage
@param Set<String> localInvoices
@param Set<String> localInvoiceConfirmations

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

<a href="/local">
    <span class="button is-link ml-2">
    WYSYŁANIE
    </span>
</a>

@template.ksefInvoicesTable(
    ksefInvoicesPackage = ksefInvoicesPackage,
    localInvoices = localInvoices,
    localInvoiceConfirmations = localInvoiceConfirmations
)

</body>
</html>