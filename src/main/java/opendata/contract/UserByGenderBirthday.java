package opendata.contract;

import auth_service.model.Gender;

import java.time.LocalDate;

public class UserByGenderBirthday {
    private String name;
    private String lastname;
    private Gender gender;
    private LocalDate birthday;

    public UserByGenderBirthday(String name, String lastname, Gender gender, LocalDate birthday) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.birthday = birthday;
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
}
