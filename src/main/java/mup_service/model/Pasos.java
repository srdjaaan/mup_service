package mup_service.model;

import auth_service.model.Gender;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Pasos extends Document {
    private String brojPasosa;
    private String drzavljanstvo;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
