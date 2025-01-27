package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Mesa;

public interface IMesaService {
	
	//METODOS CRUD
	public List<Mesa> listar();
	public void guardar(Mesa mesa);
	public void eliminar(Long idMesa);
	
	//METODOS AUXILIARES
	public Mesa buscarPorId(Long idMesa);
	public Mesa buscarPorNumeroMesa(String numeroMesa);
	
	//METODOS LOGICOS DEL SISTEMA
	public List<Mesa> mesasDisponibles();

}
