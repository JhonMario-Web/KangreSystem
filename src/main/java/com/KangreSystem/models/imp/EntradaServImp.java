package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.repository.EntradaRepository;
import com.KangreSystem.models.service.IEntradaService;

@Service
public class EntradaServImp implements IEntradaService{
	
	@Autowired
	private EntradaRepository entradaRepo;

	@Override
	public List<Entrada> listar() {
		return (List<Entrada>) entradaRepo.findAll();
	}

	@Override
	public void guardar(Entrada entrada) {
		entradaRepo.save(entrada);
	}

	@Override
	public void eliminar(Long idEntrada) {
		entradaRepo.deleteById(idEntrada);
	}

	@Override
	public Entrada buscarPorId(Long idEntrada) {
		return entradaRepo.findById(idEntrada).orElse(null);
	}

	@Override
	public void guardarLista(List<Entrada> entradas) {
		entradaRepo.saveAll(entradas);
	}

	@Override
	public List<Entrada> buscarPorLlegada(LlegadaCompra llegada) {
		return entradaRepo.findByLlegada(llegada);
	}

	@Override
	public void eliminarLista(List<Entrada> entradas) {
		entradaRepo.deleteAll(entradas);
	}

	@Override
	public List<Entrada> buscarPorFecha(Date fecha) {
		return entradaRepo.findByFecha(fecha);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Entrada> entradasMesActual() {
		List<Entrada> entradas = (List<Entrada>) entradaRepo.findAll();
		List<Entrada> entradasPorMes = new ArrayList<>();
		Date fechaActual = new Date(); 
		
		for (Entrada entrada : entradas) {
			if (entrada.getFecha().getMonth() == fechaActual.getMonth()) {
				entradasPorMes.add(entrada);
			}
		}
		
		return entradasPorMes;
	}

	@Override
	public List<Entrada> buscarPorInsumo(Insumo insumo) {
		return entradaRepo.findByInsumo(insumo);
	}

}
