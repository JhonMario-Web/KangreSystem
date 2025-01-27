package com.KangreSystem.models.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.repository.CategoriaInsumoRepository;
import com.KangreSystem.models.service.ICategoriaInsumoService;

@Service
public class CategoriaInsumoServImp implements ICategoriaInsumoService {
	
	@Autowired
	private CategoriaInsumoRepository categoriaRepository;

	@Override
	public List<CategoriaInsumo> listar() {
		return (List<CategoriaInsumo>) categoriaRepository.findAll();
	}

	@Override
	public void guardar(CategoriaInsumo categoria) {
		categoria.setCategoria(categoria.getCategoria().toUpperCase());
		categoriaRepository.save(categoria);
	}

	@Override
	public CategoriaInsumo buscarPorId(Long idCategoria) {
		return categoriaRepository.findById(idCategoria).orElse(null);
	}

	@Override
	public void eliminar(Long idCategoria) {
		categoriaRepository.deleteById(idCategoria);
	}

	@Override
	public CategoriaInsumo buscarPorCategoria(String categoria) {
		return categoriaRepository.findByCategoria(categoria);
	}

	@Override
	public boolean existePorId(Long idCategoria) {
		return categoriaRepository.existsById(idCategoria);
	}

	@Override
	public boolean existePorCategoria(String categoria) {
		return categoriaRepository.existsByCategoria(categoria);
	}

}
