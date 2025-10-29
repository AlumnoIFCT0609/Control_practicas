package com.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.control.practicas.models.Curso;

@Component
public class CursoConverter implements Converter<String, Curso> {

    @Override
    public Curso convert(String source) {
        if (source == null || source.trim().isEmpty() || source.equals("0")) {
            return null;
        }
        Curso curso = new Curso();
        curso.setId(Long.parseLong(source));
        return curso;
    }
}

