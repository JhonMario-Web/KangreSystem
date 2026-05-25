package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;
import com.KangreSystem.models.repository.PedidoMesaRepository;
import com.KangreSystem.models.service.IPedidoMesaService;

@Service
public class PedidoMesaServImp implements IPedidoMesaService {
	
	@Autowired
	private PedidoMesaRepository pedidoMesaRepo;

	@Override
	public List<PedidoMesa> listar() {
		return (List<PedidoMesa>) pedidoMesaRepo.findAll();
	}

	@Override
	public void guardar(PedidoMesa pedidoMesa) {
		pedidoMesaRepo.save(pedidoMesa);
	}

	@Override
	public void eliminar(Long idPedidoMesa) {
		pedidoMesaRepo.deleteById(idPedidoMesa);
	}

	@Override
	public PedidoMesa buscarPorId(Long idPedidoMesa) {
		return pedidoMesaRepo.findById(idPedidoMesa).orElse(null);
	}

	@Override
	public void guardarLista(List<PedidoMesa> lista) {
		pedidoMesaRepo.saveAll(lista);
	}

	@Override
	public List<PedidoMesa> buscarPorPedido(Pedido pedido) {
		return pedidoMesaRepo.findByPedido(pedido);
	}

}
