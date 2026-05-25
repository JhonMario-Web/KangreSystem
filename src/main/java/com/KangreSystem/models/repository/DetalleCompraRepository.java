package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;

@Repository
public interface DetalleCompraRepository extends CrudRepository<DetalleCompra, Long> {

	public List<DetalleCompra> findByCompra(Compra compra);
	
}
