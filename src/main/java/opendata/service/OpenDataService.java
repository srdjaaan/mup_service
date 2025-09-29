package opendata.service;

import opendata.contract.UserDTO;
import opendata.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenDataService {

    private final RestTemplate restTemplate;

    @Value("${services.mup-url}")
    private String mupServiceUrl;

    @Value("${services.auth-url}")
    private String authServiceUrl;

    private final String username = "opendata";

    private final String password = "opendata123";


    @Autowired
    public OpenDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<UserDTO> getAllUsers() {
        String url = authServiceUrl + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    User[].class
            );
            User[] users = response.getBody();
            if (users == null) return List.of();

            return Arrays.stream(users)
                    .map(user -> new UserDTO(
                            user.getName(),
                            user.getLastname(),
                            user.getBirthday(),
                            user.getPlaceOfBirth(),
                            user.getRole(),
                            user.getGender()
                    ))
                    .toList();

        } catch (Exception e) {
            System.err.println("Greška prilikom dohvatanja korisnika: " + e.getMessage());
            throw new RuntimeException("Neuspešno dohvaćeni korisnici", e);
        }
    }

}
