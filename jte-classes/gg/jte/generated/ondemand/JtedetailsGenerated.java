package gg.jte.generated.ondemand;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
@SuppressWarnings("unchecked")
public final class JtedetailsGenerated {
	public static final String JTE_NAME = "details.jte";
	public static final int[] JTE_LINE_INFO = {0,0,2,2,2,2,24,24,24,24,25,25,25,40,40,42,42,42,43,43,43,44,44,44,45,45,45,46,46,46,47,47,47,48,48,48,50,50,56,56,56,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String ksefNumber, Faktura invoice) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<meta charset=\"UTF-8\">\n<title>KSEF</title>\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n<link\n        rel=\"stylesheet\"\n        href=\"https://cdn.jsdelivr.net/npm/bulma@1.0.4/css/bulma.min.css\"\n>\n<style>\n</style>\n<script src=\"https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js\"></script>\n<body>\n\n<div>\n    <h1 class=\"h1\">AUTOMOBIL - KSEF</h1>\n</div>\n\n<div>\n    ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(ksefNumber);
		jteOutput.writeContent("\n    ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(invoice.getNaglowek().getDataWytworzeniaFa().toString());
		jteOutput.writeContent("\n\n    <table class=\"table is-striped is-fullwidth\">\n        <thead>\n            <tr>\n                <th>Nr. wiersza</th>\n                <th>Nazwa</th>\n                <th class=\"has-text-centered\">Index</th>\n                <th class=\"has-text-centered\">Jednostka</th>\n                <th class=\"has-text-centered\">Ilość</th>\n                <th class=\"has-text-right\">Cena jednostki</th>\n                <th class=\"has-text-right\">Cena razem</th>\n            </tr>\n        </thead>\n        <tbody>\n            ");
		for (var wierszFaktury : invoice.getFa().getFaWiersz()) {
			jteOutput.writeContent("\n                <tr>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(wierszFaktury.getNrWierszaFa());
			jteOutput.writeContent("</td>\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(wierszFaktury.getP7());
			jteOutput.writeContent("</td>\n                    <td class=\"has-text-centered\">");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(wierszFaktury.getIndeks());
			jteOutput.writeContent("</td>\n                    <td class=\"has-text-centered\">");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(wierszFaktury.getP8A());
			jteOutput.writeContent("</td>\n                    <td class=\"has-text-centered\">");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(wierszFaktury.getP8B());
			jteOutput.writeContent("</td>\n                    <td class=\"has-text-right\">");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(String.format("%.2f",wierszFaktury.getP9A()));
			jteOutput.writeContent(" zł</td>\n                    <td class=\"has-text-right\">");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(String.format("%.2f",wierszFaktury.getP11()));
			jteOutput.writeContent(" zł</td>\n                </tr>\n            ");
		}
		jteOutput.writeContent("\n        </tbody>\n    </table>\n</div>\n\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String ksefNumber = (String)params.get("ksefNumber");
		Faktura invoice = (Faktura)params.get("invoice");
		render(jteOutput, jteHtmlInterceptor, ksefNumber, invoice);
	}
}
