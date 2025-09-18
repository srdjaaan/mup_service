package auth_service.service;

import auth_service.DTO.UserDTO;
import auth_service.DTO.UserWithDocumentsDTO;
import auth_service.DTO.DocumentInfoDTO;
import auth_service.model.User;
import auth_service.repository.UserRepository;
import auth_service.security.JwtUtil;
import mup.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Autowired
    private RestTemplate restTemplate;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username već postoji!"); });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getJmbg());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserByJmbg(String jmbg) {
        return userRepository.findById(jmbg)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Korisnik sa ovim ID ne postoji"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public User getUserObjectByJmbg(String jmbg) {
        return userRepository.findById(jmbg)
                .orElseThrow(() -> new RuntimeException("Korisnik sa ovim JMBG ne postoji"));
    }
    
    public UserWithDocumentsDTO getUserWithDocumentsByJmbg(String jmbg) {
        User user = userRepository.findById(jmbg)
                .orElseThrow(() -> new RuntimeException("Korisnik sa ovim JMBG ne postoji"));
        
        // Dohvati dokumenta iz MUP servisa
        List<DocumentInfoDTO> documentDTOs = getDokumentiIzMupServisa(jmbg);
        
        return new UserWithDocumentsDTO(
                user.getJmbg(),
                user.getName(),
                user.getLastname(),
                user.getUsername(),
                user.getBirthday(),
                user.getPlaceOfBirth(),
                user.getRole(),
                user.getGender(),
                documentDTOs
        );
    }
    
    private List<DocumentInfoDTO> getDokumentiIzMupServisa(String jmbg) {
        try {
            String url = "http://mup-service:8081/api/zahtevi/korisnik/" + jmbg + "/dokumenti";
            
            // Kreiraj JWT token za komunikaciju sa MUP servisom
            String token = jwtUtil.generateToken("system", "POLICAJAC", "0000000000000");
            
            // Kreiraj HTTP headers sa JWT token-om
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            
            // Pošalji zahtev sa headers
            org.springframework.http.ResponseEntity<Document[]> response = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                Document[].class
            );
            
            Document[] documents = response.getBody();
            
            if (documents == null) {
                return List.of();
            }
            
            return List.of(documents).stream()
                    .map(this::mapDocumentToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Ako ne može da dohvati dokumenta, vraća praznu listu
            System.err.println("Greška pri dohvatanju dokumenata: " + e.getMessage());
            return List.of();
        }
    }
    
    private DocumentInfoDTO mapDocumentToDTO(Document document) {
        return new DocumentInfoDTO(
                document.getId(),
                document.getName(),
                document.getLastname(),
                document.getBirthday(),
                document.getPlaceOfBirth(),
                document.getCreatedAt(),
                document.getExpiresAt(),
                document.getTipDokumenta(),
                document.getBrojDokumenta()
        );
    }

    // Privatna metoda za mapiranje User -> UserDTO
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getJmbg(),
                user.getName(),
                user.getLastname(),
                user.getUsername(),
                user.getBirthday(),
                user.getPlaceOfBirth(),
                user.getRole(),
                user.getGender()
        );
    }
}