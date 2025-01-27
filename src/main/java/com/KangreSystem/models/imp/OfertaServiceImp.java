package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Oferta;
import com.KangreSystem.models.repository.OfertaRepository;
import com.KangreSystem.models.service.IOfertaService;

@Service
public class OfertaServiceImp implements IOfertaService {
	
	@Autowired
	private OfertaRepository ofertaRepository;

	@Override
	public List<Oferta> listar() {
		return (List<Oferta>) ofertaRepository.findAll();
	}

	@Override
	public void guardar(Oferta oferta) {
		ofertaRepository.save(oferta);
	}


	@Override
	public void eliminar(Long idOferta) {
		ofertaRepository.deleteById(idOferta);
	}

	@Override
	public Oferta buscarPorId(Long idOferta) {
		return ofertaRepository.findById(idOferta).orElse(null);
	}

	@Override
	public List<Oferta> buscarOfertasPorNombre(String nombre) {
		return ofertaRepository.findByNombreContaining(nombre);
	}

	@Override
	public List<Oferta> buscarPorEnabled(String enabled) {
		return ofertaRepository.findByEnabled(Boolean.parseBoolean(enabled));
	}
	
	@Override
	@Transactional
	public void actualizarOfertas() {
		List<Oferta> ofertas = (List<Oferta>) ofertaRepository.findAll();
		
		for (Oferta oferta : ofertas) {
			if (!offerIsValid(oferta.getFechaFin())) {
				
				oferta.setEnabled(false);
				ofertaRepository.save(oferta);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean offerIsValid(Date fechaFin) {
		Date fechaActual = new Date();
		
		fechaActual.setSeconds(0);
		fechaActual.setMinutes(0);
		fechaActual.setHours(0);
		
		fechaFin.setSeconds(0);
		fechaFin.setMinutes(0);
		fechaFin.setHours(0);
		
		return fechaFin.after(fechaActual);
	}
	
}
