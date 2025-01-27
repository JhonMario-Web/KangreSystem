package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;

@Repository
public interface PedidoMesaRepository extends CrudRepository<PedidoMesa, Long> {
	
	public List<PedidoMesa> findByPedido(Pedido pedido);

}
