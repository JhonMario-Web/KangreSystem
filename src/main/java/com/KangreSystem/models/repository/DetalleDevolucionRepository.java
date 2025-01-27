package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.Devolucion;

@Repository
public interface DetalleDevolucionRepository extends CrudRepository<DetalleDevolucion,Long> {
	
	public List<DetalleDevolucion> findByDevolucion(Devolucion devolucion);

}
