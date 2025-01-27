package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.Proveedor;

@Repository
public interface CompraRepository extends CrudRepository<Compra, Long> {
	
	public Compra findByNumeroCompra(String numeroCompra);
	public List<Compra> findByFecha(Date fecha);
	public List<Compra> findByProveedor(Proveedor proveedor);
	public List<Compra> findByEstado(String estado);
	public List<Compra> findByFechaAndProveedor(Date fecha, Proveedor proveedor);
	public List<Compra> findByFechaAndEstado(Date fecha, String estado);
	public List<Compra> findByProveedorAndEstado(Proveedor proveedor, String estado);
	public List<Compra> findByFechaAndProveedorAndEstado(Date fecha, Proveedor proveedor, String estado);	
}
