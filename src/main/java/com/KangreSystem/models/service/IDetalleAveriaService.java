package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.DetalleAveria;

public interface IDetalleAveriaService {
	
	//METODOS CRUD
	public List<DetalleAveria> listar();
	public void guardar(DetalleAveria detalle);
	public DetalleAveria buscarPorId(Long idDetalle);
	public void eliminar(Long idDetalle);
	public void eliminarLista(List<DetalleAveria> detalles);
	public void guardarLista(List<DetalleAveria> detalles);
	
	//METODOS AUXILIARES
	public List<DetalleAveria> buscarDetallesPorAveria(Averia averia);

}
