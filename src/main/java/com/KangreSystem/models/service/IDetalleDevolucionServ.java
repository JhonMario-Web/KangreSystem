package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.Devolucion;

public interface IDetalleDevolucionServ {
	
	//METODOS CRUD
	public List<DetalleDevolucion> listar();
	public void guardar(DetalleDevolucion detalle);
	public void guardarLista(List<DetalleDevolucion> detalles);
	public void eliminar(Long idDetalle);
	
	//METODOS AUXILIARES
	public DetalleDevolucion buscarPorId(Long idDetalle);
	public List<DetalleDevolucion> buscarPorDevolucion(Devolucion devolucion);

}
