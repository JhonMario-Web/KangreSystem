package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoAcumulado;
import com.KangreSystem.models.entity.PuntoRedimido;
import com.KangreSystem.models.repository.PuntoRedimidoRepository;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;
import com.KangreSystem.models.service.IPuntoRedimidoService;

@Service
public class PuntoRedimidoServImp implements IPuntoRedimidoService {
	
	@Autowired
	private PuntoRedimidoRepository redenRepository;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IInventarioService inventarioService;

	@Override
	public List<PuntoRedimido> listar() {
		return (List<PuntoRedimido>) redenRepository.findAll();
	}

	@Override
	public void eliminar(Long idTrxn) {
		redenRepository.deleteById(idTrxn);
	}

	@Override
	public PuntoRedimido buscarPorId(Long idTrxn) {
		return redenRepository.findById(idTrxn).orElse(null);
	}
	
	@Override
	@Transactional
	public List<PuntoRedimido> redimirParte(Pedido pedido, int puntos) {
		Cliente cliente = pedido.getCliente();
		List<PuntoAcumulado> acumulados = acumService.buscarPorCliente(cliente);
		List<PuntoRedimido> redimidos = new ArrayList<>();
		
		for (PuntoAcumulado acumulado : acumulados) {
			PuntoRedimido redencion = new PuntoRedimido();
			
			redencion.setCliente(cliente);
			redencion.setPedido(pedido);
			redencion.setAcumulado(acumulado);
			redencion.setFecha(new Date());
			redencion.setHora(new Date());
			redencion.setTotal(acumService.calcularTotalPuntos(cliente));

			if (puntos < acumulado.getDisponibles() && acumulado.getDisponibles() > 0) {
				acumulado.setDisponibles(acumulado.getDisponibles() - puntos);
				redencion.setRedimidos(puntos);

				if (puntos > 0) {
					redimidos.add(redencion);
				}
				puntos = 0;
			}

			if (puntos >= acumulado.getDisponibles() && acumulado.getDisponibles() > 0) {
				puntos -= acumulado.getDisponibles();

				redencion.setRedimidos(acumulado.getDisponibles());
				acumulado.setDisponibles(0);

				redimidos.add(redencion);
			}
		}
		
		return redimidos;
	}

	@Override
	public List<PuntoRedimido> buscarPorCliente(Cliente cliente) {
		return redenRepository.findByCliente(cliente);
	}

	@Override
	public void guardarLista(List<PuntoRedimido> redimidos) {
		redenRepository.saveAll(redimidos);
	}

	
	@Override
	@Transactional
	public List<PuntoRedimido> redimirTodo(Pedido pedido) {
		Cliente cliente = pedido.getCliente();
		List<PuntoAcumulado> acumulados = acumService.buscarPorCliente(cliente);
		List<PuntoRedimido> redimidos = new ArrayList<>();
		
		int vlrTotalEnPuntos = (int) (pedido.getTotal() / 50);
		
		for (PuntoAcumulado acumulado : acumulados) {
			PuntoRedimido redencion = new PuntoRedimido();
			
			redencion.setCliente(cliente);
			redencion.setPedido(pedido);
			redencion.setAcumulado(acumulado);
			redencion.setFecha(new Date());
			redencion.setHora(new Date());
			redencion.setTotal(acumService.calcularTotalPuntos(cliente));

			if (vlrTotalEnPuntos < acumulado.getDisponibles() && acumulado.getDisponibles() > 0) {
				acumulado.setDisponibles(acumulado.getDisponibles() - vlrTotalEnPuntos);
				redencion.setRedimidos(vlrTotalEnPuntos);

				if (vlrTotalEnPuntos > 0) {
					redimidos.add(redencion);
				}

				vlrTotalEnPuntos = 0;
			}

			if (vlrTotalEnPuntos >= acumulado.getDisponibles() && acumulado.getDisponibles() > 0) {
				vlrTotalEnPuntos -= acumulado.getDisponibles();

				redencion.setRedimidos(acumulado.getDisponibles());
				acumulado.setDisponibles(0);

				redimidos.add(redencion);
			}
		}
		
		pedido.setFecha(new Date());
		pedido.setHora(new Date());
		pedido.setCalificado(false);
		pedido.setTipoPago("KANGREPUNTOS");
		pedido.setEstado("COBRADO");
		
		inventarioService.vender(pedido);
		redimirPedido(redimidos);
		
		return redimidos;
	}

	@Override
	@Transactional
	public void redimirPedido(List<PuntoRedimido> redimidos) {
		List<PuntoAcumulado> acumulados = new ArrayList<>();
		Cliente cliente = new Cliente();
				
		for (PuntoRedimido puntoRedimido : redimidos) {
			cliente = puntoRedimido.getCliente(); 
			acumulados.add(puntoRedimido.getAcumulado());
		}
		
		redenRepository.saveAll(redimidos);
		acumService.guardarAcumulados(acumulados);
		
		int total = acumService.calcularTotalPuntos(cliente);
		cliente.setKangrepuntos(total);
		
		clienteService.guardar(cliente);
	}

}
