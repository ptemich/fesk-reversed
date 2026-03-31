@import pl.gov.crd.wzor._2025._06._25._13775.Faktura
@import pl.gov.crd.wzor._2025._06._25._13775.TKluczWartosc
@import java.math.BigDecimal
@import java.text.SimpleDateFormat
@import java.time.ZoneId
@import java.util.List


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
    @media print {
        .is-size-2, .is-size-3, .is-size-4, .is-size-5 {
            font-size: 16pt !important;
        }
        .is-size-5 {
            font-size: 10pt !important;
        }
        table {
            font-size: 8pt !important;
        }
    }
</style>
<script src="https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js"></script>
<body>

<div>
    <div class="is-flex">
        <div style="width:80%">
            <h1 class="is-size-2 pl-2"><span style="color: grey;">KSEF:</span> <span class="has-text-weight-bold">${ksefNumber}</span></h1>
            <p class="is-size-3 pl-2"><span style="color: grey;">Numer faktury:</span> <span class="has-text-weight-bold">${invoice.getFa().getP2()}</span></p>
        </div>

        <div style="width:20%" class="ml-auto">
            !{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dataWystawienia = sdf.format(invoice.getNaglowek().getDataWytworzeniaFa().toGregorianCalendar().getTime());
            }
            <p class="is-size-5 pl-2 pl-2">Data wystawienia: <br/>  ${dataWystawienia}</p>
        </div>
    </div>

    <table class="table is-fullwidth">
        <tbody>
            <tr>
                <td colspan="2" class="is-size-5">SPRZEDAWCA</td>
                <td colspan="2" class="is-size-5">NABYWCA</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold">NIP</td>
                <td class="is-size-5">${invoice.getPodmiot1().getDaneIdentyfikacyjne().getNIP()}</td>

                <td class="has-text-weight-bold">NIP</td>
                <td class="is-size-5">${invoice.getPodmiot2().getDaneIdentyfikacyjne().getNIP()}</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold">Nazwa</td>
                <td class="is-size-5">${invoice.getPodmiot1().getDaneIdentyfikacyjne().getNazwa()}</td>

                <td class="has-text-weight-bold">Nazwa</td>
                <td class="is-size-5">${invoice.getPodmiot2().getDaneIdentyfikacyjne().getNazwa()}</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold">Adres</td>
                <td class="is-size-5">${invoice.getPodmiot1().getAdres().getAdresL1()}</td>

                <td class="has-text-weight-bold">Adres</td>
                <td class="is-size-5">${invoice.getPodmiot2().getAdres().getAdresL1()}</td>
            </tr>

            <tr>
                <td class="has-text-weight-bold"></td>
                <td class="is-size-5">${invoice.getPodmiot1().getAdres().getAdresL2()}</td>

                <td class="has-text-weight-bold"></td>
                <td class="is-size-5">${invoice.getPodmiot2().getAdres().getAdresL2()}</td>
            </tr>
        </tbody>
    </table>

    <table class="table is-striped is-fullwidth">
        <thead>
            <tr>
                <th>Lp.</th>
                <th>Nazwa</th>
                <th class="has-text-left">Symbol</th>
                <th class="has-text-left">Dodatkowe uwagi</th>
                <th class="has-text-centered">JM</th>
                <th class="has-text-centered">Ilość</th>
                <th class="has-text-right">Cena jednostkowa</th>
                <th class="has-text-right">Stawka VAT</th>
                <th class="has-text-right">Cena razem</th>
            </tr>
        </thead>
        <tbody>
            @for(var wierszFaktury : invoice.getFa().getFaWiersz())
                <tr>
                    <td>${wierszFaktury.getNrWierszaFa()}</td>
                    <td>${wierszFaktury.getP7()}</td>
                    <td class="has-text-left">${wierszFaktury.getIndeks()}</td>
                    <td class="has-text-left">
                        !{
                            List<TKluczWartosc> dodatkoweUwagi = invoice.getFa().getDodatkowyOpis().stream()
                            .filter(uwaga -> uwaga.getNrWiersza() != null)
                            .filter(uwaga -> uwaga.getNrWiersza().equals(wierszFaktury.getNrWierszaFa()))
                            .toList();
                        }
                        <ul>
                            @for(var uwaga : dodatkoweUwagi)
                                <li>
                                    ${uwaga.getKlucz()}:
                                    @if(uwaga.getWartosc().startsWith("http"))
                                        <a href="${uwaga.getWartosc()}" target="_blank">${uwaga.getWartosc()}</a>
                                    @else
                                        ${uwaga.getWartosc()}
                                    @endif
                                </li>
                            @endfor
                        </ul>

                    </td>
                    <td class="has-text-centered">${wierszFaktury.getP8A()}</td>
                    <td class="has-text-centered">${wierszFaktury.getP8B()}</td>
                    <td class="has-text-right">
                        @if(wierszFaktury.getP9A() != null)
                            ${String.format("%.2f",wierszFaktury.getP9A())} (NETTO)
                        @else
                            ${String.format("%.2f",wierszFaktury.getP9B())} (BRUTTO)
                        @endif
                    </td>
                    <td class="has-text-right">${wierszFaktury.getP12()}%</td>
                    <td class="has-text-right">
                        @if(wierszFaktury.getP11() != null)
                            ${String.format("%.2f",wierszFaktury.getP11())} (NETTO)
                        @else
                            ${String.format("%.2f",wierszFaktury.getP11A())} (BRUTTO)
                        @endif
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>

    <table class="table mx-auto">
        <tbody>
            @if(invoice.getFa().getPlatnosc() != null && invoice.getFa().getPlatnosc().getDataZaplaty() != null)
                <tr class="is-size-4">
                    <td colspan="2">Zapłacono</td>
                    <td colspan="5" class="has-text-right has-text-weight-bold">${invoice.getFa().getPlatnosc().getDataZaplaty().toString()}</td>
                </tr>
            @endif

            <tr>
                <td>NETTO</td>
                <td class="has-text-right">${String.format("%.2f", invoice.getFa().getP131())} zł</td>

                <td>VAT (23%)</td>
                <td class="has-text-right">${String.format("%.2f", invoice.getFa().getP141())} zł</td>
                <td colspan="4"></td>
            </tr>

            @if(invoice.getFa().getP132() != null)
                <tr>
                    <td>NETTO</td>
                    <td class="has-text-right">${String.format("%.2f", invoice.getFa().getP132())} zł</td>

                    <td>VAT (8%)</td>
                    <td class="has-text-right">${String.format("%.2f", invoice.getFa().getP142())} zł</td>

                    <td colspan="4"></td>
                </tr>
            @endif

            <tr>
                !{
                    BigDecimal sumaNetto = invoice.getFa().getP131();
                    if (invoice.getFa().getP132() != null) {
                        sumaNetto = invoice.getFa().getP131().add(invoice.getFa().getP132());
                    }

                    BigDecimal sumaVat = invoice.getFa().getP141();
                    if (invoice.getFa().getP142() != null) {
                        sumaVat = invoice.getFa().getP141().add(invoice.getFa().getP142());
                    }
                 }
                <td>SUMA</td>
                <td class="has-text-right">${String.format("%.2f", sumaNetto)} zł</td>
                <td></td>
                <td class="has-text-right">${String.format("%.2f", sumaVat)} zł</td>
                <td>BRUTTO</td>
                <td class="has-text-right has-text-weight-bold">${String.format("%.2f", invoice.getFa().getP15())} zł</td>
            </tr>
        </tbody>
    </table>

    @if(invoice.getFa().getPlatnosc().getZaplataCzesciowa() != null && !invoice.getFa().getPlatnosc().getZaplataCzesciowa().isEmpty())
        <table class="table mx-auto">
            <thead>
                <tr>
                    <th colspan="2">Płatność częściowa</th>
                </tr>
                <tr>
                    <th>Kwota</th>
                    <th>Data</th>
                </tr>
            </thead>
            <tbody>
            @for(var zaplataCzesciowa : invoice.getFa().getPlatnosc().getZaplataCzesciowa())
                <tr>
                    <td>${zaplataCzesciowa.getKwotaZaplatyCzesciowej()} zł</td>
                    <td>${zaplataCzesciowa.getDataZaplatyCzesciowej().toString()}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    @endif

    @if(invoice.getFa().getPlatnosc() != null && invoice.getFa().getPlatnosc().getRachunekBankowy() != null && !invoice.getFa().getPlatnosc().getRachunekBankowy().isEmpty())
        !{
            var rachunekBankowy = invoice.getFa().getPlatnosc().getRachunekBankowy().getFirst();
        }
        <p class="is-size-4 pl-2 has-text-centered">Numer konta: ${rachunekBankowy.getNrRB()} - ${rachunekBankowy.getNazwaBanku()} ${rachunekBankowy.getOpisRachunku()}</p>
    @endif

    @if(invoice.getFa().getPlatnosc() != null && invoice.getFa().getPlatnosc().getTerminPlatnosci() != null && !invoice.getFa().getPlatnosc().getTerminPlatnosci().isEmpty())
        <p class="is-size-4 has-text-centered">
            !{
                var terminPlatnosci = invoice.getFa().getPlatnosc().getTerminPlatnosci().getFirst();
            }
            Termin zapłaty: <span class="has-text-weight-bold">${terminPlatnosci.getTermin().toString()}</span>
        </p>
    @endif

    !{
        List<TKluczWartosc> dodatkoweUwagiLista = invoice.getFa().getDodatkowyOpis().stream()
        .filter(uwaga -> uwaga.getNrWiersza() == null)
        .toList();
    }

    @if(!dodatkoweUwagiLista.isEmpty())
        <table class="table mx-auto">
            <thead>
                <tr>
                    <th colspan="2" class="has-text-centered">Dodatkowe uwagi</th>
                </tr>
            </thead>
            <tbody>
                @for(var uwaga : dodatkoweUwagiLista)
                    <tr>
                        <td class="has-text-weight-bold">${uwaga.getKlucz()}</td>
                        <td>${uwaga.getWartosc()}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
    @endif

</div>

</body>
</html>