package at.undok.it;

import at.undok.it.cucumber.UndokTestData;
import at.undok.it.cucumber.auth.AuthRestApiClient;
import com.github.javafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Locale;

@TestConfiguration
public class IntegrationTestConfiguration {
    private static final int DEFAULT_SERVER_PORT = 8080;

    @Bean
    Faker faker() {
        return new Faker(Locale.GERMANY);
    }

    @Bean
    UndokTestData testData() {
        return new UndokTestData(faker());
    }

    @Bean
    TestRestTemplate testRestTemplate() {
        var restTemplateBuilder = new RestTemplateBuilder()
                .requestFactory(HttpComponentsClientHttpRequestFactory::new);

        return new TestRestTemplate(restTemplateBuilder);
    }

    @Bean
    AuthRestApiClient authRestApiClient(TestRestTemplate testRestTemplate) {
        return new AuthRestApiClient(testRestTemplate, DEFAULT_SERVER_PORT);
    }

}
