package opendata.contract;

import java.time.LocalDate;

public class UserByPlace {
    private String name;
    private String lastname;
    private String placeOfBirth;


    public UserByPlace(String name, String lastname,String placeOfBirth) {
        this.name = name;
        this.lastname = lastname;
        this.placeOfBirth = placeOfBirth;
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
}
