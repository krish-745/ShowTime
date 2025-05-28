package com.bookmyshow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Info info = new Info();
        info.setTitle("ShowTime");
        info.setSummary("This application is build to demonstrate the capabilities to create shows for movies, reserve seats for shows etc.");
        info.setVersion("0.0.1");
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        return openAPI;
    }


}
