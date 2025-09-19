package mup.repository;

import mup.model.Pasos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasosRepository extends JpaRepository<Pasos, Long> {
    
    List<Pasos> findByDrzavljanstvo(String drzavljanstvo);
    
    Optional<Pasos> findByBrojPasosa(String brojPasosa);
    
    List<Pasos> findByDocumentUserJmbg(String jmbg);
    
    boolean existsByDrzavljanstvo(String drzavljanstvo);
    
    boolean existsByBrojPasosa(String brojPasosa);
}
