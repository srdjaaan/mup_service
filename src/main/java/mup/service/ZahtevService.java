package mup.service;

import mup.DTO.KreiranjeZahtevaDTO;
import mup.DTO.OdobravanjeZahtevaDTO;
import mup.DTO.ZahtevDTO;
import mup.DTO.DodavanjeKategorijeDTO;
import mup.model.*;
import mup.repository.ZahtevRepository;
import mup.repository.LicnaKartaRepository;
import mup.repository.PasosRepository;
import mup.repository.VozackaDozvolaRepository;
import mup.repository.NotificationRepository; // DODATO
import auth_service.model.User;
import auth_service.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZahtevService {

    @Autowired
    private ZahtevRepository zahtevRepository;

    @Autowired
    private LicnaKartaRepository licnaKartaRepository;

    @Autowired
    private PasosRepository pasosRepository;

    @Autowired
    private VozackaDozvolaRepository vozackaDozvolaRepository;

    @Autowired
    private NotificationRepository notificationRepository; // VEĆ POSTOJI U TVOM KODU, OSTAVLJENO

    @Autowired
    private AuthServiceClient authServiceClient;

    public ZahtevDTO kreirajZahtev(String jmbg, KreiranjeZahtevaDTO kreiranjeZahtevaDTO) {
        // Dohvati korisnika iz auth servisa
        User gradjanin = authServiceClient.getUserByJmbg(jmbg);

        if (gradjanin == null) {
            throw new RuntimeException("Korisnik sa JMBG-om " + jmbg + " nije pronađen");
        }

        if (gradjanin.getRole() != Role.GRADJANIN) {
            throw new RuntimeException("Samo građani mogu kreirati zahteve");
        }

        // Proveri starost korisnika
        String validacijaStarosti = validirajStarost(jmbg, kreiranjeZahtevaDTO.getTipDokumenta());
        if (validacijaStarosti != null) {
            throw new RuntimeException(validacijaStarosti);
        }

        // Proveri da li lična karta već postoji
        if (kreiranjeZahtevaDTO.getTipDokumenta() == TipDokumenta.LICNA_KARTA) {
            if (licnaKartaRepository.existsByJmbg(jmbg)) {
                // Proveri da li je lična karta validna
                String validacijskaPoruka = validirajLicnuKartu(jmbg);
                if (validacijskaPoruka != null) {
                    throw new RuntimeException(validacijskaPoruka);
                }
                throw new RuntimeException("Lična karta već postoji za ovog korisnika");
            }
        }

        // Proveri da li je zahtev za produženje lične karte
        if (kreiranjeZahtevaDTO.getTipDokumenta() == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
            if (!licnaKartaRepository.existsByJmbg(jmbg)) {
                throw new RuntimeException("Korisnik nema ličnu kartu za produženje");
            }

            // Proveri da li je lična karta stvarno istekla
            String validacijskaPoruka = validirajLicnuKartu(jmbg);
            if (validacijskaPoruka == null) {
                throw new RuntimeException("Lična karta je još uvek validna, nema potrebe za produženjem");
            }
        }

        // Proveri da li je zahtev za kreiranje pasoša
        if (kreiranjeZahtevaDTO.getTipDokumenta() == TipDokumenta.PASOS) {
            // Proveri da li korisnik ima ličnu kartu
            if (!licnaKartaRepository.existsByJmbg(jmbg)) {
                throw new RuntimeException("Morate imati ličnu kartu da bi kreirali pasoš");
            }

            // Proveri da li je lična karta validna
            String validacijskaPoruka = validirajLicnuKartu(jmbg);
            if (validacijskaPoruka != null) {
                throw new RuntimeException("Morate imati ličnu kartu da bi kreirali pasoš");
            }

            // Proveri da li pasoš već postoji
            if (pasosRepository.existsByDrzavljanstvo(jmbg)) {
                throw new RuntimeException("Pasoš već postoji za ovog korisnika");
            }
        }

        // Proveri da li je zahtev za kreiranje vozacke dozvole
        if (kreiranjeZahtevaDTO.getTipDokumenta() == TipDokumenta.VOZACKA_DOZVOLA) {
            // Proveri da li je kategorija odabrana
            if (kreiranjeZahtevaDTO.getKategorija() == null) {
                throw new RuntimeException("Morate odabrati kategoriju za vozacku dozvolu");
            }

            // Proveri starost za kategoriju vozacke dozvole
            String validacijaStarostiZaKategoriju = validirajStarostZaKategoriju(jmbg, kreiranjeZahtevaDTO.getKategorija());
            if (validacijaStarostiZaKategoriju != null) {
                throw new RuntimeException(validacijaStarostiZaKategoriju);
            }

            // Proveri da li korisnik ima ličnu kartu
            if (!licnaKartaRepository.existsByJmbg(jmbg)) {
                throw new RuntimeException("Morate imati ličnu kartu da bi kreirali vozacku dozvolu");
            }

            // Proveri da li je lična karta validna
            String validacijskaPoruka = validirajLicnuKartu(jmbg);
            if (validacijskaPoruka != null) {
                throw new RuntimeException("Morate imati validnu ličnu kartu da bi kreirali vozacku dozvolu");
            }

            // Proveri da li vozacka dozvola već postoji
            if (vozackaDozvolaRepository.existsByDocumentUserJmbg(jmbg)) {
                // Proveri da li korisnik već ima tu kategoriju
                List<VozackaDozvola> postojeceVozackeDozvole = vozackaDozvolaRepository.findByDocumentUserJmbg(jmbg);
                VozackaDozvola najnovijaVozackaDozvola = postojeceVozackeDozvole.stream()
                        .max((v1, v2) -> v1.getDocument().getCreatedAt().compareTo(v2.getDocument().getCreatedAt()))
                        .orElse(null);
                
                if (najnovijaVozackaDozvola != null && najnovijaVozackaDozvola.imaKategoriju(kreiranjeZahtevaDTO.getKategorija())) {
                    throw new RuntimeException("Već imate " + kreiranjeZahtevaDTO.getKategorija() + " kategoriju na svojoj vozackoj dozvoli. Ne možete dodati istu kategoriju ponovo.");
                }
            }
        }

        Zahtev zahtev;
        if (kreiranjeZahtevaDTO.getTipDokumenta() == TipDokumenta.VOZACKA_DOZVOLA) {
            zahtev = new Zahtev(gradjanin.getJmbg(), kreiranjeZahtevaDTO.getTipDokumenta(), kreiranjeZahtevaDTO.getRazlog(), kreiranjeZahtevaDTO.getKategorija());
        } else {
            zahtev = new Zahtev(gradjanin.getJmbg(), kreiranjeZahtevaDTO.getTipDokumenta(), kreiranjeZahtevaDTO.getRazlog());
        }
        zahtev = zahtevRepository.save(zahtev);

        return convertToDTO(zahtev);
    }

    public ZahtevDTO odobriZahtev(Long zahtevId, String policajacJmbg, OdobravanjeZahtevaDTO odobravanjeDTO) {
        Zahtev zahtev = zahtevRepository.findById(zahtevId)
                .orElseThrow(() -> new RuntimeException("Zahtev sa ID " + zahtevId + " nije pronađen"));

        User policajac = authServiceClient.getUserByJmbg(policajacJmbg);

        if (policajac == null) {
            throw new RuntimeException("Policajac sa JMBG-om " + policajacJmbg + " nije pronađen");
        }

        if (policajac.getRole() != Role.POLICAJAC) {
            throw new RuntimeException("Samo policajci mogu odobravati zahteve");
        }

        if (zahtev.getStatus() != StatusZahteva.NA_CEKANJU) {
            throw new RuntimeException("Zahtev je već obrađen");
        }

        zahtev.setPolicajacJmbg(policajac.getJmbg());
        zahtev.setStatus(odobravanjeDTO.getStatus());
        zahtev.setKomentar(odobravanjeDTO.getKomentar());
        zahtev.setDatumOdobrenja(LocalDateTime.now());

        // Ako je zahtev odobren, kreiraj ličnu kartu, produži postojeću ili kreiraj pasoš
        if (odobravanjeDTO.getStatus() == StatusZahteva.ODOBREN) {
            if (zahtev.getTipDokumenta() == TipDokumenta.LICNA_KARTA) {
                kreirajLicnuKartu(zahtev);
                // DODATO: obaveštenje
                kreirajObavestenje(
                        zahtev.getGradjaninJmbg(),
                        "Zahtev za ličnu kartu je odobren",
                        "Vaš zahtev za kreiranje lične karte je uspešno odobren. " +
                                (odobravanjeDTO.getKomentar() != null ? "Komentar: " + odobravanjeDTO.getKomentar() : ""),
                        NotificationType.ZAHTEV_ODOBREN,
                        zahtev.getId()
                );
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
                produziLicnuKartu(zahtev);
                // DODATO: obaveštenje
                kreirajObavestenje(
                        zahtev.getGradjaninJmbg(),
                        "Zahtev za produženje lične karte je odobren",
                        "Vaš zahtev za produženje lične karte je uspešno odobren. " +
                                (odobravanjeDTO.getKomentar() != null ? "Komentar: " + odobravanjeDTO.getKomentar() : ""),
                        NotificationType.ZAHTEV_ODOBREN,
                        zahtev.getId()
                );
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PASOS) {
                kreirajPasos(zahtev);
                // DODATO: obaveštenje
                kreirajObavestenje(
                        zahtev.getGradjaninJmbg(),
                        "Zahtev za pasoš je odobren",
                        "Vaš zahtev za kreiranje pasoša je uspešno odobren. " +
                                (odobravanjeDTO.getKomentar() != null ? "Komentar: " + odobravanjeDTO.getKomentar() : ""),
                        NotificationType.ZAHTEV_ODOBREN,
                        zahtev.getId()
                );
            } else if (zahtev.getTipDokumenta() == TipDokumenta.VOZACKA_DOZVOLA) {
                // Proveri da li je kreiranje nove vozacke dozvole ili dodavanje kategorije
                boolean jeNovaVozackaDozvola = !vozackaDozvolaRepository.existsByDocumentUserJmbg(zahtev.getGradjaninJmbg());
                
                kreirajVozackuDozvolu(zahtev);
                
                // DODATO: obaveštenje
                String naslov = jeNovaVozackaDozvola ? "Zahtev za vozacku dozvolu je odobren" : "Zahtev za dodavanje kategorije je odobren";
                String poruka = jeNovaVozackaDozvola ? 
                    "Vaš zahtev za kreiranje vozacke dozvole je uspešno odobren." :
                    "Vaš zahtev za dodavanje kategorije " + zahtev.getKategorija() + " je uspešno odobren.";
                
                kreirajObavestenje(
                        zahtev.getGradjaninJmbg(),
                        naslov,
                        poruka + (odobravanjeDTO.getKomentar() != null ? " Komentar: " + odobravanjeDTO.getKomentar() : ""),
                        NotificationType.ZAHTEV_ODOBREN,
                        zahtev.getId()
                );
            }
        } else if (odobravanjeDTO.getStatus() == StatusZahteva.ODBIJEN) {
            // DODATO: obaveštenje za odbijeni zahtev
            String tipDokumentaStr = "";
            if (zahtev.getTipDokumenta() == TipDokumenta.LICNA_KARTA) {
                tipDokumentaStr = "ličnu kartu";
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
                tipDokumentaStr = "produženje lične karte";
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PASOS) {
                tipDokumentaStr = "pasoš";
            } else if (zahtev.getTipDokumenta() == TipDokumenta.VOZACKA_DOZVOLA) {
                // Proveri da li je kreiranje nove vozacke dozvole ili dodavanje kategorije
                boolean jeNovaVozackaDozvola = !vozackaDozvolaRepository.existsByDocumentUserJmbg(zahtev.getGradjaninJmbg());
                tipDokumentaStr = jeNovaVozackaDozvola ? "vozacku dozvolu" : "dodavanje kategorije " + zahtev.getKategorija();
            }

            kreirajObavestenje(
                    zahtev.getGradjaninJmbg(),
                    "Zahtev je odbijen",
                    "Vaš zahtev za " + tipDokumentaStr + " je odbijen. " +
                            (odobravanjeDTO.getKomentar() != null ? "Razlog: " + odobravanjeDTO.getKomentar() : "Nije naveden razlog."),
                    NotificationType.ZAHTEV_ODBIJEN,
                    zahtev.getId()
            );
        }

        zahtev = zahtevRepository.save(zahtev);

        return convertToDTO(zahtev);
    }

    private void kreirajLicnuKartu(Zahtev zahtev) {
        if (zahtev.getTipDokumenta() == TipDokumenta.LICNA_KARTA) {
            User gradjanin = authServiceClient.getUserByJmbg(zahtev.getGradjaninJmbg());

            if (gradjanin == null) {
                throw new RuntimeException("Korisnik sa JMBG-om " + zahtev.getGradjaninJmbg() + " nije pronađen");
            }

            // Kreiraj Document
            Document document = new Document();
            document.setName(gradjanin.getName());
            document.setLastname(gradjanin.getLastname());
            document.setBirthday(gradjanin.getBirthday());
            document.setPlaceOfBirth(gradjanin.getPlaceOfBirth());
            document.setUserJmbg(gradjanin.getJmbg());
            document.setCreatedAt(LocalDateTime.now().toLocalDate());
            document.setExpiresAt(LocalDateTime.now().plusYears(10).toLocalDate());

            // Kreiraj Ličnu Kartu
            LicnaKarta licnaKarta = new LicnaKarta();
            licnaKarta.setDocument(document);
            licnaKarta.setGender(gradjanin.getGender());
            licnaKarta.setJmbg(gradjanin.getJmbg());
            licnaKarta.setBrojLicneKarte(generisiBrojLicneKarte());

            licnaKartaRepository.save(licnaKarta);
        }
    }

    private void produziLicnuKartu(Zahtev zahtev) {
        if (zahtev.getTipDokumenta() == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
            User gradjanin = authServiceClient.getUserByJmbg(zahtev.getGradjaninJmbg());

            if (gradjanin == null) {
                throw new RuntimeException("Korisnik sa JMBG-om " + zahtev.getGradjaninJmbg() + " nije pronađen");
            }

            // Pronađi postojeću ličnu kartu
            List<LicnaKarta> postojeceLicneKarte = licnaKartaRepository.findByJmbg(zahtev.getGradjaninJmbg());
            if (postojeceLicneKarte.isEmpty()) {
                throw new RuntimeException("Korisnik nema ličnu kartu za produženje");
            }

            // Ažuriraj postojeću ličnu kartu
            LicnaKarta postojeceLicnaKarta = postojeceLicneKarte.get(0);
            Document postojeciDocument = postojeceLicnaKarta.getDocument();
            postojeciDocument.setCreatedAt(LocalDateTime.now().toLocalDate()); // Nova datum kreiranja
            postojeciDocument.setExpiresAt(LocalDateTime.now().plusYears(10).toLocalDate()); // Nova datum isteka

            licnaKartaRepository.save(postojeceLicnaKarta);
        }
    }

    private void kreirajPasos(Zahtev zahtev) {
        if (zahtev.getTipDokumenta() == TipDokumenta.PASOS) {
            User gradjanin = authServiceClient.getUserByJmbg(zahtev.getGradjaninJmbg());

            if (gradjanin == null) {
                throw new RuntimeException("Korisnik sa JMBG-om " + zahtev.getGradjaninJmbg() + " nije pronađen");
            }

            // Kreiraj Document
            Document document = new Document();
            document.setName(gradjanin.getName());
            document.setLastname(gradjanin.getLastname());
            document.setBirthday(gradjanin.getBirthday());
            document.setPlaceOfBirth(gradjanin.getPlaceOfBirth());
            document.setUserJmbg(gradjanin.getJmbg());
            document.setCreatedAt(LocalDateTime.now().toLocalDate());
            document.setExpiresAt(LocalDateTime.now().plusYears(10).toLocalDate());

            // Kreiraj Pasoš
            Pasos pasos = new Pasos();
            pasos.setDocument(document);
            pasos.setGender(gradjanin.getGender());
            pasos.setDrzavljanstvo(gradjanin.getJmbg());
            pasos.setBrojPasosa(generisiBrojPasosa());

            pasosRepository.save(pasos);
        }
    }

    private void kreirajVozackuDozvolu(Zahtev zahtev) {
        if (zahtev.getTipDokumenta() == TipDokumenta.VOZACKA_DOZVOLA) {
            User gradjanin = authServiceClient.getUserByJmbg(zahtev.getGradjaninJmbg());

            if (gradjanin == null) {
                throw new RuntimeException("Korisnik sa JMBG-om " + zahtev.getGradjaninJmbg() + " nije pronađen");
            }

            // Proveri da li korisnik već ima vozacku dozvolu
            if (vozackaDozvolaRepository.existsByDocumentUserJmbg(zahtev.getGradjaninJmbg())) {
                // Dodaj kategoriju na postojeću vozacku dozvolu
                dodajKategorijuNaPostojecuVozackuDozvolu(zahtev);
            } else {
                // Kreiraj novu vozacku dozvolu
                kreirajNovuVozackuDozvolu(zahtev, gradjanin);
            }
        }
    }

    private void kreirajNovuVozackuDozvolu(Zahtev zahtev, User gradjanin) {
        // Kreiraj Document
        Document document = new Document();
        document.setName(gradjanin.getName());
        document.setLastname(gradjanin.getLastname());
        document.setBirthday(gradjanin.getBirthday());
        document.setPlaceOfBirth(gradjanin.getPlaceOfBirth());
        document.setUserJmbg(gradjanin.getJmbg());
        document.setCreatedAt(LocalDateTime.now().toLocalDate());
        document.setExpiresAt(LocalDateTime.now().plusYears(10).toLocalDate());

        // Kreiraj Vozacku Dozvolu sa odabranom kategorijom
        VozackaDozvola vozackaDozvola = new VozackaDozvola();
        vozackaDozvola.setDocument(document);
        
        // Dodaj odabranu kategoriju iz zahteva
        if (zahtev.getKategorija() != null) {
            vozackaDozvola.dodajKategoriju(zahtev.getKategorija());
        } else {
            throw new RuntimeException("Kategorija nije odabrana za vozacku dozvolu");
        }

        vozackaDozvolaRepository.save(vozackaDozvola);
    }

    private void dodajKategorijuNaPostojecuVozackuDozvolu(Zahtev zahtev) {
        // Pronađi najnoviju vozacku dozvolu
        List<VozackaDozvola> vozackeDozvole = vozackaDozvolaRepository.findByDocumentUserJmbg(zahtev.getGradjaninJmbg());
        VozackaDozvola najnovijaVozackaDozvola = vozackeDozvole.stream()
                .max((v1, v2) -> v1.getDocument().getCreatedAt().compareTo(v2.getDocument().getCreatedAt()))
                .orElse(null);

        if (najnovijaVozackaDozvola == null) {
            throw new RuntimeException("Korisnik nema vozacku dozvolu");
        }

        // Proveri da li vozacka dozvola nije istekla
        LocalDate danas = LocalDate.now();
        LocalDate datumIsteka = najnovijaVozackaDozvola.getDocument().getExpiresAt();

        if (danas.isAfter(datumIsteka)) {
            throw new RuntimeException("Vozacka dozvola je istekla. Morate prvo kreirati novu vozacku dozvolu.");
        }

        // Proveri da li korisnik već ima tu kategoriju
        if (najnovijaVozackaDozvola.imaKategoriju(zahtev.getKategorija())) {
            throw new RuntimeException("Korisnik već ima " + zahtev.getKategorija() + " kategoriju na svojoj vozackoj dozvoli.");
        }

        // Dodaj kategoriju
        najnovijaVozackaDozvola.dodajKategoriju(zahtev.getKategorija());
        vozackaDozvolaRepository.save(najnovijaVozackaDozvola);
    }

    private String generisiBrojLicneKarte() {
        // Generiši jedinstveni broj lične karte sa 5 cifara
        Random random = new Random();
        int broj = random.nextInt(90000) + 10000; // 10000-99999
        return "LK" + broj;
    }

    private String generisiBrojPasosa() {
        // Generiši jedinstveni broj pasoša sa 5 cifara
        Random random = new Random();
        int broj = random.nextInt(90000) + 10000; // 10000-99999
        return "PS" + broj;
    }

    public String validirajLicnuKartu(String jmbg) {
        List<LicnaKarta> licneKarte = licnaKartaRepository.findByJmbg(jmbg);

        if (licneKarte.isEmpty()) {
            return "Korisnik nema ličnu kartu";
        }

        // Pronađi najnoviju ličnu kartu
        LicnaKarta najnovijaLicnaKarta = licneKarte.stream()
                .max((lk1, lk2) -> lk1.getDocument().getCreatedAt().compareTo(lk2.getDocument().getCreatedAt()))
                .orElse(null);

        if (najnovijaLicnaKarta == null) {
            return "Korisnik nema ličnu kartu";
        }

        LocalDate danas = LocalDate.now();
        LocalDate datumIsteka = najnovijaLicnaKarta.getDocument().getExpiresAt();

        if (danas.isAfter(datumIsteka)) {
            return "Lična karta je istekla. Morate poslati zahtev da produžite ličnu kartu.";
        }

        return null; // Lična karta je validna
    }

    public String validirajPasos(String jmbg) {
        List<Pasos> pasosi = pasosRepository.findByDocumentUserJmbg(jmbg);

        if (pasosi.isEmpty()) {
            return "Korisnik nema pasoš";
        }

        // Pronađi najnoviji pasoš
        Pasos najnovijiPasos = pasosi.stream()
                .max((p1, p2) -> p1.getDocument().getCreatedAt().compareTo(p2.getDocument().getCreatedAt()))
                .orElse(null);

        if (najnovijiPasos == null) {
            return "Korisnik nema pasoš";
        }

        LocalDate danas = LocalDate.now();
        LocalDate datumIsteka = najnovijiPasos.getDocument().getExpiresAt();

        if (danas.isAfter(datumIsteka)) {
            return "Pasoš je istekao. Morate poslati zahtev da kreirate novi pasoš.";
        }

        return null; // Pasoš je validan
    }

    public String validirajVozackuDozvolu(String jmbg) {
        List<VozackaDozvola> vozackeDozvole = vozackaDozvolaRepository.findByDocumentUserJmbg(jmbg);

        if (vozackeDozvole.isEmpty()) {
            return "Korisnik nema vozacku dozvolu";
        }

        // Pronađi najnoviju vozacku dozvolu
        VozackaDozvola najnovijaVozackaDozvola = vozackeDozvole.stream()
                .max((v1, v2) -> v1.getDocument().getCreatedAt().compareTo(v2.getDocument().getCreatedAt()))
                .orElse(null);

        if (najnovijaVozackaDozvola == null) {
            return "Korisnik nema vozacku dozvolu";
        }

        LocalDate danas = LocalDate.now();
        LocalDate datumIsteka = najnovijaVozackaDozvola.getDocument().getExpiresAt();

        if (danas.isAfter(datumIsteka)) {
            return "Vozacka dozvola je istekla. Morate poslati zahtev da kreirate novu vozacku dozvolu.";
        }

        return null; // Vozacka dozvola je validna
    }

    public String validirajStarost(String jmbg, String tipDokumentaStr) {
        TipDokumenta tipDokumenta;
        try {
            tipDokumenta = TipDokumenta.valueOf(tipDokumentaStr);
        } catch (IllegalArgumentException e) {
            return "Nevažeći tip dokumenta";
        }

        return validirajStarost(jmbg, tipDokumenta);
    }

    private String validirajStarost(String jmbg, TipDokumenta tipDokumenta) {
        User gradjanin = authServiceClient.getUserByJmbg(jmbg);

        if (gradjanin == null) {
            return "Korisnik nije pronađen";
        }

        LocalDate danas = LocalDate.now();
        LocalDate datumRodjenja = gradjanin.getBirthday();

        if (datumRodjenja == null) {
            return "Datum rođenja nije definisan";
        }

        int godine = Period.between(datumRodjenja, danas).getYears();

        // Validacija za ličnu kartu i pasoš - minimum 10 godina
        if (tipDokumenta == TipDokumenta.LICNA_KARTA || tipDokumenta == TipDokumenta.PASOS) {
            if (godine < 10) {
                return "Morate imati najmanje 10 godina da biste kreirali " +
                        (tipDokumenta == TipDokumenta.LICNA_KARTA ? "ličnu kartu" : "pasoš") +
                        ". Trenutno imate " + godine + " godina.";
            }
        }

        // Validacija za produženje lične karte - minimum 10 godina
        if (tipDokumenta == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
            if (godine < 10) {
                return "Morate imati najmanje 10 godina da biste produžili ličnu kartu. Trenutno imate " + godine + " godina.";
            }
        }

        return null; // Starost je validna
    }

    // DODATO: helper za kreiranje obaveštenja
    private void kreirajObavestenje(String userJmbg, String title, String message, NotificationType type, Long zahtevId) {
        Notification notification = new Notification(userJmbg, title, message, type, zahtevId);
        notificationRepository.save(notification);
    }

    public void kreirajIstekluLicnuKartu(String jmbg) {
        User gradjanin = authServiceClient.getUserByJmbg(jmbg);

        if (gradjanin == null) {
            throw new RuntimeException("Korisnik sa JMBG-om " + jmbg + " nije pronađen");
        }

        // Proveri da li već postoji lična karta
        List<LicnaKarta> postojeceLicneKarte = licnaKartaRepository.findByJmbg(jmbg);
        if (!postojeceLicneKarte.isEmpty()) {
            // Ažuriraj postojeću ličnu kartu da bude istekla
            LicnaKarta postojeceLicnaKarta = postojeceLicneKarte.get(0);
            Document postojeciDocument = postojeceLicnaKarta.getDocument();
            postojeciDocument.setCreatedAt(LocalDate.now().minusYears(11)); // Kreirana pre 11 godina
            postojeciDocument.setExpiresAt(LocalDate.now().minusYears(1)); // Istekla pre 1 godine
            licnaKartaRepository.save(postojeceLicnaKarta);
            return;
        }

        // Kreiraj Document sa isteklom ličnom kartom
        Document document = new Document();
        document.setName(gradjanin.getName());
        document.setLastname(gradjanin.getLastname());
        document.setBirthday(gradjanin.getBirthday());
        document.setPlaceOfBirth(gradjanin.getPlaceOfBirth());
        document.setUserJmbg(gradjanin.getJmbg());
        document.setCreatedAt(LocalDate.now().minusYears(11)); // Kreirana pre 11 godina
        document.setExpiresAt(LocalDate.now().minusYears(1)); // Istekla pre 1 godine

        // Kreiraj Ličnu Kartu
        LicnaKarta licnaKarta = new LicnaKarta();
        licnaKarta.setDocument(document);
        licnaKarta.setGender(gradjanin.getGender());
        licnaKarta.setJmbg(gradjanin.getJmbg());
        licnaKarta.setBrojLicneKarte(generisiBrojLicneKarte());

        licnaKartaRepository.save(licnaKarta);
    }

    public List<ZahtevDTO> getZahteviZaGradjanina(String jmbg) {
        User gradjanin = authServiceClient.getUserByJmbg(jmbg);

        if (gradjanin == null) {
            throw new RuntimeException("Korisnik sa JMBG-om " + jmbg + " nije pronađen");
        }

        List<Zahtev> zahtevi = zahtevRepository.findByGradjaninJmbg(jmbg);

        return zahtevi.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ZahtevDTO> getZahteviNaCekanju() {
        List<Zahtev> zahtevi = zahtevRepository.findByStatus(StatusZahteva.NA_CEKANJU);

        return zahtevi.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ZahtevDTO getZahtevById(Long id) {
        Zahtev zahtev = zahtevRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zahtev sa ID " + id + " nije pronađen"));

        return convertToDTO(zahtev);
    }

    public List<Document> getDokumentiZaKorisnika(String jmbg) {
        List<Document> dokumenti = new ArrayList<>();

        // Dodaj lične karte
        List<Document> licneKarte = licnaKartaRepository.findByJmbg(jmbg).stream()
                .map(licnaKarta -> {
                    Document doc = licnaKarta.getDocument();
                    // Dodaj dodatne podatke o ličnoj karti
                    doc.setTipDokumenta("LICNA_KARTA");
                    doc.setBrojDokumenta(licnaKarta.getBrojLicneKarte());
                    return doc;
                })
                .collect(Collectors.toList());

        // Dodaj pasoše
        List<Document> pasosi = pasosRepository.findByDocumentUserJmbg(jmbg).stream()
                .map(pasos -> {
                    Document doc = pasos.getDocument();
                    // Dodaj dodatne podatke o pasošu
                    doc.setTipDokumenta("PASOS");
                    doc.setBrojDokumenta(pasos.getBrojPasosa());
                    return doc;
                })
                .collect(Collectors.toList());

        // Dodaj vozacke dozvole
        List<Document> vozackeDozvole = vozackaDozvolaRepository.findByDocumentUserJmbg(jmbg).stream()
                .map(vozackaDozvola -> {
                    Document doc = vozackaDozvola.getDocument();
                    // Dodaj dodatne podatke o vozackoj dozvoli
                    doc.setTipDokumenta("VOZACKA_DOZVOLA");
                    doc.setBrojDokumenta("VD" + vozackaDozvola.getId());
                    // Dodaj kategorije kao string
                    String kategorijeStr = vozackaDozvola.getKategorije().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));
                    doc.setKategorije(kategorijeStr);
                    return doc;
                })
                .collect(Collectors.toList());

        dokumenti.addAll(licneKarte);
        dokumenti.addAll(pasosi);
        dokumenti.addAll(vozackeDozvole);

        return dokumenti;
    }

    private ZahtevDTO convertToDTO(Zahtev zahtev) {
        ZahtevDTO dto = new ZahtevDTO();
        dto.setId(zahtev.getId());
        dto.setGradjaninJmbg(zahtev.getGradjaninJmbg());

        // Dohvati podatke o građaninu iz auth servisa
        User gradjanin = authServiceClient.getUserByJmbg(zahtev.getGradjaninJmbg());
        if (gradjanin != null) {
            dto.setGradjaninIme(gradjanin.getName());
            dto.setGradjaninPrezime(gradjanin.getLastname());
        }

        if (zahtev.getPolicajacJmbg() != null) {
            dto.setPolicajacJmbg(zahtev.getPolicajacJmbg());

            // Dohvati podatke o policajcu iz auth servisa
            User policajac = authServiceClient.getUserByJmbg(zahtev.getPolicajacJmbg());
            if (policajac != null) {
                dto.setPolicajacIme(policajac.getName());
                dto.setPolicajacPrezime(policajac.getLastname());
            }
        }

        dto.setStatus(zahtev.getStatus());
        dto.setTipDokumenta(zahtev.getTipDokumenta());
        dto.setKategorija(zahtev.getKategorija());
        dto.setRazlog(zahtev.getRazlog());
        dto.setKomentar(zahtev.getKomentar());
        dto.setDatumKreiranja(zahtev.getDatumKreiranja());
        dto.setDatumOdobrenja(zahtev.getDatumOdobrenja());

        return dto;
    }

    // DODATO: javne metode za obaveštenja
    public List<Notification> getObavestenjaZaKorisnika(String jmbg) {
        return notificationRepository.findByUserJmbgOrderByCreatedAtDesc(jmbg);
    }

    public List<Notification> getNeprocitanaObavestenja(String jmbg) {
        return notificationRepository.findByUserJmbgAndIsReadFalseOrderByCreatedAtDesc(jmbg);
    }

    public long getBrojNeprocitanihObavestenja(String jmbg) {
        return notificationRepository.countByUserJmbgAndIsReadFalse(jmbg);
    }

    public void oznaciKaoProcitanu(Long notificationId) {
        notificationRepository.markAsReadById(notificationId);
    }

    public void oznaciSveKaoProcitane(String jmbg) {
        notificationRepository.markAllAsReadByUserJmbg(jmbg);
    }

    public void dodajKategorijuNaVozackuDozvolu(String jmbg, DodavanjeKategorijeDTO dodavanjeKategorijeDTO) {
        // Proveri da li korisnik ima vozacku dozvolu
        List<VozackaDozvola> vozackeDozvole = vozackaDozvolaRepository.findByDocumentUserJmbg(jmbg);
        
        if (vozackeDozvole.isEmpty()) {
            throw new RuntimeException("Korisnik nema vozacku dozvolu. Morate prvo kreirati vozacku dozvolu.");
        }

        // Pronađi najnoviju vozacku dozvolu
        VozackaDozvola najnovijaVozackaDozvola = vozackeDozvole.stream()
                .max((v1, v2) -> v1.getDocument().getCreatedAt().compareTo(v2.getDocument().getCreatedAt()))
                .orElse(null);

        if (najnovijaVozackaDozvola == null) {
            throw new RuntimeException("Korisnik nema vozacku dozvolu");
        }

        // Proveri da li vozacka dozvola nije istekla
        LocalDate danas = LocalDate.now();
        LocalDate datumIsteka = najnovijaVozackaDozvola.getDocument().getExpiresAt();

        if (danas.isAfter(datumIsteka)) {
            throw new RuntimeException("Vozacka dozvola je istekla. Morate prvo kreirati novu vozacku dozvolu.");
        }

        // Proveri da li korisnik već ima tu kategoriju
        if (najnovijaVozackaDozvola.imaKategoriju(dodavanjeKategorijeDTO.getKategorija())) {
            throw new RuntimeException("Korisnik već ima " + dodavanjeKategorijeDTO.getKategorija() + " kategoriju na svojoj vozackoj dozvoli.");
        }

        // Proveri starost za novu kategoriju
        String validacijaStarosti = validirajStarostZaKategoriju(jmbg, dodavanjeKategorijeDTO.getKategorija());
        if (validacijaStarosti != null) {
            throw new RuntimeException(validacijaStarosti);
        }

        // Dodaj kategoriju
        najnovijaVozackaDozvola.dodajKategoriju(dodavanjeKategorijeDTO.getKategorija());
        vozackaDozvolaRepository.save(najnovijaVozackaDozvola);

        // Kreiraj obaveštenje
        kreirajObavestenje(
                jmbg,
                "Kategorija je dodana na vozacku dozvolu",
                "Kategorija " + dodavanjeKategorijeDTO.getKategorija() + " je uspešno dodana na vašu vozacku dozvolu. " +
                        (dodavanjeKategorijeDTO.getRazlog() != null ? "Razlog: " + dodavanjeKategorijeDTO.getRazlog() : ""),
                NotificationType.ZAHTEV_ODOBREN,
                null
        );
    }

    public String validirajStarostZaKategoriju(String jmbg, String kategorijaStr) {
        Kategorija kategorija;
        try {
            kategorija = Kategorija.valueOf(kategorijaStr);
        } catch (IllegalArgumentException e) {
            return "Nevažeća kategorija";
        }
        
        return validirajStarostZaKategoriju(jmbg, kategorija);
    }

    private String validirajStarostZaKategoriju(String jmbg, Kategorija kategorija) {
        User gradjanin = authServiceClient.getUserByJmbg(jmbg);

        if (gradjanin == null) {
            return "Korisnik nije pronađen";
        }

        LocalDate danas = LocalDate.now();
        LocalDate datumRodjenja = gradjanin.getBirthday();

        if (datumRodjenja == null) {
            return "Datum rođenja nije definisan";
        }

        int godine = Period.between(datumRodjenja, danas).getYears();

        // Validacija za A kategoriju - minimum 16 godina
        if (kategorija == Kategorija.A && godine < 16) {
            return "Morate imati najmanje 16 godina da biste dodali A kategoriju. Trenutno imate " + godine + " godina.";
        }

        // Validacija za B kategoriju - minimum 18 godina
        if (kategorija == Kategorija.B && godine < 18) {
            return "Morate imati najmanje 18 godina da biste dodali B kategoriju. Trenutno imate " + godine + " godina.";
        }

        // Validacija za C i D kategorije - minimum 21 godina
        if ((kategorija == Kategorija.C || kategorija == Kategorija.D) && godine < 21) {
            return "Morate imati najmanje 21 godinu da biste dodali " + kategorija + " kategoriju. Trenutno imate " + godine + " godina.";
        }

        return null; // Starost je validna
    }
}
