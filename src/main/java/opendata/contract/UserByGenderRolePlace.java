package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserByGenderRolePlace {
    private String name;
    private String lastname;
    private Gender gender;
    private Role role;
    private String placeOfBirth;

    public UserByGenderRolePlace(String name, String lastname, Gender gender, Role role, String placeOfBirth) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.role = role;
        this.placeOfBirth = placeOfBirth;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public Role getRole() {
        return role;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
}
