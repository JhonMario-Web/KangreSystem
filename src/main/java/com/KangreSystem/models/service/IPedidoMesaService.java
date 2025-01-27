package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;

public interface IPedidoMesaService {
	
	//METODOS CRUD
	public List<PedidoMesa> listar();
	public void guardar(PedidoMesa pedidoMesa);
	public void eliminar(Long idPedidoMesa);
	public void guardarLista(List<PedidoMesa> lista);
	
	//METODOS AUXILIARES
	public PedidoMesa buscarPorId(Long idPedidoMesa);
	public List<PedidoMesa> buscarPorPedido(Pedido pedido);

}
