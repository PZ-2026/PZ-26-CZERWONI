package pl.edu.ur.teachly.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja serializera JSON Jackson.
 *
 * <p>Rejestruje moduły Java 8 (daty, czasy) i wyłącza serializację dat jako timestampy, dzięki
 * czemu {@link java.time.LocalDate} i {@link java.time.LocalDateTime} są serialozowane jako
 * łańcuchy ISO 8601 (np. {@code "2025-06-07"}).
 */
@Configuration
public class JacksonConfig {

    /**
     * Tworzy i konfiguruje {@link ObjectMapper} używany przez Spring MVC.
     *
     * @return skonfigurowany ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
