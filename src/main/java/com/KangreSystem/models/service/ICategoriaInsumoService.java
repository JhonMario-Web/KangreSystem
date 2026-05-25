package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.CategoriaInsumo;

public interface ICategoriaInsumoService {
	
	public List<CategoriaInsumo> listar();
	public void guardar(CategoriaInsumo categoria);
	public CategoriaInsumo buscarPorId(Long idCategoria);
	public CategoriaInsumo buscarPorCategoria(String categoria);
	public void eliminar(Long idCategoria);
	public boolean existePorId(Long idCategoria);
	public boolean existePorCategoria(String categoria);
}
