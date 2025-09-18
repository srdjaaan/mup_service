package mup.model;

import auth_service.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "zahtevi")
public class Zahtev {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gradjanin_jmbg", nullable = false)
    private String gradjaninJmbg;
    
    @Column(name = "policajac_jmbg")
    private String policajacJmbg;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusZahteva status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipDokumenta tipDokumenta;
    
    @Column(nullable = false)
    private String razlog;
    
    @Column
    private String komentar;
    
    @Column(nullable = false)
    private LocalDateTime datumKreiranja;
    
    @Column
    private LocalDateTime datumOdobrenja;
    
    // Konstruktori
    public Zahtev() {
        this.datumKreiranja = LocalDateTime.now();
        this.status = StatusZahteva.NA_CEKANJU;
    }
    
    public Zahtev(String gradjaninJmbg, TipDokumenta tipDokumenta, String razlog) {
        this();
        this.gradjaninJmbg = gradjaninJmbg;
        this.tipDokumenta = tipDokumenta;
        this.razlog = razlog;
    }
    
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
    
    public String getPolicajacJmbg() {
        return policajacJmbg;
    }
    
    public void setPolicajacJmbg(String policajacJmbg) {
        this.policajacJmbg = policajacJmbg;
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

