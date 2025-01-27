package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.DetalleAveria;
import com.KangreSystem.models.repository.DetalleAveriaRepository;
import com.KangreSystem.models.service.IDetalleAveriaService;

@Service
public class DetalleAveriaServImp implements IDetalleAveriaService{
	
	@Autowired
	private DetalleAveriaRepository detalleRepository;

	@Override
	public List<DetalleAveria> listar() {
		return (List<DetalleAveria>) detalleRepository.findAll();
	}

	@Override
	public void guardar(DetalleAveria detalle) {
		detalleRepository.save(detalle);
	}

	@Override
	public DetalleAveria buscarPorId(Long idDetalle) {
		return detalleRepository.findById(idDetalle).orElse(null);
	}

	@Override
	public void eliminar(Long idDetalle) {
		detalleRepository.deleteById(idDetalle);
	}

	@Override
	public void guardarLista(List<DetalleAveria> detalles) {
		detalleRepository.saveAll(detalles);
	}

	@Override
	public List<DetalleAveria> buscarDetallesPorAveria(Averia averia) {
		return detalleRepository.findByAveria(averia);
	}

	@Override
	public void eliminarLista(List<DetalleAveria> detalles) {
		detalleRepository.deleteAll(detalles);
	}

}
