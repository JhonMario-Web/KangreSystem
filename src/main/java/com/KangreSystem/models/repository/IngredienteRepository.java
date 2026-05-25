package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Ingrediente;
import com.KangreSystem.models.entity.Producto;

@Repository
public interface IngredienteRepository extends CrudRepository<Ingrediente, Long> {
	public List<Ingrediente> findByProducto(Producto producto);
}
