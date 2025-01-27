package com.KangreSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.ProveedorRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
//@TestMethodOrder(OrderAnnotation.class)
public class ProveedorTest {
	//@Rollback(false)
	//@Order(1), @Order(2), etc
	
	@Autowired
	private ProveedorRepository proveedorRepo;
	
	@Test
	public void testCreateProveedor() {
		CategoriaProveedor categoria = new CategoriaProveedor((long) 1, "BEBIDAS");
		Proveedor proveedor = new Proveedor((long) 1, "8301234567", "Pablo Escobar", "3143932500", "pablito@gmail.com", "Calle de las mas falsas que hay", categoria, true);
		Proveedor proveedorSaved = proveedorRepo.save(proveedor);
		
		assertNotNull(proveedorSaved);
	}
	
	@Test
	public void testFindByNitExists() {
		String nit = "8600016978";
		Proveedor proveedor = proveedorRepo.findByNitProveedor(nit);
		
		assertThat(proveedor.getNitProveedor()).isEqualTo(nit);
	}
	
	@Test
	public void testFindByNitNotExists() {
		String nit = "86000169788";
		Proveedor proveedor = proveedorRepo.findByNitProveedor(nit);
		
		assertNull(proveedor);
	}
	
	@Test
	public void testUpdateProveedor() {
		String nit = "8600016978";
		String nombre = "Apple";
		Proveedor proveedor = proveedorRepo.findByNitProveedor(nit);
		
		proveedor.setNombre(nombre);
		Proveedor proveedorUpdated = proveedorRepo.save(proveedor);
		
		assertThat(proveedorUpdated.getNombre()).isEqualTo(nombre);
	}
	
	@Test
	public void testListProveedores() {
		List<Proveedor> proveedores = (List<Proveedor>) proveedorRepo.findAll();
		
		assertThat(proveedores).size().isGreaterThan(0);
	}
	
	@Test
	public void testDeleteProveedor() {
		long idProveedor = 1;
		boolean existsBefore = proveedorRepo.findById(idProveedor).isPresent();
		
		proveedorRepo.deleteById(idProveedor);
		
		boolean notExistsAfter = proveedorRepo.findById(idProveedor).isPresent();
		
		assertTrue(existsBefore);
		assertFalse(notExistsAfter);
	}
	
}
