package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Calificacion;

@Repository
public interface CalificacionRepository extends CrudRepository<Calificacion, Long> {

	public List<Calificacion> findByFecha(Date fecha);
	public List<Calificacion> findByCalificacion(String calificacion);
	public List<Calificacion> findByFechaAndCalificacion(Date fecha, String calificacion);
	
}
