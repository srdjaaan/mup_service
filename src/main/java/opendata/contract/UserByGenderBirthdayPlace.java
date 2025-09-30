package opendata.contract;

import auth_service.model.Gender;
import auth_service.model.Role;

import java.time.LocalDate;

public class UserByGenderBirthdayPlace {
    private String name;
    private String lastname;
    private Gender gender;
    private LocalDate birthday;
    private String placeOfBirth;

    public UserByGenderBirthdayPlace(String name, String lastname, Gender gender, LocalDate birthday, String placeOfBirth) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.birthday = birthday;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
}
