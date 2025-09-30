package opendata.contract;


import java.time.LocalDate;

public class UserByBirthday {
    private String name;
    private String lastname;
    private LocalDate birthday;


    public UserByBirthday(String name, String lastname,LocalDate birthday) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
    }


    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }


    public LocalDate getBirthday() {
        return birthday;
    }

}
