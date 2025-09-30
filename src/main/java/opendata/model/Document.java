package opendata.model;

import java.time.LocalDate;

public class Document {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    private String tipDokumenta;
    private String brojDokumenta;
    private String kategorije;
    private String userJmbg;

    public Document() {}

    public Document(Long id, String name, String lastname,
                    LocalDate birthday, String placeOfBirth,
                    LocalDate createdAt, LocalDate expiresAt,
                    String tipDokumenta, String brojDokumenta,
                    String kategorije, String userJmbg) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.tipDokumenta = tipDokumenta;
        this.brojDokumenta = brojDokumenta;
        this.kategorije = kategorije;
        this.userJmbg = userJmbg;
    }

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

    public String getTipDokumenta() {
        return tipDokumenta;
    }

    public void setTipDokumenta(String tipDokumenta) {
        this.tipDokumenta = tipDokumenta;
    }

    public String getKategorije() {
        return kategorije;
    }

    public void setKategorije(String kategorije) {
        this.kategorije = kategorije;
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
}
