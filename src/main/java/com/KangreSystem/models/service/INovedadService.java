package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;

import com.KangreSystem.models.entity.Novedad;

public interface INovedadService {

	//METODOS CRUD
	public List<Novedad> listar();
	public void guardar(Novedad novedad);
	public void eliminar(Long idNovedad);
	
	//METODOS AUXILIARES
	public Novedad buscarPorId(Long idNovedad);
	public List<Novedad> buscarPorFecha(Date fecha);
	public boolean existePorId(Long idNovedad);
	
}
