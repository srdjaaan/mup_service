package mup.DTO;

import mup.model.TipDokumenta;
import mup.model.Kategorija;

public class KreiranjeZahtevaDTO {
    
    private TipDokumenta tipDokumenta;
    private String razlog;
    private Kategorija kategorija; // Za vozacke dozvole
    
    public KreiranjeZahtevaDTO() {}
    
    public KreiranjeZahtevaDTO(TipDokumenta tipDokumenta, String razlog) {
        this.tipDokumenta = tipDokumenta;
        this.razlog = razlog;
    }
    
    public KreiranjeZahtevaDTO(TipDokumenta tipDokumenta, String razlog, Kategorija kategorija) {
        this.tipDokumenta = tipDokumenta;
        this.razlog = razlog;
        this.kategorija = kategorija;
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
    
    public Kategorija getKategorija() {
        return kategorija;
    }
    
    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }
}

