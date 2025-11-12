package com.control.practicas.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        String error = "Ha ocurrido un error inesperado";
        
        if (errorMessage != null && !errorMessage.toString().isEmpty()) {
            error = errorMessage.toString();
        }
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    error = "Error 404? Tío, no esta lo que buscas, pregúntale donde está a tu madre, ella sabe ";
                    break;
                case 403:
                    error = "Error 403? Alto ahí, no se puede pasar, enseñame el pase apátrida!";
                    break;
                case 500:
                    error = "Error 500? Error en el servidor a saber qué habrás hecho machacateclas! ";
                    break;
            }
        }
        
        model.addAttribute("error", error);
        model.addAttribute("viewName", "error");
        
        return "layout";
    }
}
