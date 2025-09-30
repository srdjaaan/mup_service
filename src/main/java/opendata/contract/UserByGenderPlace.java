package opendata.contract;

import auth_service.model.Gender;

public class UserByGenderPlace {
    private String name;
    private String lastname;
    private Gender gender;
    private String placeOfBirth;

    public UserByGenderPlace(String name, String lastname, Gender gender, String placeOfBirth) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
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

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
}
