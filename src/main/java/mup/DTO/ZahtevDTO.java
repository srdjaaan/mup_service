package mup.DTO;

import mup.model.StatusZahteva;
import mup.model.TipDokumenta;
import java.time.LocalDateTime;

public class ZahtevDTO {
    
    private Long id;
    private String gradjaninJmbg;
    private String gradjaninIme;
    private String gradjaninPrezime;
    private String policajacJmbg;
    private String policajacIme;
    private String policajacPrezime;
    private StatusZahteva status;
    private TipDokumenta tipDokumenta;
    private String razlog;
    private String komentar;
    private LocalDateTime datumKreiranja;
    private LocalDateTime datumOdobrenja;
    
    public ZahtevDTO() {}
    
    // Getters i Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGradjaninJmbg() {
        return gradjaninJmbg;
    }
    
    public void setGradjaninJmbg(String gradjaninJmbg) {
        this.gradjaninJmbg = gradjaninJmbg;
    }
    
    public String getGradjaninIme() {
        return gradjaninIme;
    }
    
    public void setGradjaninIme(String gradjaninIme) {
        this.gradjaninIme = gradjaninIme;
    }
    
    public String getGradjaninPrezime() {
        return gradjaninPrezime;
    }
    
    public void setGradjaninPrezime(String gradjaninPrezime) {
        this.gradjaninPrezime = gradjaninPrezime;
    }
    
    public String getPolicajacJmbg() {
        return policajacJmbg;
    }
    
    public void setPolicajacJmbg(String policajacJmbg) {
        this.policajacJmbg = policajacJmbg;
    }
    
    public String getPolicajacIme() {
        return policajacIme;
    }
    
    public void setPolicajacIme(String policajacIme) {
        this.policajacIme = policajacIme;
    }
    
    public String getPolicajacPrezime() {
        return policajacPrezime;
    }
    
    public void setPolicajacPrezime(String policajacPrezime) {
        this.policajacPrezime = policajacPrezime;
    }
    
    public StatusZahteva getStatus() {
        return status;
    }
    
    public void setStatus(StatusZahteva status) {
        this.status = status;
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
    
    public String getKomentar() {
        return komentar;
    }
    
    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
    
    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }
    
    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
    
    public LocalDateTime getDatumOdobrenja() {
        return datumOdobrenja;
    }
    
    public void setDatumOdobrenja(LocalDateTime datumOdobrenja) {
        this.datumOdobrenja = datumOdobrenja;
    }
}

