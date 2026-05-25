package com.KangreSystem.models.imp;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.Restaurante;
import com.KangreSystem.models.repository.RestauranteRepository;
import com.KangreSystem.models.service.IRestauranteService;

@Service
public class RestauranteServImp implements IRestauranteService {
	
	@Autowired
	private RestauranteRepository restauranteRepo;

	@Override
	public List<Restaurante> listar() {
		return (List<Restaurante>) restauranteRepo.findAll();
	}

	@Override
	public void guardar(Restaurante restaurante) {
		restaurante.setDireccion(restaurante.getDireccion().toUpperCase());
		restaurante.setHorario(restaurante.getHorario().toUpperCase());
		restaurante.setNombre(restaurante.getNombre().toUpperCase());
		restauranteRepo.save(restaurante);
	}

	@Override
	public void eliminar(Long idRestaurante) {
		restauranteRepo.deleteById(idRestaurante);
	}

	@Override
	public Restaurante buscarPorId(Long idRestaurante) {
		return restauranteRepo.findById(idRestaurante).orElse(null);
	}

	@Override
	public Restaurante buscarPorNombre(String nombre) {
		return restauranteRepo.findByNombre(nombre);
	}

	@Override
	public boolean checkImgIsValid(MultipartFile file) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		int _5MB = 5120;
		long size = (file.getSize() / 1024); 
		
		if ((extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) && (size <= _5MB)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean existePorNombre(String nombre) {
		return restauranteRepo.existsByNombre(nombre);
	}

	@Override
	public List<Restaurante> buscarRestaurantesPorNombre(String nombre) {
		return restauranteRepo.findByNombreContaining(nombre);
	}

	@Override
	public List<Restaurante> buscarPorCiudad(String ciudad) {
		return restauranteRepo.findByCiudad(ciudad);
	}

}
