package at.undok.common.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrossOriginConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://testprecarioa.undok.at", "http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }

}
