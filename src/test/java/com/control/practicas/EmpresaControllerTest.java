package com.control.practicas;

import com.control.practicas.controllers.EmpresaController;

//package com.control.practicas.controllers;

import com.control.practicas.models.Empresa;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.services.EmpresaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests de integración para EmpresaController
 * @WebMvcTest carga solo la capa web (controllers)
 */
@WebMvcTest(EmpresaController.class)
class EmpresaControllerTest {
    
	 private final MockMvc mockMvc;

	    @MockBean
	    private EmpresaRepository empresaRepository;

	    @MockBean
	    private EmpresaService empresaService;

	    private Empresa empresa;

	    // ✅ Inyección de dependencias por constructor
	    public EmpresaControllerTest(MockMvc mockMvc) {
	        this.mockMvc = mockMvc;
	    }

	    @BeforeEach
	    void setUp() {
	        empresa = new Empresa();
	        empresa.setId(1L);
	        empresa.setNombre("Test SA");
	        empresa.setCif("B12345678");
	        empresa.setEmail("test@empresa.com");
	        empresa.setDireccion("Calle Test 123");
	        empresa.setTelefono("955123456");
	        empresa.setSector("Tecnología");
	        empresa.setPersonaContacto("Juan Pérez");
	        empresa.setActiva(true);
	        empresa.setAlumnos(new ArrayList<>());
	        empresa.setTutoresPracticas(new ArrayList<>());
	    }

	    @Test
	    void testListar() throws Exception {
	        Empresa empresa2 = new Empresa();
	        empresa2.setId(2L);
	        empresa2.setNombre("Otra SA");
	        empresa2.setCif("B87654321");

	        List<Empresa> empresas = Arrays.asList(empresa, empresa2);
	        when(empresaService.listarTodas()).thenReturn(empresas);

	        mockMvc.perform(get("/empresa/listar"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("layout"))
	                .andExpect(model().attributeExists("empresas"))
	                .andExpect(model().attribute("empresas", hasSize(2)))
	                .andExpect(model().attribute("viewName", "empresa/listar"));

	        verify(empresaService, times(1)).listarTodas();
	    }

	    @Test
	    void testMostrarFormularioNuevo() throws Exception {
	        mockMvc.perform(get("/empresa/nuevo"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("layout"))
	                .andExpect(model().attributeExists("empresa"))
	                .andExpect(model().attribute("viewName", "empresa/form"));
	    }

	    @Test
	    void testMostrarFormularioEditar() throws Exception {
	        when(empresaService.buscarPorId(1L)).thenReturn(Optional.of(empresa));

	        mockMvc.perform(get("/empresa/editar/1"))
	                .andExpect(status().isOk())
	                .andExpect(view().name("layout"))
	                .andExpect(model().attributeExists("empresa"))
	                .andExpect(model().attribute("empresa", empresa))
	                .andExpect(model().attribute("viewName", "empresa/form"));

	        verify(empresaService, times(1)).buscarPorId(1L);
	    }

	    @Test
	    void testMostrarFormularioEditarEmpresaNoExiste() throws Exception {
	        when(empresaService.buscarPorId(999L)).thenReturn(Optional.empty());

	        mockMvc.perform(get("/empresa/editar/999"))
	                .andExpect(status().is3xxRedirection())
	                .andExpect(redirectedUrl("/empresa/listar"))
	                .andExpect(flash().attributeExists("error"))
	                .andExpect(flash().attribute("error", "Empresa no encontrada"));

	        verify(empresaService, times(1)).buscarPorId(999L);
	    }
    @Test
    void testGuardarNuevaEmpresa() throws Exception {
        // Arrange
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.empty());
        when(empresaRepository.findByEmail("test@empresa.com")).thenReturn(Optional.empty());
        when(empresaService.guardar(any(Empresa.class))).thenReturn(empresa);
        
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("nombre", "Test SA")
                .param("cif", "B12345678")
                .param("email", "test@empresa.com")
                .param("direccion", "Calle Test 123")
                .param("telefono", "955123456")
                .param("sector", "Tecnología")
                .param("personaContacto", "Juan Pérez"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Empresa creada exitosamente"));
        
        verify(empresaService, times(1)).guardar(any(Empresa.class));
    }
    
    @Test
    void testGuardarEmpresaSinNombre() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("nombre", "")
                .param("cif", "B12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/nuevo"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "El nombre de la empresa es obligatorio"));
        
        verify(empresaService, never()).guardar(any(Empresa.class));
    }
    
