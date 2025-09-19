package mup.DTO;

import mup.model.Kategorija;

public class DodavanjeKategorijeDTO {
    
    private Kategorija kategorija;
    private String razlog;
    
    public DodavanjeKategorijeDTO() {}
    
    public DodavanjeKategorijeDTO(Kategorija kategorija, String razlog) {
        this.kategorija = kategorija;
        this.razlog = razlog;
    }
    
    public Kategorija getKategorija() {
        return kategorija;
    }
    
    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }
    
    public String getRazlog() {
        return razlog;
    }
    
    public void setRazlog(String razlog) {
        this.razlog = razlog;
    }
}
