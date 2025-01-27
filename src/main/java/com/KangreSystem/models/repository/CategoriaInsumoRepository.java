package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.CategoriaInsumo;

@Repository
public interface CategoriaInsumoRepository extends CrudRepository<CategoriaInsumo, Long> {

	public boolean existsByCategoria(String categoria);
	public CategoriaInsumo findByCategoria(String categoria);
}
