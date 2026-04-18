package br.edu.eseg.pcbuilder.config;

import br.edu.eseg.pcbuilder.model.Usuario;
import br.edu.eseg.pcbuilder.repository.UsuarioRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner criarUsuariosPadrao(UsuarioRepository usuarioRepository,
                                                 PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("admin@eseg.edu.br").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Admin");
                admin.setEmail("admin@eseg.edu.br");
                admin.setSenha(passwordEncoder.encode("123456"));
                admin.setRole("ROLE_ADMIN");
                usuarioRepository.save(admin);
            }

            if (usuarioRepository.findByEmail("aluno@eseg.edu.br").isEmpty()) {
                Usuario aluno = new Usuario();
                aluno.setNome("Aluno");
                aluno.setEmail("aluno@eseg.edu.br");
                aluno.setSenha(passwordEncoder.encode("123456"));
                aluno.setRole("ROLE_USER");
                usuarioRepository.save(aluno);
            }
        };
    }
}