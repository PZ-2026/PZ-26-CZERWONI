package pl.edu.ur.teachly.support;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Shared Testcontainers settings for integration tests. */
final class PostgresTestSupport {

    static final String IMAGE_NAME = resolveImageName();

    private PostgresTestSupport() {}

    static PostgreSQLContainer<?> newContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse(IMAGE_NAME))
                .withDatabaseName("teachly")
                .withUsername("postgres")
                .withPassword("postgres");
    }

    private static String resolveImageName() {
        String fromEnv = System.getenv("TESTCONTAINERS_POSTGRES_IMAGE");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return System.getProperty("testcontainers.postgres.image", "postgres:18-alpine");
    }
}
