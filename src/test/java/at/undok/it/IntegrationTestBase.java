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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static at.undok.it.IntegrationTestBase.GreenMailPorts.*;

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
    private static final DockerImageName GREENMAIL_IMAGE_NAME = DockerImageName.parse("greenmail/standalone:1.6.5");

    static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>(DATABASE_IMAGE_NAME);
    static final GenericContainer<?> MAIL = new GenericContainer<>(GREENMAIL_IMAGE_NAME)
            .withExposedPorts(SMTP.port, POP3.port, IMAP.port, SMTPS.port, IMAPS.port, POP3S.port, API.port);

    static {
        try {

            if (SETUP_DOCKER_BASED_TEST_INFRASTRUCTURE) {
                DATABASE.start();
                MAIL.start();
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

    @DynamicPropertySource
    static void setMailProperties(DynamicPropertyRegistry registry) {
        if (!SETUP_DOCKER_BASED_TEST_INFRASTRUCTURE) {
            return;
        }

        var smtp = MAIL.getMappedPort(SMTP.port);
        var api = MAIL.getMappedPort(API.port);

        LOGGER.info("Using Greenmail with ports: {} (SMTP), {} (API)", smtp, api);

        registry.add("spring.mail.port", () -> smtp);
    }

    public enum GreenMailPorts {
        SMTP(3025),
        POP3(3110),
        IMAP(3143),
        SMTPS(3465),
        IMAPS(3993),
        POP3S(3995),
        API(8080);

        public final int port;

        GreenMailPorts(int port) {
            this.port = port;
        }
    }

}
