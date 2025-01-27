package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Salida;
import com.KangreSystem.models.repository.SalidaRepository;
import com.KangreSystem.models.service.ISalidaService;

@Service
public class SalidaServImp implements ISalidaService {
	
	@Autowired
	private SalidaRepository salidaRepo;

	@Override
	public List<Salida> listar() {
		return (List<Salida>) salidaRepo.findAll();
	}

	@Override
	public void guardar(Salida salida) {
		salidaRepo.save(salida);
	}

	@Override
	public void eliminar(Long idSalida) {
		salidaRepo.deleteById(idSalida);
	}

	@Override
	public Salida buscarPorId(Long idSalida) {
		return salidaRepo.findById(idSalida).orElse(null);
	}

	@Override
	public void guardarLista(List<Salida> salidas) {
		salidaRepo.saveAll(salidas);
	}

	@Override
	public List<Salida> buscarPorTipoFecha(String tipo, Date fecha) {
		return salidaRepo.findByTipoAndFecha(tipo, fecha);
	}

	@Override
	public void eliminarLista(List<Salida> salidas) {
		salidaRepo.deleteAll(salidas);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Salida> salidasMesActual() {
		List<Salida> salidas = (List<Salida>) salidaRepo.findAll();
		List<Salida> salidasMesActual = new ArrayList<>();
		Date fechaActual = new Date();
		
		for (Salida salida : salidas) {
			if (salida.getFecha().getMonth() == fechaActual.getMonth()) {
				salidasMesActual.add(salida);
			}
		}
		
		
		return salidasMesActual;
	}

	@Override
	public List<Salida> buscarPorInsumo(Insumo insumo) {
		return salidaRepo.findByInsumo(insumo);
	}

	@Override
	public List<Salida> buscarPorPedido(Pedido pedido) {
		return salidaRepo.findByPedido(pedido);
	}

	@Override
	public List<Salida> buscarPorAveria(Averia averia) {
		return salidaRepo.findByAveria(averia);
	}

	@Override
	public List<Salida> buscarPorDevolucion(Devolucion devolucion) {
		return salidaRepo.findByDevolucion(devolucion);
	}

	@Override
	public List<Salida> buscarPorTipo(String tipo) {
		return salidaRepo.findByTipo(tipo);
	}

	@Override
	public List<Salida> buscarPorFecha(Date fecha) {
		return salidaRepo.findByFecha(fecha);
	}

}
