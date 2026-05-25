package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.repository.CategoriaProveedorRepository;
import com.KangreSystem.models.service.ICategoriaProveedorService;

@Service
public class CategoriaProveedorServImp implements ICategoriaProveedorService {

	@Autowired
	private CategoriaProveedorRepository categoriaRepository;
	
	@Override
	public List<CategoriaProveedor> listar() {
		return (List<CategoriaProveedor>) categoriaRepository.findAll();
	}

	@Override
	public CategoriaProveedor buscarPorId(Long idCategoria) {
		return categoriaRepository.findById(idCategoria).orElse(null);
	}

	@Override
	public void guardar(CategoriaProveedor categoriaProveedor) {
		categoriaProveedor.setCategoria(categoriaProveedor.getCategoria().toUpperCase());
		categoriaRepository.save(categoriaProveedor);
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
	public boolean existePorCategoria(String categoria) {
		return categoriaRepository.existsByCategoria(categoria);
	}

	@Override
	public CategoriaProveedor buscarPorCategoria(String categoria) {
		return categoriaRepository.findByCategoria(categoria);
	}

}
