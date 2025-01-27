package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.repository.DetalleCompraRepository;
import com.KangreSystem.models.service.IDetalleCompraService;

@Service
public class DetalleCompraServImp implements IDetalleCompraService{
	
	@Autowired
	private DetalleCompraRepository detalleRepository;

	@Override
	public List<DetalleCompra> listar() {
		return (List<DetalleCompra>) detalleRepository.findAll();
	}

	@Override
	public void guardar(DetalleCompra detalle) {
		detalleRepository.save(detalle);
	}

	@Override
	public void eliminar(Long idDetalle) {
		detalleRepository.deleteById(idDetalle);
	}

	@Override
	public DetalleCompra buscarPorId(Long idDetalle) {
		return detalleRepository.findById(idDetalle).orElse(null);
	}

	@Override
	public void guardarLista(List<DetalleCompra> detalles) {
		detalleRepository.saveAll(detalles);
	}

	@Override
	public List<DetalleCompra> buscarDetallesPorCompra(Compra compraAUX) {
		return detalleRepository.findByCompra(compraAUX);
	}

}
