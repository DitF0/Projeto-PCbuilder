package br.edu.eseg.pcbuilder.repository;

import br.edu.eseg.pcbuilder.model.Configuracao;
import br.edu.eseg.pcbuilder.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    List<Configuracao> findByUsuario(Usuario usuario);
}
