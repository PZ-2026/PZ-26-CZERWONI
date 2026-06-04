package pl.edu.ur.teachly.support;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton PostgreSQL container for integration tests. Started before Spring context (via {@link
 * PostgresIntegrationTest}) and stopped in {@code @AfterAll} — not try-with-resources, because the
 * container must stay up for the whole test class.
 */
final class PostgresContainerHolder {

    private static PostgreSQLContainer<?> container;

    private PostgresContainerHolder() {}

    static PostgreSQLContainer<?> getStarted() {
        if (container == null) {
            container = createAndStart();
        }
        return container;
    }

    static void stopIfRunning() {
        if (container != null) {
            container.stop();
            container = null;
        }
    }

    @SuppressWarnings("resource")
    private static PostgreSQLContainer<?> createAndStart() {
        PostgreSQLContainer<?> postgres = PostgresTestSupport.newContainer();
        postgres.start();
        return postgres;
    }
}
