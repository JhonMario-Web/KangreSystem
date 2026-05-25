package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Proveedor;

@Repository
public interface DevolucionRepository extends CrudRepository<Devolucion, Long> {
	
	public List<Devolucion> findByProveedor(Proveedor proveedor);
	public List<Devolucion> findByFecha(Date fecha);
	public List<Devolucion> findByFechaAndProveedor(Date fecha, Proveedor proveedor);
	

}
