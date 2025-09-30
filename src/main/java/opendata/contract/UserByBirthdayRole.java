package opendata.contract;


import auth_service.model.Role;

import java.time.LocalDate;

public class UserByBirthdayRole {
    private String name;
    private String lastname;
    private LocalDate birthday;
    private Role role;

    public UserByBirthdayRole(String name, String lastname, Role role, LocalDate birthday) {
        this.name = name;
        this.lastname = lastname;
        this.role = role;
        this.birthday = birthday;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public Role getRole() {
        return role;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
}
