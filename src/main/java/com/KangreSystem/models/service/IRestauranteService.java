package com.KangreSystem.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.Restaurante;

public interface IRestauranteService {
	
	//METODOS CRUD
	public List<Restaurante> listar();
	public void guardar(Restaurante restaurante);
	public void eliminar(Long idRestaurante);
	
	//METODOS AUXILIARES
	public Restaurante buscarPorId(Long idRestaurante);
	public Restaurante buscarPorNombre(String nombre);
	public boolean checkImgIsValid(MultipartFile file);
	public boolean existePorNombre(String nombre);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Restaurante> buscarRestaurantesPorNombre(String nombre);
	public List<Restaurante> buscarPorCiudad(String ciudad);
	
}
