package mup.service;

import mup.DTO.KreiranjeZahtevaDTO;
import mup.DTO.OdobravanjeZahtevaDTO;
import mup.DTO.ZahtevDTO;
import mup.model.*;
import mup.repository.ZahtevRepository;
import mup.repository.LicnaKartaRepository;
import auth_service.model.User;
import auth_service.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        
        // Ako je zahtev odobren, kreiraj ličnu kartu
        if (odobravanjeDTO.getStatus() == StatusZahteva.ODOBREN) {
            kreirajLicnuKartu(zahtev);
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
    
    private String generisiBrojLicneKarte() {
        // Generiši jedinstveni broj lične karte sa 5 cifara
        Random random = new Random();
        int broj = random.nextInt(90000) + 10000; // 10000-99999
        return "LK" + broj;
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
        return licnaKartaRepository.findByJmbg(jmbg).stream()
                .map(licnaKarta -> {
                    Document doc = licnaKarta.getDocument();
                    // Dodaj dodatne podatke o ličnoj karti
                    doc.setTipDokumenta("LICNA_KARTA");
                    doc.setBrojDokumenta(licnaKarta.getBrojLicneKarte());
                    return doc;
                })
                .collect(Collectors.toList());
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

