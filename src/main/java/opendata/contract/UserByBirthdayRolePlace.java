package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserByBirthdayRolePlace {
    private String name;
    private String lastname;
    private LocalDate birthday;
    private Role role;
    private String placeOfBirth;

    public UserByBirthdayRolePlace(String name, String lastname, LocalDate birthday, Role role, String placeOfBirth) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.role = role;
        this.placeOfBirth = placeOfBirth;
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

    public Role getRole() {
        return role;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
}
