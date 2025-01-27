package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Proveedor;

@Repository
public interface LlegadaCompraRepository extends CrudRepository<LlegadaCompra, Long> {

	public boolean existsByNumeroFactura(String numeroFactura);
	public List<LlegadaCompra> findByProveedor(Proveedor proveedor);
	public LlegadaCompra findByNumeroFactura(String numeroFactura);
}
