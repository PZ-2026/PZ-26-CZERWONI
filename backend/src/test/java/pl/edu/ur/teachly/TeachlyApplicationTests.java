package pl.edu.ur.teachly;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.edu.ur.teachly.support.PostgresIntegrationTest;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TeachlyApplicationTests extends PostgresIntegrationTest {

    @Test
    void contextLoads() {}
}
