package com.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.control.practicas.models.Empresa;

@Component
public class EmpresaConverter implements Converter<String, Empresa> {

    @Override
    public Empresa convert(String source) {
        if (source == null || source.trim().isEmpty() || source.equals("0")) {
            return null;
        }
        Empresa empresa = new Empresa();
        empresa.setId(Long.parseLong(source));
        return empresa;
    }
}
