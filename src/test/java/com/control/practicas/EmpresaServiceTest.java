package com.control.practicas;

//package com.control.practicas.services;

import com.control.practicas.models.Empresa;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.services.EmpresaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Tests unitarios para EmpresaService usando Mockito
 */
@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {
    
    @Mock
    private EmpresaRepository empresaRepository;
    
    @InjectMocks
    private EmpresaService empresaService;
    
    private Empresa empresa;
    
    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNombre("Test SA");
        empresa.setCif("B12345678");
        empresa.setEmail("test@empresa.com");
        empresa.setActiva(true);
    }
    
    @Test
    void testListarTodas() {
        // Arrange
        Empresa empresa2 = new Empresa();
        empresa2.setId(2L);
        empresa2.setNombre("Otra SA");
        empresa2.setCif("B87654321");
        
        List<Empresa> empresas = Arrays.asList(empresa, empresa2);
        when(empresaRepository.findAll()).thenReturn(empresas);
        
        // Act
        List<Empresa> resultado = empresaService.listarTodas();
        
        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Test SA", resultado.get(0).getNombre());
        assertEquals("Otra SA", resultado.get(1).getNombre());
        verify(empresaRepository, times(1)).findAll();
    }
    
    @Test
    void testListarActivas() {
        // Arrange
        List<Empresa> empresasActivas = Arrays.asList(empresa);
        when(empresaRepository.findByActivaTrue()).thenReturn(empresasActivas);
        
        // Act
        List<Empresa> resultado = empresaService.listarActivas();
        
        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActiva());
        verify(empresaRepository, times(1)).findByActivaTrue();
    }
    
    @Test
    void testBuscarPorId() {
        // Arrange
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        
        // Act
        Optional<Empresa> resultado = empresaService.buscarPorId(1L);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Test SA", resultado.get().getNombre());
        assertEquals(1L, resultado.get().getId());
        verify(empresaRepository, times(1)).findById(1L);
    }
    
    @Test
    void testBuscarPorIdNoExiste() {
        // Arrange
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Empresa> resultado = empresaService.buscarPorId(999L);
        
        // Assert
        assertFalse(resultado.isPresent());
        verify(empresaRepository, times(1)).findById(999L);
    }
    
    @Test
    void testBuscarPorCif() {
        // Arrange
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.of(empresa));
        
        // Act
        Optional<Empresa> resultado = empresaService.buscarPorCif("B12345678");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("B12345678", resultado.get().getCif());
        verify(empresaRepository, times(1)).findByCif("B12345678");
    }
    
    @Test
    void testBuscarPorEmail() {
        // Arrange
        when(empresaRepository.findByEmail("test@empresa.com")).thenReturn(Optional.of(empresa));
        
        // Act
        Optional<Empresa> resultado = empresaService.buscarPorEmail("test@empresa.com");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("test@empresa.com", resultado.get().getEmail());
        verify(empresaRepository, times(1)).findByEmail("test@empresa.com");
    }
    
    @Test
    void testGuardar() {
        // Arrange
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);
        
        // Act
        Empresa guardada = empresaService.guardar(empresa);
        
        // Assert
        assertNotNull(guardada);
        assertEquals("Test SA", guardada.getNombre());
        assertEquals("B12345678", guardada.getCif());
        verify(empresaRepository, times(1)).save(empresa);
    }
    
    @Test
    void testEliminar() {
        // Arrange
        doNothing().when(empresaRepository).deleteById(1L);
        
        // Act
        empresaService.eliminar(1L);
        
        // Assert
        verify(empresaRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testActivar() {
        // Arrange
        empresa.setActiva(false);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);
        
        // Act
        empresaService.activar(1L);
        
        // Assert
        assertTrue(empresa.getActiva());
        verify(empresaRepository, times(1)).findById(1L);
        verify(empresaRepository, times(1)).save(empresa);
    }
    
    @Test
    void testActivarEmpresaNoExiste() {
        // Arrange
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        empresaService.activar(999L);
        
        // Assert
        verify(empresaRepository, times(1)).findById(999L);
        verify(empresaRepository, never()).save(any(Empresa.class));
    }
    
    @Test
    void testDesactivar() {
        // Arrange
        empresa.setActiva(true);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);
        
        // Act
        empresaService.desactivar(1L);
        
        // Assert
        assertFalse(empresa.getActiva());
        verify(empresaRepository, times(1)).findById(1L);
        verify(empresaRepository, times(1)).save(empresa);
    }
    
    @Test
    void testDesactivarEmpresaNoExiste() {
        // Arrange
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        empresaService.desactivar(999L);
        
        // Assert
        verify(empresaRepository, times(1)).findById(999L);
        verify(empresaRepository, never()).save(any(Empresa.class));
    }
    
    @Test
    void testExistePorCif() {
        // Arrange
        when(empresaRepository.findByCif("B12345678")).thenReturn(Optional.of(empresa));
        when(empresaRepository.findByCif("B99999999")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertTrue(empresaService.existePorCif("B12345678"));
        assertFalse(empresaService.existePorCif("B99999999"));
        
        verify(empresaRepository, times(1)).findByCif("B12345678");
        verify(empresaRepository, times(1)).findByCif("B99999999");
    }
    
    @Test
    void testExistePorEmail() {
        // Arrange
        when(empresaRepository.findByEmail("test@empresa.com")).thenReturn(Optional.of(empresa));
        when(empresaRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertTrue(empresaService.existePorEmail("test@empresa.com"));
        assertFalse(empresaService.existePorEmail("noexiste@test.com"));
        
        verify(empresaRepository, times(1)).findByEmail("test@empresa.com");
        verify(empresaRepository, times(1)).findByEmail("noexiste@test.com");
    }
    
    @Test
    void testContarTodas() {
        // Arrange
        when(empresaRepository.count()).thenReturn(5L);
        
        // Act
        long count = empresaService.contarTodas();
        
        // Assert
        assertEquals(5L, count);
        verify(empresaRepository, times(1)).count();
    }
    
    @Test
    void testContarActivas() {
        // Arrange
        when(empresaRepository.countByActivaTrue()).thenReturn(3L);
        
        // Act
        long count = empresaService.contarActivas();
        
        // Assert
        assertEquals(3L, count);
        verify(empresaRepository, times(1)).countByActivaTrue();
    }
    
    @Test
    void testBuscarPorSector() {
        // Arrange
        Empresa empresa2 = new Empresa();
        empresa2.setNombre("Otra Tech");
        empresa2.setSector("Tecnología");
        
        empresa.setSector("Tecnología");
        List<Empresa> empresasTech = Arrays.asList(empresa, empresa2);
        when(empresaRepository.findBySector("Tecnología")).thenReturn(empresasTech);
        
        // Act
        List<Empresa> resultado = empresaService.buscarPorSector("Tecnología");
        
        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(e -> "Tecnología".equals(e.getSector())));
        verify(empresaRepository, times(1)).findBySector("Tecnología");
    }
    
    @Test
    void testBuscarPorNombre() {
        // Arrange
        List<Empresa> empresas = Arrays.asList(empresa);
        when(empresaRepository.findByNombreContainingIgnoreCase("test")).thenReturn(empresas);
        
        // Act
        List<Empresa> resultado = empresaService.buscarPorNombre("test");
        
        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getNombre().toLowerCase().contains("test"));
        verify(empresaRepository, times(1)).findByNombreContainingIgnoreCase("test");
    }
}
