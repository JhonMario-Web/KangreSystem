package com.KangreSystem.models.service;

import java.util.List;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Favorito;

public interface IFavoritoService {
	
	//METODOS CRUD
	public List<Favorito> listar();
	public void guardar(Favorito favorito);
	public void eliminar(Long idFavorito);
	public void guardarTodo(List<Favorito> favoritos);
	public void eliminarTodo(List<Favorito> favoritos);
	
	//METODOS AUXILIARES
	public Favorito buscarPorId(Long idFavorito);
	public Favorito buscarPorFavorito(Cliente favorito);
	public boolean existePorId(Long idFavorito);
	public List<Favorito> buscarPorCliente(Cliente cliente);
	public long contarPorCliente(Cliente cliente);

}
