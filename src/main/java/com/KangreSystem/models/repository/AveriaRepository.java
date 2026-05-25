package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Averia;

@Repository
public interface AveriaRepository extends CrudRepository<Averia, Long> {
	
	public Averia findByNumeroAveria(String numeroAveria);
	public List<Averia> findByFecha(Date fecha);
	public boolean existsByFecha(Date fecha);
	
}
