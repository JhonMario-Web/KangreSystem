package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Oferta;

public interface IOfertaService {
	
	//METODOS DEL C R U D
	public List<Oferta> listar();
	public void guardar(Oferta oferta);
	public void eliminar(Long idOferta);
	public Oferta buscarPorId(Long idOferta);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Oferta> buscarOfertasPorNombre(String nombre);
	public List<Oferta> buscarPorEnabled(String enabled);
	
	//METODOS LOGICOS DE SISTEMA
	public void actualizarOfertas();
	
}
