package com.KangreSystem.models.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;

@Repository
public interface InsumoRepository extends CrudRepository<Insumo, Long> {
	
	public  boolean existsByNombre(String nombre);
	public Insumo findByNombre(String nombre);
	
	public List<Insumo> findByNombreContaining(String nombre);
	public List<Insumo> findByCategoria(CategoriaInsumo categoria);
	public List<Insumo> findByProveedor(Proveedor proveedor);
	public List<Insumo> findByEstado(String estado);
	public List<Insumo> findByCategoriaAndProveedorAndEstado(CategoriaInsumo categoria, Proveedor proveedor, String estado);
	public List<Insumo> findByCategoriaAndProveedor(CategoriaInsumo categoria, Proveedor proveedor);
	public List<Insumo> findByCategoriaAndEstado(CategoriaInsumo categoria, String estado);
	public List<Insumo> findByProveedorAndEstado(Proveedor proveedor, String estado);
	public List<Insumo> findByProveedorAndDevolucion(Proveedor proveedor, boolean devolucion);

}
