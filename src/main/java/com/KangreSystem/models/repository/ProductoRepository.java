package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.Producto;

@Repository
public interface ProductoRepository extends CrudRepository<Producto, Long> {
	
	//METODOS AUXILIARES
	public boolean existsByNombre(String nombre);
	public Producto findByNombre(String nombre);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Producto> findByNombreContaining(String nombre);
	public List<Producto> findByCategoria(CategoriaProducto categoria);
	public List<Producto> findByEstado(String estado);
	public List<Producto> findByCategoriaAndEstado(CategoriaProducto categoria, String estado);
	
	//METODOS LOGICOS DEL SISTEMA
	@Query("SELECT p FROM Producto p ORDER BY p.cantVendida ASC")
	public List<Producto> orderByCantVendidaAsc();
	
	@Query("SELECT p FROM Producto p ORDER BY p.cantVendida DESC")
	public List<Producto> orderByCantVendidaDesc();
	
}
