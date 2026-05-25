package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoRedimido;

public interface IPuntoRedimidoService {
	
	//METODOS CRUD
	public List<PuntoRedimido> listar();
	public void guardarLista(List<PuntoRedimido> redimidos);
	public void eliminar(Long idTrxn);
	
	//METODOS AUXILIARES
	public PuntoRedimido buscarPorId(Long idTrxn);
	public List<PuntoRedimido> buscarPorCliente(Cliente cliente);
	
	//METODOS LOGICOS DEL SISTEMA
	public void redimirPedido(List<PuntoRedimido> redimidos);
	public List<PuntoRedimido> redimirTodo(Pedido pedido);
	public List<PuntoRedimido> redimirParte(Pedido pedido, int puntos);

}
