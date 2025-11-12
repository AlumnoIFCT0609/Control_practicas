package com.control.practicas;
//package com.control.practicas.models;

import org.junit.jupiter.api.Test;

import com.control.practicas.models.Empresa;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Tests unitarios para la entidad Empresa
 */
class EmpresaTest {
    
    private Empresa empresa;
    
    @BeforeEach
    void setUp() {
        empresa = new Empresa();
    }
    
    @Test
    void testSettersAndGetters() {
        // Arrange & Act
        empresa.setId(1L);
        empresa.setNombre("Acme Corporation");
        empresa.setCif("B12345678");
        empresa.setDireccion("Calle Falsa 123");
        empresa.setTelefono("955123456");
        empresa.setEmail("contacto@acme.com");
        empresa.setSector("Tecnología");
        empresa.setPersonaContacto("Juan Pérez");
        empresa.setActiva(true);
        
        // Assert
        assertEquals(1L, empresa.getId());
        assertEquals("Acme Corporation", empresa.getNombre());
        assertEquals("B12345678", empresa.getCif());
        assertEquals("Calle Falsa 123", empresa.getDireccion());
        assertEquals("955123456", empresa.getTelefono());
        assertEquals("contacto@acme.com", empresa.getEmail());
        assertEquals("Tecnología", empresa.getSector());
        assertEquals("Juan Pérez", empresa.getPersonaContacto());
        assertTrue(empresa.getActiva());
    }
    
    @Test
    void testActivaPorDefecto() {
        // Assert - El campo activa debe ser true por defecto
        assertTrue(empresa.getActiva());
    }
    
    @Test
    void testListasInicializadas() {
        // Assert - Las listas deben estar inicializadas vacías
        assertNotNull(empresa.getTutoresPracticas());
        assertNotNull(empresa.getAlumnos());
        assertTrue(empresa.getTutoresPracticas().isEmpty());
        assertTrue(empresa.getAlumnos().isEmpty());
    }
    
    @Test
    void testFechaCreacionNull() {
        // Assert - La fecha de creación es null hasta que se persiste
        assertNull(empresa.getFechaCreacion());
    }
    
    @Test
    void testOnCreate() {
        // Act - Simular el @PrePersist llamando al método directamente
        empresa.onCreate();
        
        // Assert
        assertNotNull(empresa.getFechaCreacion());
        assertEquals(LocalDate.now(), empresa.getFechaCreacion());
    }
    
    @Test
    void testCambiarEstadoActiva() {
        // Arrange
        empresa.setActiva(true);
        
        // Act
        empresa.setActiva(false);
        
        // Assert
        assertFalse(empresa.getActiva());
        
        // Act
        empresa.setActiva(true);
        
        // Assert
        assertTrue(empresa.getActiva());
    }
    
    @Test
    void testEmpresaCompleta() {
        // Arrange & Act
        empresa.setNombre("Test SA");
        empresa.setCif("A87654321");
        empresa.setDireccion("Av. Ejemplo 456");
        empresa.setTelefono("600111222");
        empresa.setEmail("info@test.es");
        empresa.setSector("Industria");
        empresa.setPersonaContacto("María López");
        empresa.setActiva(true);
        empresa.setTutoresPracticas(new ArrayList<>());
        empresa.setAlumnos(new ArrayList<>());
        
        // Assert
        assertNotNull(empresa.getNombre());
        assertNotNull(empresa.getCif());
        assertNotNull(empresa.getTutoresPracticas());
        assertNotNull(empresa.getAlumnos());
    }
    
    @Test
    void testModificarFechaCreacion() {
        // Arrange
        LocalDate fecha = LocalDate.of(2023, 1, 15);
        
        // Act
        empresa.setFechaCreacion(fecha);
        
        // Assert
        assertEquals(fecha, empresa.getFechaCreacion());
    }
}
