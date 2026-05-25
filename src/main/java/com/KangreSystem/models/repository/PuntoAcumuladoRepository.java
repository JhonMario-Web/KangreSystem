package com.KangreSystem.models.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoAcumulado;

@Repository
public interface PuntoAcumuladoRepository extends CrudRepository<PuntoAcumulado, Long> {
	
	public List<PuntoAcumulado> findByCliente(Cliente cliente);
	public List<PuntoAcumulado> findByPedido(Pedido pedido);
}
