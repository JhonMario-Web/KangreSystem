package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.CategoriaProveedor;

@Repository
public interface CategoriaProveedorRepository extends CrudRepository<CategoriaProveedor, Long> {
	
	public boolean existsByCategoria(String categoria);
	public CategoriaProveedor findByCategoria(String categoria);

}
