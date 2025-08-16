package auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String JWT_SECRET = "tajni_kljuc_za_demo_treba_biti_duzi_za_prod"; // Za produkciju 32+ char
    private static final long JWT_EXPIRATION_MS = 86_400_000; // 1 dan

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // Generiše JWT token sa username i role
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Parsira username iz tokena
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Parsira rolu iz tokena
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // Validacija tokena
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Privatna metoda za parsiranje claims
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
