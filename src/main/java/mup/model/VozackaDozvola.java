package mup.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vozacke_dozvole_v2")
public class VozackaDozvola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "vozacka_dozvola_kategorije", 
        joinColumns = @JoinColumn(name = "vozacka_dozvola_id")
    )
    @Column(name = "kategorija")
    private List<Kategorija> kategorije = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public List<Kategorija> getKategorije() { return kategorije; }
    public void setKategorije(List<Kategorija> kategorije) { this.kategorije = kategorije; }
    
    // Helper metode za rad sa kategorijama
    public void dodajKategoriju(Kategorija kategorija) {
        if (!kategorije.contains(kategorija)) {
            kategorije.add(kategorija);
        }
    }
    
    public void ukloniKategoriju(Kategorija kategorija) {
        kategorije.remove(kategorija);
    }
    
    public boolean imaKategoriju(Kategorija kategorija) {
        return kategorije.contains(kategorija);
    }
}
