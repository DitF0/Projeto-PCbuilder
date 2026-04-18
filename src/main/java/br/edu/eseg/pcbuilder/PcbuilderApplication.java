package br.edu.eseg.pcbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PcbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcbuilderApplication.class, args);
    }
}
// http://localhost:8080
// http://localhost:8080/h2-console
// JDBC URL: jdbc:h2:mem:pcbuilderdb para ver o SQL tabelinhas senha em branco e nome sa