    @Test
    void testGuardarEmpresaSinCif() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("nombre", "Test SA")
                .param("cif", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/nuevo"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "El CIF es obligatorio"));
        
        verify(empresaService, never()).guardar(any(Empresa.class));
    }
    
    @Test
    void testGuardarEmpresaCifDuplicado() throws Exception {
        // Arrange
        Empresa existente = new Empresa();
        existente.setId(2L);
        existente.setCif("B12345678");
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.of(existente));
        
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("nombre", "Test SA")
                .param("cif", "B12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/nuevo"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Ya existe una empresa con ese CIF"));
        
        verify(empresaService, never()).guardar(any(Empresa.class));
    }
    
    @Test
    void testGuardarEmpresaEmailDuplicado() throws Exception {
        // Arrange
        Empresa existente = new Empresa();
        existente.setId(2L);
        existente.setEmail("test@empresa.com");
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.empty());
        when(empresaRepository.findByEmail("test@empresa.com")).thenReturn(Optional.of(existente));
        
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("nombre", "Test SA")
                .param("cif", "B12345678")
                .param("email", "test@empresa.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/nuevo"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Ya existe una empresa con ese email"));
        
        verify(empresaService, never()).guardar(any(Empresa.class));
    }
    
    @Test
    void testActualizarEmpresaExistente() throws Exception {
        // Arrange
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.of(empresa));
        when(empresaRepository.findByEmail("test@empresa.com")).thenReturn(Optional.of(empresa));
        when(empresaService.guardar(any(Empresa.class))).thenReturn(empresa);
        
        // Act & Assert
        mockMvc.perform(post("/empresa/guardar")
                .param("id", "1")
                .param("nombre", "Test SA Actualizada")
                .param("cif", "B12345678")
                .param("email", "test@empresa.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Empresa actualizada exitosamente"));
        
        verify(empresaService, times(1)).guardar(any(Empresa.class));
    }
    
    @Test
    void testEliminarEmpresaSinRelaciones() throws Exception {
        // Arrange
        when(empresaService.buscarPorId(1L)).thenReturn(Optional.of(empresa));
        doNothing().when(empresaService).eliminar(1L);
        
        // Act & Assert
        mockMvc.perform(get("/empresa/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Empresa eliminada exitosamente"));
        
        verify(empresaService, times(1)).buscarPorId(1L);
        verify(empresaService, times(1)).eliminar(1L);
    }
    
    @Test
    void testEliminarEmpresaNoExiste() throws Exception {
        // Arrange
        when(empresaService.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/empresa/eliminar/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Empresa no encontrada"));
        
        verify(empresaService, times(1)).buscarPorId(999L);
        verify(empresaService, never()).eliminar(anyLong());
    }
    
    @Test
    void testCambiarEstadoActivar() throws Exception {
        // Arrange
        empresa.setActiva(false);
        when(empresaService.buscarPorId(1L)).thenReturn(Optional.of(empresa));
        when(empresaService.guardar(any(Empresa.class))).thenReturn(empresa);
        
        // Act & Assert
        mockMvc.perform(post("/empresa/cambiar-estado/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Empresa activada exitosamente"));
        
        verify(empresaService, times(1)).buscarPorId(1L);
        verify(empresaService, times(1)).guardar(any(Empresa.class));
    }
    
    @Test
    void testCambiarEstadoDesactivar() throws Exception {
        // Arrange
        empresa.setActiva(true);
        when(empresaService.buscarPorId(1L)).thenReturn(Optional.of(empresa));
        when(empresaService.guardar(any(Empresa.class))).thenReturn(empresa);
        
        // Act & Assert
        mockMvc.perform(post("/empresa/cambiar-estado/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Empresa desactivada exitosamente"));
        
        verify(empresaService, times(1)).buscarPorId(1L);
        verify(empresaService, times(1)).guardar(any(Empresa.class));
    }
    
    @Test
    void testCambiarEstadoEmpresaNoExiste() throws Exception {
        // Arrange
        when(empresaService.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(post("/empresa/cambiar-estado/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empresa/listar"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Empresa no encontrada"));
        
        verify(empresaService, times(1)).buscarPorId(999L);
        verify(empresaService, never()).guardar(any(Empresa.class));
    }
}