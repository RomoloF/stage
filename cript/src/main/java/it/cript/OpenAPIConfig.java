package it.cript;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@ComponentScan(basePackages = {"it.cript.controller"}) // Assicurati che questo pacchetto includa i tuoi controller
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentazione di Romolo")
                .version("1.0")
                .description("API Documentazione dell'applicazione"));
    }
}