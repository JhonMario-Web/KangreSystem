package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Venta;

public interface IVentaService {
	
	//METODOS CRUD
	public List<Venta> listar();
	public void guardar(Venta venta);
	public void guardarLista(List<Venta> ventas);
	public void eliminar(Long idVenta);
	public void eliminarLista(List<Venta> ventas);
	
	//METODOS AUXILIARES
	public Venta buscarPorId(Long idVenta);
	public Venta buscarPorPedido(Pedido pedido);

}
