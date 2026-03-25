package pl.ptemich.ksef.localconf;



public class LocalConfig {

    private String nip;
    private String ksefToken;
    private String receivedInvoicesPath;
    private String generatedInvoicesPath;

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getKsefToken() {
        return ksefToken;
    }

    public void setKsefToken(String ksefToken) {
        this.ksefToken = ksefToken;
    }

    public String getReceivedInvoicesPath() {
        return receivedInvoicesPath;
    }

    public void setReceivedInvoicesPath(String receivedInvoicesPath) {
        this.receivedInvoicesPath = receivedInvoicesPath;
    }

    public String getGeneratedInvoicesPath() {
        return generatedInvoicesPath;
    }

    public void setGeneratedInvoicesPath(String generatedInvoicesPath) {
        this.generatedInvoicesPath = generatedInvoicesPath;
    }
}
