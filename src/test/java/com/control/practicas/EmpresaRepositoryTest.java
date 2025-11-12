package com.control.practicas;

//package com.control.practicas.repositories;

import com.control.practicas.models.Empresa;
import com.control.practicas.repositories.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para EmpresaRepository
 * @DataJpaTest configura una BD H2 en memoria para los tests
 */
@DataJpaTest
class EmpresaRepositoryTest {

    private final TestEntityManager entityManager;
    private final EmpresaRepository empresaRepository;

    // ✅ Inyección de dependencias por constructor
    @Autowired
    public EmpresaRepositoryTest(TestEntityManager entityManager, EmpresaRepository empresaRepository) {
        this.entityManager = entityManager;
        this.empresaRepository = empresaRepository;
    }

    @Test
    void testGuardarYBuscarEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Test Corp");
        empresa.setCif("B11111111");
        empresa.setEmail("test@corp.com");
        empresa.setActiva(true);

        Empresa guardada = entityManager.persistAndFlush(empresa);
        Optional<Empresa> encontrada = empresaRepository.findById(guardada.getId());

        assertTrue(encontrada.isPresent());
        assertEquals("Test Corp", encontrada.get().getNombre());
        assertEquals("B11111111", encontrada.get().getCif());
    }

    @Test
    void testFindByCif() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Empresa CIF Test");
        empresa.setCif("B22222222");
        empresa.setEmail("cif@test.com");
        entityManager.persistAndFlush(empresa);

        Optional<Empresa> encontrada = empresaRepository.findByCif("B22222222");

        assertTrue(encontrada.isPresent());
        assertEquals("Empresa CIF Test", encontrada.get().getNombre());
    }

    @Test
    void testFindByCifNoExiste() {
        Optional<Empresa> encontrada = empresaRepository.findByCif("B99999999");
        assertFalse(encontrada.isPresent());
    }

    @Test
    void testFindByEmail() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Empresa Email Test");
        empresa.setCif("B33333333");
        empresa.setEmail("email@unique.com");
        entityManager.persistAndFlush(empresa);

        Optional<Empresa> encontrada = empresaRepository.findByEmail("email@unique.com");

        assertTrue(encontrada.isPresent());
        assertEquals("Empresa Email Test", encontrada.get().getNombre());
    }

    @Test
    void testFindByActivaTrue() {
        Empresa activa1 = new Empresa();
        activa1.setNombre("Activa 1");
        activa1.setCif("B44444444");
        activa1.setEmail("activa1@test.com");
        activa1.setActiva(true);

        Empresa activa2 = new Empresa();
        activa2.setNombre("Activa 2");
        activa2.setCif("B55555555");
        activa2.setEmail("activa2@test.com");
        activa2.setActiva(true);

        Empresa inactiva = new Empresa();
        inactiva.setNombre("Inactiva");
        inactiva.setCif("B66666666");
        inactiva.setEmail("inactiva@test.com");
        inactiva.setActiva(false);

        entityManager.persist(activa1);
        entityManager.persist(activa2);
        entityManager.persist(inactiva);
        entityManager.flush();

        List<Empresa> activas = empresaRepository.findByActivaTrue();

        assertEquals(2, activas.size());
        assertTrue(activas.stream().allMatch(Empresa::getActiva));
    }

    @Test
    void testFindByActivaFalse() {
        Empresa activa = new Empresa();
        activa.setNombre("Activa");
        activa.setCif("B77777777");
        activa.setEmail("activa@test.com");
        activa.setActiva(true);

        Empresa inactiva1 = new Empresa();
        inactiva1.setNombre("Inactiva 1");
        inactiva1.setCif("B88888888");
        inactiva1.setEmail("inactiva1@test.com");
        inactiva1.setActiva(false);

        Empresa inactiva2 = new Empresa();
        inactiva2.setNombre("Inactiva 2");
        inactiva2.setCif("B99999999");
        inactiva2.setEmail("inactiva2@test.com");
        inactiva2.setActiva(false);

        entityManager.persist(activa);
        entityManager.persist(inactiva1);
        entityManager.persist(inactiva2);
        entityManager.flush();

        List<Empresa> inactivas = empresaRepository.findByActivaFalse();

        assertEquals(2, inactivas.size());
        assertTrue(inactivas.stream().noneMatch(Empresa::getActiva));
    }

    @Test
    void testCountByActivaTrue() {
        Empresa activa1 = new Empresa();
        activa1.setNombre("Activa 1");
        activa1.setCif("A11111111");
        activa1.setEmail("act1@test.com");
        activa1.setActiva(true);

        Empresa activa2 = new Empresa();
        activa2.setNombre("Activa 2");
        activa2.setCif("A22222222");
        activa2.setEmail("act2@test.com");
        activa2.setActiva(true);

        Empresa inactiva = new Empresa();
        inactiva.setNombre("Inactiva");
        inactiva.setCif("A33333333");
        inactiva.setEmail("inact@test.com");
        inactiva.setActiva(false);

        entityManager.persist(activa1);
        entityManager.persist(activa2);
        entityManager.persist(inactiva);
        entityManager.flush();

        long count = empresaRepository.countByActivaTrue();

        assertEquals(2, count);
    }

    @Test
    void testFindBySector() {
        Empresa tech1 = new Empresa();
        tech1.setNombre("Tech Company 1");
        tech1.setCif("C11111111");
        tech1.setEmail("tech1@test.com");
        tech1.setSector("Tecnología");

        Empresa tech2 = new Empresa();
        tech2.setNombre("Tech Company 2");
        tech2.setCif("C22222222");
        tech2.setEmail("tech2@test.com");
        tech2.setSector("Tecnología");

        Empresa industria = new Empresa();
        industria.setNombre("Industrial SA");
        industria.setCif("C33333333");
        industria.setEmail("ind@test.com");
        industria.setSector("Industria");

        entityManager.persist(tech1);
        entityManager.persist(tech2);
        entityManager.persist(industria);
        entityManager.flush();

        List<Empresa> tecnologia = empresaRepository.findBySector("Tecnología");

        assertEquals(2, tecnologia.size());
        assertTrue(tecnologia.stream().allMatch(e -> "Tecnología".equals(e.getSector())));
    }

    @Test
    void testFindByNombreContainingIgnoreCase() {
        Empresa emp1 = new Empresa();
        emp1.setNombre("Acme Corporation");
        emp1.setCif("D11111111");
        emp1.setEmail("acme1@test.com");

        Empresa emp2 = new Empresa();
        emp2.setNombre("ACME Industries");
        emp2.setCif("D22222222");
        emp2.setEmail("acme2@test.com");

        Empresa emp3 = new Empresa();
        emp3.setNombre("Other Company");
        emp3.setCif("D33333333");
        emp3.setEmail("other@test.com");

        entityManager.persist(emp1);
        entityManager.persist(emp2);
        entityManager.persist(emp3);
        entityManager.flush();

        List<Empresa> resultados = empresaRepository.findByNombreContainingIgnoreCase("acme");

        assertEquals(2, resultados.size());
        assertTrue(resultados.stream()
                .allMatch(e -> e.getNombre().toLowerCase().contains("acme")));
    }

    @Test
    void testEliminarEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Para Eliminar");
        empresa.setCif("E11111111");
        empresa.setEmail("eliminar@test.com");
        Empresa guardada = entityManager.persistAndFlush(empresa);
        Long id = guardada.getId();

        empresaRepository.deleteById(id);
        Optional<Empresa> buscada = empresaRepository.findById(id);

        assertFalse(buscada.isPresent());
    }

    @Test
    void testActualizarEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Nombre Original");
        empresa.setCif("F11111111");
        empresa.setEmail("original@test.com");
        empresa.setActiva(true);
        Empresa guardada = entityManager.persistAndFlush(empresa);

        guardada.setNombre("Nombre Actualizado");
        guardada.setActiva(false);
        Empresa actualizada = empresaRepository.save(guardada);
        entityManager.flush();

        assertEquals("Nombre Actualizado", actualizada.getNombre());
        assertFalse(actualizada.getActiva());
    }
}

