package auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Bean za enkripciju lozinki
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Konfiguracija sigurnosnog lanca
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Isključujemo CSRF za REST API
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/users",
                                "/api/auth/user/**"
                        ).permitAll() // Dozvoljeni endpointi bez autentikacije
                        .anyRequest().authenticated()
                )
                .httpBasic(); // Koristimo osnovnu HTTP autentikaciju za ostale zahteve

        return http.build();
    }
}