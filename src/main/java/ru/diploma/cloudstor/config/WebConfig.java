package ru.diploma.cloudstor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.origins}")
    private String origins;

    @Value("${cors.headers}")
    private String allowedHeaders;

    @Value("${cors.methods}")
    private String allowedMethods;

    @Value("${cors.credentials}")
    private boolean allowCredentials;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/logout").setViewName("logout");
        registry.addViewController("/file").setViewName("file");
        registry.addViewController("/list").setViewName("list");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(allowCredentials)
                .allowedOrigins(origins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders);
    }
}