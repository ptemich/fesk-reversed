package pl.ptemich.ksef.localconf;



public class LocalConfig {

    private String nip;
    private String ksefToken;
    private String exportPath;

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

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
}
