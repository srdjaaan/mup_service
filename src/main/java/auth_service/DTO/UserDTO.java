package auth_service.DTO;

import auth_service.model.Gender;
import auth_service.model.Role;
import java.time.LocalDate;

public class UserDTO {
    private String jmbg;
    private String name;
    private String lastname;
    private String username;
    private LocalDate birthday;
    private String placeOfBirth;
    private Role role;
    private Gender gender;

    public UserDTO(String jmbg, String name, String lastname, String username, LocalDate birthday, String placeOfBirth, Role role, Gender gender) {
        this.jmbg = jmbg;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.role = role;
        this.gender = gender;
    }

    public String getJmbg() { return jmbg; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getUsername() { return username; }
    public LocalDate getBirthday() { return birthday; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public Role getRole() { return role; }
    public Gender getGender() { return gender; }
}
