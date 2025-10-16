package com.control.practicas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "repositories")
@EntityScan(basePackages = "models")
@ComponentScan(basePackages = {"com.control.practicas", "services", "controllers"})


public class ControlPracticasApplication extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(ControlPracticasApplication.class, args);
    }
}
