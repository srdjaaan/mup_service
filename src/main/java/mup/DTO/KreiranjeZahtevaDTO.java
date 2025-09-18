package mup.DTO;

import mup.model.TipDokumenta;

public class KreiranjeZahtevaDTO {
    
    private TipDokumenta tipDokumenta;
    private String razlog;
    
    public KreiranjeZahtevaDTO() {}
    
    public KreiranjeZahtevaDTO(TipDokumenta tipDokumenta, String razlog) {
        this.tipDokumenta = tipDokumenta;
        this.razlog = razlog;
    }
    
    public TipDokumenta getTipDokumenta() {
        return tipDokumenta;
    }
    
    public void setTipDokumenta(TipDokumenta tipDokumenta) {
        this.tipDokumenta = tipDokumenta;
    }
    
    public String getRazlog() {
        return razlog;
    }
    
    public void setRazlog(String razlog) {
        this.razlog = razlog;
    }
}

