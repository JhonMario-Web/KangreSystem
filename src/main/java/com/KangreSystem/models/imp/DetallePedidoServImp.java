package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.repository.DetallePedidoRepo;
import com.KangreSystem.models.service.IDetallePedidoServ;

@Service
public class DetallePedidoServImp implements IDetallePedidoServ {
	
	@Autowired
	private DetallePedidoRepo detalleRepo;

	@Override
	public List<DetallePedido> listar() {
		return (List<DetallePedido>) detalleRepo.findAll();
	}

	@Override
	public void guardar(DetallePedido detalle) {
		detalleRepo.save(detalle);
	}

	@Override
	public void eliminar(Long idDetalle) {
		detalleRepo.deleteById(idDetalle);
	}

	@Override
	public DetallePedido buscarPorId(Long idDetalle) {
		return detalleRepo.findById(idDetalle).orElse(null);
	}

	@Override
	public void guardarDetalles(List<DetallePedido> detalles) {
		detalleRepo.saveAll(detalles);
	}

	@Override
	public List<DetallePedido> buscarDetallesPorPedido(Pedido pedido) {
		return detalleRepo.findByPedido(pedido);
	}

}
