package mup.service;

import mup.DTO.KreiranjeZahtevaDTO;
import mup.DTO.OdobravanjeZahtevaDTO;
import mup.DTO.ZahtevDTO;
import mup.model.*;
import mup.repository.ZahtevRepository;
import mup.repository.LicnaKartaRepository;
import mup.repository.PasosRepository;
import auth_service.model.User;
import auth_service.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        
        Zahtev zahtev = new Zahtev(gradjanin.getJmbg(), kreiranjeZahtevaDTO.getTipDokumenta(), kreiranjeZahtevaDTO.getRazlog());
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
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PRODUZENJE_LICNE_KARTE) {
                produziLicnuKartu(zahtev);
            } else if (zahtev.getTipDokumenta() == TipDokumenta.PASOS) {
                kreirajPasos(zahtev);
            }
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
        
        dokumenti.addAll(licneKarte);
        dokumenti.addAll(pasosi);
        
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
        dto.setRazlog(zahtev.getRazlog());
        dto.setKomentar(zahtev.getKomentar());
        dto.setDatumKreiranja(zahtev.getDatumKreiranja());
        dto.setDatumOdobrenja(zahtev.getDatumOdobrenja());
        
        return dto;
    }
}

