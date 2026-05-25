package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.repository.DetalleLlegadaCompraRepo;
import com.KangreSystem.models.service.IDetalleLlegadaCompraServ;

@Service
public class DetalleLlegadaCompraServImp implements IDetalleLlegadaCompraServ{
	
	@Autowired
	private DetalleLlegadaCompraRepo detalleRepo;

	@Override
	public List<DetalleLlegadaCompra> listar() {
		return (List<DetalleLlegadaCompra>) detalleRepo.findAll();
	}

	@Override
	public void guardar(DetalleLlegadaCompra llegada) {
		detalleRepo.save(llegada);
	}

	@Override
	public void eliminar(Long idDetalle) {
		detalleRepo.deleteById(idDetalle);
	}

	@Override
	public void buscarPorId(Long idDetalle) {
		detalleRepo.findById(idDetalle).orElse(null);
	}

	@Override
	public void guardarLista(List<DetalleLlegadaCompra> detalles) {
		detalleRepo.saveAll(detalles);
	}

}
