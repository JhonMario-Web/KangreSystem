package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.CategoriaProducto;

@Repository
public interface CategoriaProductoRepository extends CrudRepository<CategoriaProducto, Long> {

	public boolean existsByCategoria(String categoria);
	public CategoriaProducto findByCategoria(String categoria);
}
