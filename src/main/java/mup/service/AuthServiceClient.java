package mup.service;

import auth_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String AUTH_SERVICE_URL = "http://auth-service:8080";
    
    public User getUserByJmbg(String jmbg) {
        try {
            String url = AUTH_SERVICE_URL + "/api/auth/user/object/" + jmbg;
            return restTemplate.getForObject(url, User.class);
        } catch (Exception e) {
            System.err.println("Greška pri dohvatanju korisnika: " + e.getMessage());
            return null;
        }
    }
    
    public User getUserByUsername(String username) {
        try {
            String url = AUTH_SERVICE_URL + "/api/auth/user/username/" + username;
            return restTemplate.getForObject(url, User.class);
        } catch (Exception e) {
            System.err.println("Greška pri dohvatanju korisnika: " + e.getMessage());
            return null;
        }
    }
}
