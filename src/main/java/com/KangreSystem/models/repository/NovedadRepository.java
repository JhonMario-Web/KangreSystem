package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Novedad;

@Repository
public interface NovedadRepository extends CrudRepository<Novedad, Long> {
	
	public List<Novedad> findAllByFecha(Date fecha);
}
