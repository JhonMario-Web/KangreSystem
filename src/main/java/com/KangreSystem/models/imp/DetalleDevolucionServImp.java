package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.repository.DetalleDevolucionRepository;
import com.KangreSystem.models.service.IDetalleDevolucionServ;

@Service
public class DetalleDevolucionServImp implements IDetalleDevolucionServ{
	
	@Autowired
	private DetalleDevolucionRepository detalleRepo; 

	@Override
	public List<DetalleDevolucion> listar() {
		return (List<DetalleDevolucion>) detalleRepo.findAll();
	}

	@Override
	public void guardar(DetalleDevolucion detalle) {
		detalleRepo.save(detalle);
	}

	@Override
	public void guardarLista(List<DetalleDevolucion> detalles) {
		detalleRepo.saveAll(detalles);
	}

	@Override
	public void eliminar(Long idDetalle) {
		detalleRepo.deleteById(idDetalle);
	}

	@Override
	public DetalleDevolucion buscarPorId(Long idDetalle) {
		return detalleRepo.findById(idDetalle).orElse(null);
	}

	@Override
	public List<DetalleDevolucion> buscarPorDevolucion(Devolucion devolucion) {
		return detalleRepo.findByDevolucion(devolucion);
	}

}
