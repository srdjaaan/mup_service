package mup.controller;

import mup.DTO.KreiranjeZahtevaDTO;
import mup.DTO.OdobravanjeZahtevaDTO;
import mup.DTO.ZahtevDTO;
import mup.DTO.DodavanjeKategorijeDTO;
import mup.model.Document;
import mup.model.Notification;
import mup.service.ZahtevService;
import mup.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zahtevi")
@CrossOrigin(origins = "*")
public class ZahtevController {
    
    @Autowired
    private ZahtevService zahtevService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/kreiraj")
    public ResponseEntity<?> kreirajZahtev(@RequestBody KreiranjeZahtevaDTO kreiranjeZahtevaDTO, 
                                         HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }
            
            String jmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (!"GRADJANIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo građani mogu kreirati zahteve");
            }
            
            ZahtevDTO zahtev = zahtevService.kreirajZahtev(jmbg, kreiranjeZahtevaDTO);
            return ResponseEntity.ok(zahtev);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/{zahtevId}/odobri")
    public ResponseEntity<?> odobriZahtev(@PathVariable Long zahtevId, 
                                        @RequestBody OdobravanjeZahtevaDTO odobravanjeDTO,
                                        HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }
            
            String jmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (!"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo policajci mogu odobravati zahteve");
            }
            
            ZahtevDTO zahtev = zahtevService.odobriZahtev(zahtevId, jmbg, odobravanjeDTO);
            return ResponseEntity.ok(zahtev);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/moji-zahtevi")
    public ResponseEntity<?> getMojiZahtevi(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }
            
            String jmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (!"GRADJANIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo građani mogu videti svoje zahteve");
            }
            
            List<ZahtevDTO> zahtevi = zahtevService.getZahteviZaGradjanina(jmbg);
            return ResponseEntity.ok(zahtevi);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/na-cekanju")
    public ResponseEntity<?> getZahteviNaCekanju(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }
            
            String role = jwtUtil.getRoleFromToken(token);
            
            if (!"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo policajci mogu videti zahteve na čekanju");
            }
            
            List<ZahtevDTO> zahtevi = zahtevService.getZahteviNaCekanju();
            return ResponseEntity.ok(zahtevi);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/{zahtevId}")
    public ResponseEntity<?> getZahtevById(@PathVariable Long zahtevId, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }
            
            ZahtevDTO zahtev = zahtevService.getZahtevById(zahtevId);
            return ResponseEntity.ok(zahtev);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/korisnik/{jmbg}/dokumenti")
    public ResponseEntity<?> getDokumentiZaKorisnika(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            List<Document> dokumenti = zahtevService.getDokumentiZaKorisnika(jmbg);
            return ResponseEntity.ok(dokumenti);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/validiraj-licnu-kartu")
    public ResponseEntity<?> validirajLicnuKartu(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            String validacijskaPoruka = zahtevService.validirajLicnuKartu(jmbg);
            
            if (validacijskaPoruka == null) {
                return ResponseEntity.ok(Map.of("status", "VALIDNA", "poruka", "Lična karta je validna"));
            } else {
                return ResponseEntity.ok(Map.of("status", "NEVALIDNA", "poruka", validacijskaPoruka));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/admin/kreiraj-isteklu-licnu-kartu/{jmbg}")
    public ResponseEntity<?> kreirajIstekluLicnuKartu(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String role = jwtUtil.getRoleFromToken(token);
            if (!"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo policajci mogu kreirati istekle lične karte");
            }

            zahtevService.kreirajIstekluLicnuKartu(jmbg);
            return ResponseEntity.ok(Map.of("poruka", "Istekla lična karta je kreirana za testiranje"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/validiraj-pasos")
    public ResponseEntity<?> validirajPasos(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            String validacijskaPoruka = zahtevService.validirajPasos(jmbg);
            
            if (validacijskaPoruka == null) {
                return ResponseEntity.ok(Map.of("status", "VALIDAN", "poruka", "Pasoš je validan"));
            } else {
                return ResponseEntity.ok(Map.of("status", "NEVALIDAN", "poruka", validacijskaPoruka));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/validiraj-starost/{tipDokumenta}")
    public ResponseEntity<?> validirajStarost(@PathVariable String jmbg, @PathVariable String tipDokumenta, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            String validacijskaPoruka = zahtevService.validirajStarost(jmbg, tipDokumenta);

            if (validacijskaPoruka == null) {
                return ResponseEntity.ok(Map.of("status", "VALIDNA", "poruka", "Starost je validna za kreiranje dokumenta"));
            } else {
                return ResponseEntity.ok(Map.of("status", "NEVALIDNA", "poruka", validacijskaPoruka));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/validiraj-vozacku-dozvolu")
    public ResponseEntity<?> validirajVozackuDozvolu(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            String validacijskaPoruka = zahtevService.validirajVozackuDozvolu(jmbg);

            if (validacijskaPoruka == null) {
                return ResponseEntity.ok(Map.of("status", "VALIDNA", "poruka", "Vozacka dozvola je validna"));
            } else {
                return ResponseEntity.ok(Map.of("status", "NEVALIDNA", "poruka", validacijskaPoruka));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/validiraj-starost-za-kategoriju/{kategorija}")
    public ResponseEntity<?> validirajStarostZaKategoriju(@PathVariable String jmbg, @PathVariable String kategorija, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            // Proveri da li korisnik traži svoje dokumente ili je policajac
            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoje dokumente");
            }

            String validacijskaPoruka = zahtevService.validirajStarostZaKategoriju(jmbg, kategorija);

            if (validacijskaPoruka == null) {
                return ResponseEntity.ok(Map.of("status", "VALIDNA", "poruka", "Starost je validna za " + kategorija + " kategoriju"));
            } else {
                return ResponseEntity.ok(Map.of("status", "NEVALIDNA", "poruka", validacijskaPoruka));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Notification endpoints
    @GetMapping("/korisnik/{jmbg}/obavestenja")
    public ResponseEntity<?> getObavestenja(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoja obaveštenja");
            }

            List<Notification> obavestenja = zahtevService.getObavestenjaZaKorisnika(jmbg);
            return ResponseEntity.ok(obavestenja);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/obavestenja/neprocitana")
    public ResponseEntity<?> getNeprocitanaObavestenja(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoja obaveštenja");
            }

            List<Notification> obavestenja = zahtevService.getNeprocitanaObavestenja(jmbg);
            return ResponseEntity.ok(obavestenja);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/korisnik/{jmbg}/obavestenja/broj-neprocitanih")
    public ResponseEntity<?> getBrojNeprocitanihObavestenja(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete videti samo svoja obaveštenja");
            }

            long broj = zahtevService.getBrojNeprocitanihObavestenja(jmbg);
            return ResponseEntity.ok(Map.of("brojNeprocitanih", broj));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/obavestenja/{notificationId}/oznaci-procitanu")
    public ResponseEntity<?> oznaciKaoProcitanu(@PathVariable Long notificationId, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            zahtevService.oznaciKaoProcitanu(notificationId);
            return ResponseEntity.ok(Map.of("poruka", "Obaveštenje je označeno kao pročitano"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/korisnik/{jmbg}/obavestenja/oznaci-sve-procitane")
    public ResponseEntity<?> oznaciSveKaoProcitane(@PathVariable String jmbg, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete označiti samo svoja obaveštenja");
            }

            zahtevService.oznaciSveKaoProcitane(jmbg);
            return ResponseEntity.ok(Map.of("poruka", "Sva obaveštenja su označena kao pročitana"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/korisnik/{jmbg}/dodaj-kategoriju")
    public ResponseEntity<?> dodajKategoriju(@PathVariable String jmbg, @RequestBody DodavanjeKategorijeDTO dodavanjeKategorijeDTO, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nevažeći token");
            }

            String tokenJmbg = jwtUtil.getJmbgFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (!jmbg.equals(tokenJmbg) && !"POLICAJAC".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Možete dodati kategoriju samo na svoju vozacku dozvolu");
            }

            if (!"GRADJANIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Samo građani mogu dodavati kategorije");
            }

            zahtevService.dodajKategorijuNaVozackuDozvolu(jmbg, dodavanjeKategorijeDTO);
            return ResponseEntity.ok(Map.of("poruka", "Kategorija je uspešno dodana na vozacku dozvolu"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/document/all")
    public ResponseEntity<?> getDokumenti() {
        try {
            List<Document> dokumenti = zahtevService.getSviDokumenti();
            return ResponseEntity.ok(dokumenti);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

