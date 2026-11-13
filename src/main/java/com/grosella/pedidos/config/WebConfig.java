package com.grosella.pedidos.config;

import com.grosella.pedidos.security.SesionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;
    private final String[] origenesPermitidos;

    public WebConfig(SesionInterceptor sesionInterceptor,
                     @Value("${app.cors.allowed-origins}") String[] origenesPermitidos) {
        this.sesionInterceptor = sesionInterceptor;
        this.origenesPermitidos = origenesPermitidos;
    }

    /** Permite que el frontend Angular (localhost:4200) consuma la API. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
    }
}
