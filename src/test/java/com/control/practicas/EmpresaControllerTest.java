package com.control.practicas;


import com.control.practicas.config.DataInitializer;
import com.control.practicas.controllers.EmpresaController;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.services.EmpresaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = EmpresaController.class,
excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.control.practicas.config.DataInitializer.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.control.practicas.config.SecurityConfig.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.control.practicas.controllers.AdminController.class)
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EmpresaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaRepository empresaRepository;

    @MockBean
    private EmpresaService empresaService;
    
    @MockBean
    private DataInitializer dataInitializer; // <-- añade esto


    @Test
    void testSimple() throws Exception {
        System.out.println("Test ejecutado!");
    }
}