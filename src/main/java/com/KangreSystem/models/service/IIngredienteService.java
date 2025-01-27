package com.KangreSystem.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.Ingrediente;
import com.KangreSystem.models.entity.Producto;

public interface IIngredienteService {
	
	//METODOS CRUD
	public List<Ingrediente> listar();
	public void guardar(Ingrediente ingrediente);
	public void guardarTodo(List<Ingrediente> ingredientes);
	public void eliminar(Long idIngrediente);
	public boolean saveDataFromUploadFile(MultipartFile file);
	
	//METODOS AUXILIARES
	public Ingrediente buscarPorId(Long idIngrediente);
	public List<Ingrediente> buscarPorProducto(Producto producto);
}
