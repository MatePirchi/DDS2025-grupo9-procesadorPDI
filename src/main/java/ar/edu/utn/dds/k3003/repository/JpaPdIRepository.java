package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.PdI;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test")
public interface JpaPdIRepository extends JpaRepository<PdI, String>, PdIRepository {

    PdI save(PdI pdi);

    Optional<PdI> findById(String id);

    List<PdI> findByHechoId(String hechoId);

    List<PdI> findAll();
}
