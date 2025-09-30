package opendata.contract;


import auth_service.model.Role;


public class UserByRole {
    private String name;
    private String lastname;
    private Role role;


    public UserByRole(String name, String lastname, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.role = role;
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
}
