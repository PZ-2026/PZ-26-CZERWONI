package pl.edu.ur.teachly.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguracja Spring MVC dla obsługi zasobów statycznych.
 *
 * <p>Mapuje ścieżkę URL {@code /uploads/**} na lokalny katalog {@code uploads/} na dysku serwera,
 * umożliwiając serwowanie awatarów użytkowników bezpośrednio przez HTTP.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Rejestruje handler zasobów statycznych dla katalogu {@code uploads/}.
     *
     * @param registry rejestr handlerów zasobów Spring MVC
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath + "/");
    }
}
