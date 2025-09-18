package mup.DTO;

import mup.model.StatusZahteva;

public class OdobravanjeZahtevaDTO {
    
    private StatusZahteva status;
    private String komentar;
    
    public OdobravanjeZahtevaDTO() {}
    
    public OdobravanjeZahtevaDTO(StatusZahteva status, String komentar) {
        this.status = status;
        this.komentar = komentar;
    }
    
    public StatusZahteva getStatus() {
        return status;
    }
    
    public void setStatus(StatusZahteva status) {
        this.status = status;
    }
    
    public String getKomentar() {
        return komentar;
    }
    
    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
}

