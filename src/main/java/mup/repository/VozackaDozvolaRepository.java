package mup.repository;

import mup.model.VozackaDozvola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VozackaDozvolaRepository extends JpaRepository<VozackaDozvola, Long> {
    
    // Pronađi sve vozacke dozvole za korisnika
    List<VozackaDozvola> findByDocumentUserJmbg(String jmbg);
    
    // Proveri da li korisnik ima vozacku dozvolu
    boolean existsByDocumentUserJmbg(String jmbg);
    
    // Pronađi vozacku dozvolu po JMBG-u
    Optional<VozackaDozvola> findByDocumentUserJmbgAndId(String jmbg, Long id);
    
    // Pronađi sve vozacke dozvole koje sadrže određenu kategoriju
    @Query("SELECT v FROM VozackaDozvola v WHERE v.kategorije LIKE %:kategorija%")
    List<VozackaDozvola> findByKategorijeContaining(@Param("kategorija") String kategorija);
}
