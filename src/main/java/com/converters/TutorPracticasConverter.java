package com.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.control.practicas.models.TutorPracticas;

@Component
public class TutorPracticasConverter implements Converter<String, TutorPracticas> {

    @Override
    public TutorPracticas convert(String source) {
        if (source == null || source.trim().isEmpty() || source.equals("0")) {
            return null;
        }
        TutorPracticas tutor = new TutorPracticas();
        tutor.setId(Long.parseLong(source));
        return tutor;
    }
}
