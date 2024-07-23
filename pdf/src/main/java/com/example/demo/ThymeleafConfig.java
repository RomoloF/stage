package com.example.demo;

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


	@Configuration
public class ThymeleafConfig {

    @Bean
    ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML"); // Usa "HTML" invece di "HTML5"
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
}
