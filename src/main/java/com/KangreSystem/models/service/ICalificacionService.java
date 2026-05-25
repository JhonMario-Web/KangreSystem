package com.KangreSystem.models.service;

import java.security.Principal;
import java.util.List;
import com.KangreSystem.models.entity.Calificacion;

public interface ICalificacionService {
	
	//METODOS CRUD
	public List<Calificacion> listar();
	public void guardar(Calificacion calificacion);
	public void eliminar(Long idCalificacion);
	
	//METODOS AUXILIARES
	public Calificacion buscarPorId(Long idCalificacion);
	public List<Calificacion> filtrar(Calificacion calificacion);
	
	//METODOS LOGICOS DEL SISTEMA
	public boolean validPermitida(Calificacion calificacion, Principal principal);
	public boolean validIsCalificada(Calificacion calificacion);
}
