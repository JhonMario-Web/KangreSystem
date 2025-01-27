package com.KangreSystem.models.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.entity.Proveedor;

@Repository
public interface ProveedorRepository extends CrudRepository<Proveedor, Long> {
	
	public boolean existsByNitProveedor(String nitProveedor);
	public boolean existsByNombre(String nombre);
	public Proveedor findByNitProveedorAndNombre(String nitProveedor, String nombre);
	public Proveedor findByNitProveedor(String nitProveedor);
	
	//Metodos para la busqueda dinamica
	public List<Proveedor> findByNitProveedorContaining(String nitProveedor);
	public List<Proveedor> findByCategoria(CategoriaProveedor categoria);
	public List<Proveedor> findByCiudad(String ciudad);
	public List<Proveedor> findByEnabled(boolean enabled);
	public List<Proveedor> findByCategoriaAndCiudadAndEnabled(CategoriaProveedor categoria, String ciudad, boolean enabled);
	public List<Proveedor> findByCategoriaAndCiudad(CategoriaProveedor categoria, String ciudad);
	public List<Proveedor> findByCategoriaAndEnabled(CategoriaProveedor categoria, boolean enabled);
	public List<Proveedor> findByCiudadAndEnabled(String ciudad, boolean enabled);
	
	
	
}
