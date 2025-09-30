package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

public class UserByGenderRole {
    private String name;
    private String lastname;
    private Gender gender;
    private Role role;

    public UserByGenderRole(String name, String lastname, Gender gender, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
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

    public Role getRole() {
        return role;
    }
}
