package mup.model;

import javax.persistence.*;

@Entity
@Table(name = "vozacke_dozvole")
public class VozackaDozvola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kategorija kategorija;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Kategorija getKategorija() { return kategorija; }
    public void setKategorija(Kategorija kategorija) { this.kategorija = kategorija; }
}
