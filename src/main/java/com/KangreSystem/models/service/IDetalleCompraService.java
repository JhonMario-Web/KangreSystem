package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;

public interface IDetalleCompraService {
	
	//METODOS CRUD
	public List<DetalleCompra> listar();
	public void guardar(DetalleCompra detalle);
	public void eliminar(Long idDetalle);
	public void guardarLista(List<DetalleCompra> detalles);
	
	//METODOS AUXILIARES
	public DetalleCompra buscarPorId(Long idDetalle);
	public List<DetalleCompra> buscarDetallesPorCompra(Compra compraAUX);

}
