package pl.ptemich.ksef.localconf;



public class LocalConfig {

    private String nip;
    private String ksefToken;
    private String ksefToLocalPath; // pobrane z ksef wystawione przez inne firmy - ACARD importuje ale nie kasuje
    private String localToKsefPath; // do wyslania do ksef
    private String ksefToLocalProcessed; // pobrane z ksef po wyslaniu z lokalna - ACARD importuje i kasuje
    private String ksefToLocalProcessedCopy; // pobrane z ksef po wyslaniu z lokalna

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

    public String getKsefToLocalPath() {
        return ksefToLocalPath;
    }

    public void setKsefToLocalPath(String ksefToLocalPath) {
        this.ksefToLocalPath = ksefToLocalPath;
    }

    public String getLocalToKsefPath() {
        return localToKsefPath;
    }

    public void setLocalToKsefPath(String localToKsefPath) {
        this.localToKsefPath = localToKsefPath;
    }

    public String getKsefToLocalProcessed() {
        return ksefToLocalProcessed;
    }

    public void setKsefToLocalProcessed(String ksefToLocalProcessed) {
        this.ksefToLocalProcessed = ksefToLocalProcessed;
    }

    public String getKsefToLocalProcessedCopy() {
        return ksefToLocalProcessedCopy;
    }

    public void setKsefToLocalProcessedCopy(String ksefToLocalProcessedCopy) {
        this.ksefToLocalProcessedCopy = ksefToLocalProcessedCopy;
    }
}
