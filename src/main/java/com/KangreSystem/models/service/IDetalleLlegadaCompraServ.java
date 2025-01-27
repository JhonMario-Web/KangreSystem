package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;

public interface IDetalleLlegadaCompraServ {
	
	//METODOS CRUD
	public List<DetalleLlegadaCompra> listar();
	public void guardar(DetalleLlegadaCompra llegada);
	public void guardarLista(List<DetalleLlegadaCompra> detalles);
	public void eliminar(Long idDetalle);
	
	//METODOS AUXILIARES
	public void buscarPorId(Long idDetalle);

}
