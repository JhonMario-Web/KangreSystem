package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Novedad;
import com.KangreSystem.models.repository.NovedadRepository;
import com.KangreSystem.models.service.INovedadService;

@Service
public class NovedadServImp implements INovedadService {
	
	@Autowired
	private NovedadRepository novedadRepository;
	
	@Override
	public List<Novedad> listar() {
		return (List<Novedad>) novedadRepository.findAll();
	}

	@Override
	public void guardar(Novedad novedad) {
		novedad.setAsunto(novedad.getAsunto().toUpperCase());
		novedad.setMensaje(novedad.getMensaje().toUpperCase());
		novedadRepository.save(novedad);
	}

	@Override
	public void eliminar(Long idNovedad) {
		novedadRepository.deleteById(idNovedad);
	}

	@Override
	public Novedad buscarPorId(Long idNovedad) {
		return novedadRepository.findById(idNovedad).orElse(null);
	}

	@Override
	public List<Novedad> buscarPorFecha(Date fecha) {
		return novedadRepository.findAllByFecha(fecha);
	}

	@Override
	public boolean existePorId(Long idNovedad) {
		return novedadRepository.existsById(idNovedad);
	}

}
