package com.KangreSystem.models.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Pedido;

@Repository
public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	
	public Pedido findByNumeroOrden(String numeroPedido);
	public List<Pedido> findByEmpleado(Empleado empleado);
	public List<Pedido> findByEstado(String estado);
	public List<Pedido> findByEstadoAndFecha(String estado, Date fecha);
	public List<Pedido> findByFecha(Date fecha);
}
