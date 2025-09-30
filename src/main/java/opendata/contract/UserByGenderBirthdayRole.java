package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserByGenderBirthdayRole {
    private String name;
    private String lastname;
    private Gender gender;
    private LocalDate birthday;
    private Role role;

    public UserByGenderBirthdayRole(String name, String lastname, Gender gender, LocalDate birthday, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.birthday = birthday;
        this.role = role;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public Role getRole() {
        return role;
    }
}
