package com.converters;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.converters.CursoConverter;
import com.converters.EmpresaConverter;
import com.converters.TutorPracticasConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CursoConverter cursoConverter;
    private final EmpresaConverter empresaConverter;
    private final TutorPracticasConverter tutorPracticasConverter;

    public WebConfig(CursoConverter cursoConverter, EmpresaConverter empresaConverter, TutorPracticasConverter tutorPracticasConverter) {
        this.cursoConverter = cursoConverter;
        this.empresaConverter = empresaConverter;
        this.tutorPracticasConverter = tutorPracticasConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(cursoConverter);
        registry.addConverter(empresaConverter);
        registry.addConverter(tutorPracticasConverter);
    }
}

