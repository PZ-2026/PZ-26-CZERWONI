package pl.edu.ur.teachly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Punkt wejścia aplikacji Teachly — platforma do zarządzania korepetycjami.
 *
 * <p>Uruchamia kontekst Spring Boot z włączonym audytingiem JPA (automatyczne wypełnianie pól
 * {@code createdAt} i {@code updatedAt}).
 */
@SpringBootApplication
@EnableJpaAuditing
public class TeachlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachlyApplication.class, args);
    }
}
