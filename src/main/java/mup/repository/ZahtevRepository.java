package mup.repository;

import mup.model.Zahtev;
import mup.model.StatusZahteva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZahtevRepository extends JpaRepository<Zahtev, Long> {
    
    List<Zahtev> findByGradjaninJmbg(String gradjaninJmbg);
    
    List<Zahtev> findByStatus(StatusZahteva status);
    
    List<Zahtev> findByGradjaninJmbgAndStatus(String gradjaninJmbg, StatusZahteva status);
    
    List<Zahtev> findByPolicajacJmbg(String policajacJmbg);
}

