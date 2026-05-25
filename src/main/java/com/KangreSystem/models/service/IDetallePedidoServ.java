package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Pedido;

public interface IDetallePedidoServ {
	
	//METODOS CRUD
	public List<DetallePedido> listar();
	public void guardar(DetallePedido detalle);
	public void guardarDetalles(List<DetallePedido> detalles);
	public void eliminar(Long idDetalle);
	
	//METODOS AUXILIARES
	public DetallePedido buscarPorId(Long idDetalle);
	public List<DetallePedido> buscarDetallesPorPedido(Pedido pedido);

}
