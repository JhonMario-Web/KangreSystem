package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.PagoEfectivo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;
import com.KangreSystem.models.entity.PuntoAcumulado;
import com.KangreSystem.models.repository.PedidoRepository;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IDetallePedidoServ;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IMesaService;
import com.KangreSystem.models.service.IPagoEfectivoService;
import com.KangreSystem.models.service.IPedidoMesaService;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IPuntoAcumuladoService;

@Service
public class PedidoServImp implements IPedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private IPedidoMesaService pedidoMesaService;
	
	@Autowired
	private IDetallePedidoServ detallePedidoService;
	
	@Autowired
	private IPuntoAcumuladoService acumService;
	
	@Autowired
	private IPagoEfectivoService pagoEfectivoServ;
	
	@Autowired
	private IMesaService mesaService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IInventarioService inventarioService;

	@Override
	public List<Pedido> listar() {
		return (List<Pedido>) pedidoRepository.findAll();
	}

	@Override
	public void guardar(Pedido pedido) {
		pedidoRepository.save(pedido);
	}

	@Override
	public Pedido buscarPorId(Long idPedido) {
		return pedidoRepository.findById(idPedido).orElse(null);
	}

	@Override
	@Transactional
	public void eliminar(Long idPedido) {
		Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
		List<PuntoAcumulado> acumulados = acumService.buscarPorPedido(pedido);
		Cliente cliente = null;
		
		if (pedido.getCliente() != null) {
			cliente = pedido.getCliente();
			
			acumService.eliminarLista(acumulados);
			
			int total = acumService.calcularTotalPuntos(cliente);
			cliente.setKangrepuntos(total);
			clienteService.guardar(cliente);
		}
		
		pedidoRepository.deleteById(idPedido);
	}

	@Override
	public Integer calcularKangrepuntos(Long subtotal) {
		Integer kangrepuntos =  (int) (subtotal / 800);
		return kangrepuntos;
	}

	public Long calcularSubtotalPedido(List<DetallePedido> detalles) {
		Long subtotal = (long) 0;
		
		for (DetallePedido detalle : detalles) {
			subtotal = detalle.getSubtotal() + subtotal;
		}
		
		return subtotal;
	}
	
	public Long calcularTotalPedido(Long subtotal) {
		Long total = subtotal + calcularIva(subtotal);
		return total;
	}

	@Override
	public Long calcularIva(Long subtotal) {
		Long iva = (long) (subtotal * 0.19f);
		return iva;
	}

	@Override
	public Pedido buscarPorNumeroOrden(String numeroOrden) {
		return pedidoRepository.findByNumeroOrden(numeroOrden);
	}

	@Override
	public List<Pedido> buscarPorEmpleado(Empleado empleado) {
		return pedidoRepository.findByEmpleado(empleado);
	}

	@Override
	public boolean filterIsValid(Pedido pedido) {
		if ((!pedido.getNumeroOrden().isEmpty() && pedido.getFecha() == null && pedido.getEstado().isEmpty())
				|| (pedido.getNumeroOrden().isEmpty() && pedido.getFecha() != null && pedido.getEstado().isEmpty())
				|| (pedido.getNumeroOrden().isEmpty() && pedido.getFecha() == null && !pedido.getEstado().isEmpty())
				|| (pedido.getNumeroOrden().isEmpty() && pedido.getFecha() != null && !pedido.getEstado().isEmpty())){
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void tomarOrden(Pedido pedido, List<DetallePedido> detalles, List<PedidoMesa> mesasAgregadas) {
		long iva = calcularIva(pedido.getSubtotal());
		
		pedido.setFecha(new Date());
		pedido.setHora(new Date());
		pedido.setEstado("SOLICITADO");
		pedido.setTotal(pedido.getSubtotal() + iva);
		pedido.setCalificado(false);
		
		pedidoRepository.save(pedido);
		
		if (!mesasAgregadas.isEmpty()) {
			for (PedidoMesa pedidoMesa : mesasAgregadas) {
				pedidoMesa.setPedido(pedido);
				mesaService.guardar(pedidoMesa.getMesa());
			}
			
			pedidoMesaService.guardarLista(mesasAgregadas);
		}
		
		for (DetallePedido detallePedido : detalles) {
			detallePedido.setIdDetalle(null);
		}
		
		detallePedidoService.guardarDetalles(detalles);
	}

	@Override
	@Transactional
	public PagoEfectivo pagarPedidoEfectivo(Pedido pedido, Long efectivo) {
		PagoEfectivo pago = new PagoEfectivo();
		List<PedidoMesa> mesasUtilizadas = pedidoMesaService.buscarPorPedido(pedido);
		
		if (!mesasUtilizadas.isEmpty()) {
			for (PedidoMesa pedidoMesa : mesasUtilizadas) {
				pedidoMesa.getMesa().setEstado("DESOCUPADA");
				
				mesaService.guardar(pedidoMesa.getMesa());
			}
		}
		
		if (pedido.getCliente() != null) {
			acumService.acumularPuntos(pedido);
		}
		
		pago.setPedido(pedido);
		pago.setEfectivo(efectivo);
		pago.setTotal(pedido.getTotal());
		
		if (efectivo >= pago.getTotal()) {
			pago.setCambio(efectivo - pedido.getTotal());
		}
		
		pedido.setEstado("COBRADO");
		
		pagoEfectivoServ.guardar(pago);
		inventarioService.vender(pedido);
		
		return pago;
	}

	@Override
	public List<Pedido> buscarPorEstado(String estado) {
		return pedidoRepository.findByEstado(estado);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Pedido> buscarPorEstadoFecha(String estado, Date fecha) {
		
		fecha.setHours(0);
		fecha.setMinutes(0);
		fecha.setSeconds(0);
		
		return pedidoRepository.findByEstadoAndFecha(estado, fecha);
	}

	@Override
	public List<Pedido> buscarPorFecha(Date fecha) {
		return pedidoRepository.findByFecha(fecha);
	}

	@Override
	@Transactional
	public void ordenSolicitadaVencida() {
		List<Pedido> pedidos = pedidoRepository.findByEstado("SOLICITADO");
		
		for (Pedido pedido : pedidos) {
			if (orderIsValid(pedido.getFecha())) {
				pedido.setEstado("CANCELADO");
				
				pedidoRepository.save(pedido);
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private boolean orderIsValid(Date fecha) {
		Date fechaActual = new Date();
		
		fechaActual.setSeconds(0);
		fechaActual.setMinutes(0);
		fechaActual.setHours(0);
		
		fecha.setSeconds(0);
		fecha.setMinutes(0);
		fecha.setHours(0);
		
		return fecha.after(fechaActual);
	}

}
