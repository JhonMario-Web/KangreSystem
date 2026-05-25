package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Favorito;
import com.KangreSystem.models.repository.FavoritoRepository;
import com.KangreSystem.models.service.IFavoritoService;

@Service
public class FavoritoServImp implements IFavoritoService {
	
	@Autowired
	private FavoritoRepository favoritoRepository;

	@Override
	public List<Favorito> listar() {
		return (List<Favorito>) favoritoRepository.findAll();
	}

	@Override
	public void guardar(Favorito favorito) {
		favoritoRepository.save(favorito);
	}

	@Override
	public void eliminar(Long idFavorito) {
		favoritoRepository.deleteById(idFavorito);
	}

	@Override
	public Favorito buscarPorId(Long idFavorito) {
		return favoritoRepository.findById(idFavorito).orElse(null);
	}

	@Override
	public boolean existePorId(Long idFavorito) {
		return favoritoRepository.existsById(idFavorito);
	}

	@Override
	public List<Favorito> buscarPorCliente(Cliente cliente) {
		return favoritoRepository.findByCliente(cliente);
	}

	@Override
	public void guardarTodo(List<Favorito> favoritos) {
		favoritoRepository.saveAll(favoritos);
	}

	@Override
	public void eliminarTodo(List<Favorito> favoritos) {
		favoritoRepository.deleteAll(favoritos);
	}

	@Override
	public Favorito buscarPorFavorito(Cliente favorito) {
		return favoritoRepository.findByFavorito(favorito);
	}

	@Override
	public long contarPorCliente(Cliente cliente) {
		return favoritoRepository.countByCliente(cliente);
	}

}
