package mup.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userJmbg;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Long zahtevId; // Referenca na zahtev koji je povezan sa obaveštenjem

    public Notification() {}

    public Notification(String userJmbg, String title, String message, NotificationType type, Long zahtevId) {
        this.userJmbg = userJmbg;
        this.title = title;
        this.message = message;
        this.type = type;
        this.zahtevId = zahtevId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserJmbg() {
        return userJmbg;
    }

    public void setUserJmbg(String userJmbg) {
        this.userJmbg = userJmbg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getZahtevId() {
        return zahtevId;
    }

    public void setZahtevId(Long zahtevId) {
        this.zahtevId = zahtevId;
    }
}