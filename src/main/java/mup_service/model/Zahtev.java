package mup_service.model;

import auth_service.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Zahtev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipDokumenta tip;

    @Enumerated(EnumType.STRING)
    private StatusZahteva status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipDokumenta getTip() {
        return tip;
    }

    public void setTip(TipDokumenta tip) {
        this.tip = tip;
    }

    public StatusZahteva getStatus() {
        return status;
    }

    public void setStatus(StatusZahteva status) {
        this.status = status;
    }
}
