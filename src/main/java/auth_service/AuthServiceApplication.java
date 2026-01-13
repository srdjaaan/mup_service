package auth_service;

import auth_service.model.*;
import auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

//    @Bean
//    CommandLineRunner runner(UserRepository userRepository) {
//        return args -> {
//            User user = new User();
//            user.setJmbg("0201003555444");
//            user.setName("Natasa");
//            user.setLastname("Petkovic");
//            user.setUsername("nata");
//            user.setPassword("nata123");
//            user.setPlaceOfBirth("Backa Palanka");
//            user.setRole(Role.GRADJANIN);
//            user.setGender(Gender.ZENSKI);
//
//            userRepository.save(user);
//
//            System.out.println("Sacuvan korisnik: " + userRepository.findByUsername("nata").get().getName());
//        };
//    }
}
