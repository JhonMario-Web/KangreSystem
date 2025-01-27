package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.repository.AveriaRepository;
import com.KangreSystem.models.service.IAveriaService;

@Service
public class AveriaServImp implements IAveriaService {
	
	@Autowired
	private AveriaRepository averiaRepository;

	@Override
	public List<Averia> listar() {
		return (List<Averia>) averiaRepository.findAll();
	}

	@Override
	public Averia buscarPorId(Long idAveria) {
		return averiaRepository.findById(idAveria).orElse(null);
	}

	@Override
	public void guardar(Averia averia) {
		averiaRepository.save(averia);
	}

	@Override
	public void eliminar(Long idAveria) {
		averiaRepository.deleteById(idAveria);
	}

	@Override
	public Averia buscarPorNumeroAveria(String numeroAveria) {
		return averiaRepository.findByNumeroAveria(numeroAveria);
	}

	@Override
	public List<Averia> buscarPorFecha(Date fecha) {
		return averiaRepository.findByFecha(fecha);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean unicaPorDia() {
		Date fechaActual = new Date();
		
		fechaActual.setHours(0);
		fechaActual.setMinutes(0);
		fechaActual.setSeconds(0);
		
		return averiaRepository.existsByFecha(fechaActual);
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<Averia> averiasMesActual() {
		List<Averia> averias = (List<Averia>) averiaRepository.findAll();
		List<Averia> averiasMesActual = new ArrayList<>();
		Date fechaActual = new Date();
		
		for (Averia averia : averias) {
			if (averia.getFecha().getMonth() == fechaActual.getMonth()) {
				averiasMesActual.add(averia);
			}
		}
		
		return averiasMesActual;
	}

	@Override
	public Long precioAveriasMesActual() {
		List<Averia> averias = averiasMesActual();
		long total = 0;
		
		for (Averia averia : averias) {
			total += averia.getTotal();
		}
		
		return total;
	}

}
