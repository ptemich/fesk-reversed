package pl.ptemich.ksef.local;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LocalInvoice {

    @Id
    private String fileId;

    private boolean exported;

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

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }
}
