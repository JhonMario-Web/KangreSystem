package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;

import com.KangreSystem.models.entity.Averia;

public interface IAveriaService {
	
	//METODOS CRUD
	public List<Averia> listar();
	public void guardar(Averia averia);
	public void eliminar(Long idAveria);
	
	//METODOS AUXILIARES
	public List<Averia> buscarPorFecha(Date fecha);
	public Averia buscarPorNumeroAveria(String numeroAveria);
	public Averia buscarPorId(Long idAveria);
	
	//METODOS LOGICOS DEL SISTEMA
	public boolean unicaPorDia();
	public List<Averia> averiasMesActual();
	public Long precioAveriasMesActual();

}
