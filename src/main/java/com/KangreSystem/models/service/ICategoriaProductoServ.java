package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.CategoriaProducto;

public interface ICategoriaProductoServ {
	
	public List<CategoriaProducto> listar();
	public void guardar(CategoriaProducto categoria);
	public CategoriaProducto buscarPorId(Long idCategoria);
	public CategoriaProducto buscarPorCategoria(String categoria);
	public void eliminar(Long idCategoria);
	public boolean existePorId(Long idCategoria);
	public boolean existePorCategoria(String categoria);
}
