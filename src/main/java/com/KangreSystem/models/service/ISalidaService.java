package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Salida;

public interface ISalidaService {
	
	//METODOS CRUD
	public List<Salida> listar();
	public void guardar(Salida salida);
	public void guardarLista(List<Salida> salidas);
	public void eliminar(Long idSalida);
	public void eliminarLista(List<Salida> salidas);
	
	//METODOS AUXILIARES
	public Salida buscarPorId(Long idSalida);
	public List<Salida> buscarPorTipoFecha(String tipo, Date fecha);
	public List<Salida> buscarPorTipo(String tipo);
	public List<Salida> buscarPorFecha(Date fecha);
	public List<Salida> buscarPorInsumo(Insumo insumo);
	public List<Salida> buscarPorPedido(Pedido pedido);
	public List<Salida> buscarPorAveria(Averia averia);
	public List<Salida> buscarPorDevolucion(Devolucion devolucion);
	
	//METODOS LOGICOS DEL SISTEMA
	public List<Salida> salidasMesActual();

}
