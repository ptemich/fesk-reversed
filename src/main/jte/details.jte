@import pl.gov.crd.wzor._2025._06._25._13775.Faktura
@import java.math.BigDecimal
@import java.text.SimpleDateFormat


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
    <h1 class="is-size-2 pl-2 has-text-weight-bold">${ksefNumber}</h1>
    <p class="is-size-3 pl-2">${invoice.getFa().getP2()}</p>
    !{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dataWystawienia = sdf.format(invoice.getNaglowek().getDataWytworzeniaFa().toGregorianCalendar().getTime());
    }
    <p class="is-size-4 pl-2">Data wystawienia: ${dataWystawienia}</p>
    !{
        var rachunekBankowy = invoice.getFa().getPlatnosc().getRachunekBankowy().getFirst();
    }
    <p class="is-size-4 pl-2">${rachunekBankowy.getNrRB()} - ${rachunekBankowy.getOpisRachunku()}</p>


    <table class="table is-fullwidth">
        <tbody>
            <tr>
                <td colspan="2">NABYWCA</td>
                <td colspan="2">ODBIORCA</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold">NIP</td>
                <td>${invoice.getPodmiot1().getDaneIdentyfikacyjne().getNIP()}</td>

                <td class="has-text-weight-bold">NIP</td>
                <td>${invoice.getPodmiot2().getDaneIdentyfikacyjne().getNIP()}</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold">Nazwa</td>
                <td>${invoice.getPodmiot1().getDaneIdentyfikacyjne().getNazwa()}</td>

                <td class="has-text-weight-bold">Nazwa</td>
                <td>${invoice.getPodmiot2().getDaneIdentyfikacyjne().getNazwa()}</td>
            </tr>
        </tbody>
    </table>

    <table class="table is-striped is-fullwidth">
        <thead>
            <tr>
                <th>Nr. wiersza</th>
                <th>Nazwa</th>
                <th class="has-text-centered">Index</th>
                <th class="has-text-centered">Jednostka</th>
                <th class="has-text-centered">Ilość</th>
                <th class="has-text-right">Cena jednostki</th>
                <th class="has-text-right">Stawka VAT</th>
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
                    <td class="has-text-right">${wierszFaktury.getP12()}%</td>
                    <td class="has-text-right">${String.format("%.2f",wierszFaktury.getP11())} zł</td>
                </tr>
            @endfor
        </tbody>
    </table>

    <table class="table mx-auto">
        <tbody>
            <tr class="is-size-4">
                !{
                    var terminPlatnosci = invoice.getFa().getPlatnosc().getTerminPlatnosci().getFirst();
                 }
                <td colspan="2">Termin zapłaty</td>
                <td colspan="5" class="has-text-right has-text-weight-bold">${terminPlatnosci.getTermin().toString()}</td>
            </tr>

            <tr>
                <td>NETTO</td>
                <td class="has-text-right">${invoice.getFa().getP131()} zł</td>

                <td>VAT</td>
                <td class="has-text-right">${invoice.getFa().getP141()} zł</td>
                <td colspan="3"></td>
            </tr>

            <tr>
                <td>NETTO</td>
                <td class="has-text-right">${invoice.getFa().getP132()} zł</td>

                <td>VAT</td>
                <td class="has-text-right">${invoice.getFa().getP142()} zł</td>

                <td colspan="3"></td>
            </tr>

            <tr>
                !{
                    BigDecimal sumaNetto = invoice.getFa().getP131().add(invoice.getFa().getP132());
                    BigDecimal sumaVat = invoice.getFa().getP141().add(invoice.getFa().getP142());
                 }
                <td>SUMA</td>
                <td class="has-text-right">${sumaNetto} zł</td>
                <td></td>
                <td class="has-text-right">${sumaVat} zł</td>
                <td class="has-text-right has-text-weight-bold">${invoice.getFa().getP15()} zł</td>
            </tr>
        </tbody>
    </table>
</div>

</body>
</html>