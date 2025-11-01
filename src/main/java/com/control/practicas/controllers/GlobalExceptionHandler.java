package com.control.practicas.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        // Pasamos el mensaje de error al modelo (por si quieres usarlo)
        model.addAttribute("error", ex.getMessage());
        // Redirige a la plantilla Thymeleaf personalizada
        
        model.addAttribute("viewName", "error");
        return "layout"; // Nombre del HTML sin extensión
    }
}