package mup.controller;

import mup.DTO.KreiranjeZahtevaDTO;
import mup.DTO.OdobravanjeZahtevaDTO;
import mup.DTO.ZahtevDTO;
import mup.model.Document;
import mup.service.ZahtevService;
import mup.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

