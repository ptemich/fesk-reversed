package gg.jte.generated.ondemand;
import pl.ptemich.ksef.ksef.InvoicesPackage;
import pl.ptemich.ksef.localconf.LocalConfig;
@SuppressWarnings("unchecked")
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,3,24,24,24,26,27,28,29,32,32,32,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, LocalConfig localConfig, InvoicesPackage invoicesPackage) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<meta charset=\"UTF-8\">\n<title>KSEF</title>\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n<link\n        rel=\"stylesheet\"\n        href=\"https://cdn.jsdelivr.net/npm/bulma@1.0.4/css/bulma.min.css\"\n>\n<style>\n</style>\n<script src=\"https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js\"></script>\n<body>\n\n<div>\n    <h1 class=\"is-size-1 pl-2 has-text-weight-bold\">AUTOMOBIL - KSEF</h1>\n</div>\n\n");
		gg.jte.generated.ondemand.JtetableGenerated.render(jteOutput, jteHtmlInterceptor, invoicesPackage);
		jteOutput.writeContent("\n\n");
		jteOutput.writeContent("\n");
		jteOutput.writeContent("\n");
		jteOutput.writeContent("\n");
		jteOutput.writeContent("\n\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		LocalConfig localConfig = (LocalConfig)params.get("localConfig");
		InvoicesPackage invoicesPackage = (InvoicesPackage)params.get("invoicesPackage");
		render(jteOutput, jteHtmlInterceptor, localConfig, invoicesPackage);
	}
}
