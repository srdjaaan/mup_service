package auth_service.DTO;

import java.time.LocalDate;

public class DocumentInfoDTO {
    
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    
    public DocumentInfoDTO() {}
    
    public DocumentInfoDTO(Long id, String name, String lastname, LocalDate birthday, 
                          String placeOfBirth, LocalDate createdAt, LocalDate expiresAt) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    
    // Getters i Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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

