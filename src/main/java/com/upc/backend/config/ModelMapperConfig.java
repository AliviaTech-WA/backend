package com.upc.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Infraestructura común: conserva el ModelMapper utilizado por servicios y conversiones DTO-entidad.
public class ModelMapperConfig {

    @Bean
    // Infraestructura común: expone una única instancia de ModelMapper para inyección.
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
