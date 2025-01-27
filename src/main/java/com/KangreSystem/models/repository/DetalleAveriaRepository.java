package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.DetalleAveria;

@Repository
public interface DetalleAveriaRepository extends CrudRepository<DetalleAveria, Long> {

	public List<DetalleAveria> findByAveria(Averia averia);

}
