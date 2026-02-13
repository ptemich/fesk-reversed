@import pl.gov.crd.wzor._2025._06._25._13775.Faktura

@param String ksefNumber
@param Faktura invoice

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
    ${ksefNumber}
    ${invoice.getNaglowek().getDataWytworzeniaFa().toString()}

    <table class="table is-striped is-fullwidth">
        <thead>
            <tr>
                <th>Nr. wiersza</th>
                <th>Nazwa</th>
                <th class="has-text-centered">Index</th>
                <th class="has-text-centered">Jednostka</th>
                <th class="has-text-centered">Ilość</th>
                <th class="has-text-right">Cena jednostki</th>
                <th class="has-text-right">Cena razem</th>
            </tr>
        </thead>
        <tbody>
            @for(var wierszFaktury : invoice.getFa().getFaWiersz())
                <tr>
                    <td>${wierszFaktury.getNrWierszaFa()}</td>
                    <td>${wierszFaktury.getP7()}</td>
                    <td class="has-text-centered">${wierszFaktury.getIndeks()}</td>
                    <td class="has-text-centered">${wierszFaktury.getP8A()}</td>
                    <td class="has-text-centered">${wierszFaktury.getP8B()}</td>
                    <td class="has-text-right">${String.format("%.2f",wierszFaktury.getP9A())} zł</td>
                    <td class="has-text-right">${String.format("%.2f",wierszFaktury.getP11())} zł</td>
                </tr>
            @endfor
        </tbody>
    </table>
</div>

</body>
</html>