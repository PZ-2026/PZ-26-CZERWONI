package pl.edu.ur.teachly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeachlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachlyApplication.class, args);
    }
}
