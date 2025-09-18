package mup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {"mup.model", "auth_service.model"})
@ComponentScan(basePackages = {"mup"})
public class MupServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MupServiceApplication.class, args);
    }
}
