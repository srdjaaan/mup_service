package mup.repository;

import mup.model.LicnaKarta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicnaKartaRepository extends JpaRepository<LicnaKarta, Long> {
    
    List<LicnaKarta> findByJmbg(String jmbg);
    
    Optional<LicnaKarta> findByBrojLicneKarte(String brojLicneKarte);
    
    List<LicnaKarta> findByDocumentUserJmbg(String jmbg);
    
    boolean existsByJmbg(String jmbg);
    
    boolean existsByBrojLicneKarte(String brojLicneKarte);
}

