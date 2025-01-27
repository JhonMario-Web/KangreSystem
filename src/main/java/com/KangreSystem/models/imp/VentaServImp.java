package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Venta;
import com.KangreSystem.models.repository.VentaRepository;
import com.KangreSystem.models.service.IVentaService;

@Service
public class VentaServImp implements IVentaService{
	
	@Autowired
	private VentaRepository ventaRepo;

	@Override
	public List<Venta> listar() {
		return (List<Venta>) ventaRepo.findAll();
	}

	@Override
	public void guardar(Venta venta) {
		ventaRepo.save(venta);
	}

	@Override
	public void guardarLista(List<Venta> ventas) {
		ventaRepo.saveAll(ventas);
	}

	@Override
	public void eliminar(Long idVenta) {
		ventaRepo.deleteById(idVenta);
	}

	@Override
	public void eliminarLista(List<Venta> ventas) {
		ventaRepo.deleteAll(ventas);
	}

	@Override
	public Venta buscarPorId(Long idVenta) {
		return ventaRepo.findById(idVenta).orElse(null);
	}

	@Override
	public Venta buscarPorPedido(Pedido pedido) {
		return ventaRepo.findByPedido(pedido);
	}

}
