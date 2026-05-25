package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Pedido;

@Repository
public interface DetallePedidoRepo extends CrudRepository<DetallePedido, Long> {
	
	public List<DetallePedido> findByPedido(Pedido pedido);

}
