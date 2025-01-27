package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Restaurante;

@Repository
public interface RestauranteRepository extends CrudRepository<Restaurante, Long> {
	public Restaurante findByNombre(String nombre);
	public boolean existsByNombre(String nombre);
	public List<Restaurante> findByNombreContaining(String nombre);
	public List<Restaurante> findByCiudad(String ciudad);
	
}
