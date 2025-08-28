package mup_service.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class VozackaDozvola extends Document {

    @Enumerated(EnumType.STRING)
    private Kategorija kategorija;

    private String brojVozacke;


}
