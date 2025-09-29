package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserDTO {
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private Role role;
    private Gender gender;

    public UserDTO(String name, String lastname,LocalDate birthday, String placeOfBirth, Role role, Gender gender) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.placeOfBirth = placeOfBirth;
        this.role = role;
        this.gender = gender;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }


    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public Role getRole() {
        return role;
    }

    public Gender getGender() {
        return gender;
    }
}
