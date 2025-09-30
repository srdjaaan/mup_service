package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserByGender {
    private String name;
    private String lastname;
    private Gender gender;

    public UserByGender(String name, String lastname, Gender gender) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
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
}
