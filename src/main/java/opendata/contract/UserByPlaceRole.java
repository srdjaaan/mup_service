package opendata.contract;

import auth_service.model.Role;

public class UserByPlaceRole {
    private String name;
    private String lastname;
    private String placeOfBirth;
    private Role role;


    public UserByPlaceRole(String name, String lastname,String placeOfBirth, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.placeOfBirth = placeOfBirth;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public Role getRole() {
        return role;
    }
}
