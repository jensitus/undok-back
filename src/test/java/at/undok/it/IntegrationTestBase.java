package at.undok.it;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@ActiveProfiles("dev")
public abstract class IntegrationTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestBase.class);

    /**
     * Configuration switch to determine if docker should be used or not
     */
    private static final boolean SETUP_DOCKER_BASED_TEST_INFRASTRUCTURE = true;

    private static final DockerImageName DATABASE_IMAGE_NAME = DockerImageName.parse("postgres:11");

    static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>(DATABASE_IMAGE_NAME);

    static {
        try {

            if (SETUP_DOCKER_BASED_TEST_INFRASTRUCTURE) {
                DATABASE.start();
            }

        } catch (Throwable e) {
            LOGGER.error("Failure during static initialization of IntegrationTestBase", e);
            throw e;
        }
    }

    @DynamicPropertySource
    static void setDatabaseProperties(DynamicPropertyRegistry registry) {
        if (!SETUP_DOCKER_BASED_TEST_INFRASTRUCTURE) {
            return;
        }

        LOGGER.info("Using database {} ({}): {}, {}", DATABASE.getJdbcUrl(), DATABASE.getDatabaseName(),
                DATABASE.getUsername(), DATABASE.getPassword());
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> PostgreSQL9Dialect.class.getName());
    }

}
