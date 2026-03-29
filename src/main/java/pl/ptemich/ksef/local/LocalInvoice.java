package pl.ptemich.ksef.local;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LocalInvoice {

    @Id
    private String fileId;

    private int processingCode;

    private String processingDescription;

    private String ksefNumber;

    private String seller;

    private String buyer;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public int getProcessingCode() {
        return processingCode;
    }

    public void setProcessingCode(int processingCode) {
        this.processingCode = processingCode;
    }

    public String getProcessingDescription() {
        return processingDescription;
    }

    public void setProcessingDescription(String processingDescription) {
        this.processingDescription = processingDescription;
    }

    public String getKsefNumber() {
        return ksefNumber;
    }

    public void setKsefNumber(String ksefNumber) {
        this.ksefNumber = ksefNumber;
    }
}
