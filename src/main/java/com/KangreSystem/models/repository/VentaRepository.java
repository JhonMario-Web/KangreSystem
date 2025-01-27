package com.KangreSystem.models.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Venta;

@Repository
public interface VentaRepository extends CrudRepository<Venta, Long> {
	
	public Venta findByPedido(Pedido pedido);

}
