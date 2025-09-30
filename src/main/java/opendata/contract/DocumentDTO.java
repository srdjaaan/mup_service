package opendata.contract;

import java.time.LocalDate;

public class DocumentDTO {
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    private String tipDokumenta;
    private String kategorije;

    public DocumentDTO() {}

    public DocumentDTO(String name, String lastname,
                    LocalDate birthday, String placeOfBirth,
                    LocalDate createdAt, LocalDate expiresAt,
                    String tipDokumenta, String kategorije) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.tipDokumenta = tipDokumenta;
        this.kategorije = kategorije;
    }

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
