package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.repository.CategoriaProductoRepository;
import com.KangreSystem.models.service.ICategoriaProductoServ;

@Service
public class CategoriaProductoServImp implements ICategoriaProductoServ {

	@Autowired
	private CategoriaProductoRepository categoriaRepository;
	
	@Override
	public List<CategoriaProducto> listar() {
		return (List<CategoriaProducto>) categoriaRepository.findAll();
	}

	@Override
	public void guardar(CategoriaProducto categoria) {
		categoria.setCategoria(categoria.getCategoria().toUpperCase());
		categoriaRepository.save(categoria);
	}

	@Override
	public CategoriaProducto buscarPorId(Long idCategoria) {
		return categoriaRepository.findById(idCategoria).orElse(null);
	}

	@Override
	public void eliminar(Long idCategoria) {
		categoriaRepository.deleteById(idCategoria);
	}

	@Override
	public boolean existePorId(Long idCategoria) {
		return categoriaRepository.existsById(idCategoria);
	}

	@Override
	public CategoriaProducto buscarPorCategoria(String categoria) {
		return categoriaRepository.findByCategoria(categoria);
	}

	@Override
	public boolean existePorCategoria(String categoria) {
		return categoriaRepository.existsByCategoria(categoria);
	}

	

}
