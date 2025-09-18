package auth_service.DTO;

import mup.model.Document;
import auth_service.model.Gender;
import auth_service.model.Role;
import java.time.LocalDate;
import java.util.List;

public class UserWithDocumentsDTO {
    
    private String jmbg;
    private String name;
    private String lastname;
    private String username;
    private LocalDate birthday;
    private String placeOfBirth;
    private Role role;
    private Gender gender;
    private List<DocumentInfoDTO> documents;
    
    public UserWithDocumentsDTO() {}
    
    public UserWithDocumentsDTO(String jmbg, String name, String lastname, String username, 
                               LocalDate birthday, String placeOfBirth, Role role, Gender gender, 
                               List<DocumentInfoDTO> documents) {
        this.jmbg = jmbg;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.role = role;
        this.gender = gender;
        this.documents = documents;
    }
    
    // Getters i Setters
    public String getJmbg() {
        return jmbg;
    }
    
    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public List<DocumentInfoDTO> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<DocumentInfoDTO> documents) {
        this.documents = documents;
    }
}
