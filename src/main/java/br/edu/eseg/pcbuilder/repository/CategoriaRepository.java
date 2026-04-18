package br.edu.eseg.pcbuilder.repository;

import br.edu.eseg.pcbuilder.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
