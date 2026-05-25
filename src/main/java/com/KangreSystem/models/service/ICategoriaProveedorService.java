package com.KangreSystem.models.service;

import java.util.List;

import com.KangreSystem.models.entity.CategoriaProveedor;

public interface ICategoriaProveedorService {
	public List<CategoriaProveedor> listar();
	public CategoriaProveedor buscarPorId(Long idCategoria);
	public CategoriaProveedor buscarPorCategoria(String categoria);
	public void guardar(CategoriaProveedor categoriaProveedor);
	public void eliminar(Long idCategoria);
	public boolean existePorId(Long idCategoria);
	public boolean existePorCategoria(String categoria);
}
