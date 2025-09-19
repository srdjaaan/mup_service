package mup.model;

import auth_service.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    
    @Transient
    private String tipDokumenta;
    
    @Transient
    private String brojDokumenta;

    @Column(name = "user_jmbg")
    private String userJmbg;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getUserJmbg() {
        return userJmbg;
    }

    public void setUserJmbg(String userJmbg) {
        this.userJmbg = userJmbg;
    }
    
    public String getTipDokumenta() {
        return tipDokumenta;
    }
    
    public void setTipDokumenta(String tipDokumenta) {
        this.tipDokumenta = tipDokumenta;
    }
    
    public String getBrojDokumenta() {
        return brojDokumenta;
    }
    
    public void setBrojDokumenta(String brojDokumenta) {
        this.brojDokumenta = brojDokumenta;
    }
}
