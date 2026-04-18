package br.edu.eseg.pcbuilder.repository;

import br.edu.eseg.pcbuilder.model.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
}
