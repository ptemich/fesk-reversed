package gg.jte.generated.ondemand;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadata;
import pl.ptemich.ksef.ksef.InvoicesPackage;
import java.util.Set;
@SuppressWarnings("unchecked")
public final class JtetableGenerated {
	public static final String JTE_NAME = "table.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,4,4,4,4,8,8,8,9,9,9,10,10,24,24,26,26,26,27,27,27,28,28,28,29,29,29,30,30,30,31,31,31,33,33,34,34,34,34,35,35,36,36,36,36,40,40,42,42,42,42,44,44,46,46,47,47,54,54,54,4,5,5,5,5};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, InvoicesPackage invoicesPackage, Set<String> acardInvoices) {
		jteOutput.writeContent("\n<div id=\"invoices\">\n    ");
		if (invoicesPackage != null) {
			jteOutput.writeContent("\n        <p class=\"pl-2\">Lista pobrana o: ");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(invoicesPackage.getFormatedTime());
			jteOutput.writeContent("</p>\n        ");
			if (invoicesPackage.invoices() != null) {
				jteOutput.writeContent("\n            <table class=\"table is-striped is-fullwidth\">\n                <thead>\n                <tr>\n                    <th>Sprzedawca</th>\n                    <th class=\"has-text-centered\">Data wystawienia</th>\n                    <th class=\"has-text-right\">Netto</th>\n                    <th class=\"has-text-right\">Vat</th>\n                    <th class=\"has-text-right\">Brutto</th>\n                    <th>Numer KSEF</th>\n                    <th class=\"has-text-centered\">ACARD</th>\n                    <th></th>\n                </tr>\n                </thead>\n                ");
				for (InvoiceMetadata invoice : invoicesPackage.invoices()) {
					jteOutput.writeContent("\n                    <tr>\n                        <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(invoice.getSeller().getName());
					jteOutput.writeContent("</td>\n                        <td class=\"has-text-centered\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(invoice.getIssueDate().toString());
					jteOutput.writeContent("</td>\n                        <td class=\"has-text-right\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(String.format("%.2f", invoice.getNetAmount()));
					jteOutput.writeContent(" zł</td>\n                        <td class=\"has-text-right\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(String.format("%.2f", invoice.getVatAmount()));
					jteOutput.writeContent(" zł</td>\n                        <td class=\"has-text-right\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(String.format("%.2f", invoice.getGrossAmount()));
					jteOutput.writeContent(" zł</td>\n                        <td>");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(invoice.getKsefNumber());
					jteOutput.writeContent("</td>\n                        <td class=\"has-text-centered\">\n                            ");
					if (acardInvoices.contains(invoice.getKsefNumber())) {
						jteOutput.writeContent("\n                                <a href=\"/details/acard/");
						jteOutput.setContext("a", "href");
						jteOutput.writeUserContent(invoice.getKsefNumber());
						jteOutput.setContext("a", null);
						jteOutput.writeContent("\" target=\"_blank\">✅</a>\n                            ");
					} else {
						jteOutput.writeContent("\n                                <span hx-get=\"/copy/");
						jteOutput.setContext("span", "hx-get");
						jteOutput.writeUserContent(invoice.getKsefNumber());
						jteOutput.setContext("span", null);
						jteOutput.writeContent("\"\n                                      hx-target=\"#invoices\"\n                                      hx-swap=\"outerHTML\"\n                                      class=\"button is-black is-size-7\">KOPIUJ</span>\n                            ");
					}
					jteOutput.writeContent("\n                        </td>\n                        <td><a href=\"/download/xml/");
					jteOutput.setContext("a", "href");
					jteOutput.writeUserContent(invoice.getKsefNumber());
					jteOutput.setContext("a", null);
					jteOutput.writeContent("\">XML</a></td>\n                    </tr>\n                ");
				}
				jteOutput.writeContent("\n            </table>\n        ");
			}
			jteOutput.writeContent("\n    ");
		}
		jteOutput.writeContent("\n    <span hx-get=\"/reload\"\n          hx-target=\"#invoices\"\n          hx-swap=\"outerHTML\"\n          class=\"button is-black ml-2\">\n        ODŚWIERZ\n    </span>\n</div>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		InvoicesPackage invoicesPackage = (InvoicesPackage)params.get("invoicesPackage");
		Set<String> acardInvoices = (Set<String>)params.get("acardInvoices");
		render(jteOutput, jteHtmlInterceptor, invoicesPackage, acardInvoices);
	}
}
