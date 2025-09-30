package opendata.contract;


import java.time.LocalDate;

public class UserByBirthdayPlace {
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;

    public UserByBirthdayPlace(String name, String lastname, String placeOfBirth, LocalDate birthday) {
        this.name = name;
        this.lastname = lastname;
        this.placeOfBirth = placeOfBirth;
        this.birthday = birthday;
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

    public LocalDate getBirthday() {
        return birthday;
    }
}
