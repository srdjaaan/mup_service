package auth_service.repository;

import auth_service.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentReository extends JpaRepository<Document, Long> {
}
