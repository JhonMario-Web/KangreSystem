package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;

public interface IEntradaService {
	
	//METODOS CRUD
	public List<Entrada> listar();
	public void guardar(Entrada entrada);
	public void guardarLista(List<Entrada> entradas);
	public void eliminar(Long idEntrada);
	public void eliminarLista(List<Entrada> entradas);
	
	//METODOS AUXILIARES
	public Entrada buscarPorId(Long idEntrada);
	public List<Entrada> buscarPorLlegada(LlegadaCompra llegada);
	public List<Entrada> buscarPorFecha(Date fecha);
	public List<Entrada> buscarPorInsumo(Insumo insumo);
	
	//METODOS LOGICOS DEL SISTEMA
	public List<Entrada> entradasMesActual();

}
