package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleAveria;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.Ingrediente;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.entity.Salida;
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.entity.Venta;
import com.KangreSystem.models.service.IAveriaService;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IDetalleAveriaService;
import com.KangreSystem.models.service.IDetalleCompraService;
import com.KangreSystem.models.service.IDetalleDevolucionServ;
import com.KangreSystem.models.service.IDetalleLlegadaCompraServ;
import com.KangreSystem.models.service.IDetallePedidoServ;
import com.KangreSystem.models.service.IDevolucionServ;
import com.KangreSystem.models.service.IEntradaService;
import com.KangreSystem.models.service.IIngredienteService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.ILlegadaCompraServ;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IProductoService;
import com.KangreSystem.models.service.ISalidaService;
import com.KangreSystem.models.service.IVencimientoLoteServ;
import com.KangreSystem.models.service.IVentaService;

@Service
public class InventarioServImp implements IInventarioService{
	
	@Autowired
	private ICompraService compraService;
	
	@Autowired
	private IPedidoService pedidoService;
	
	@Autowired
	private IDevolucionServ devolucionService;
	
	@Autowired
	private IAveriaService averiaService;
	
	@Autowired
	private IDetalleAveriaService detalleAveriaService;
	
	@Autowired
	private ILlegadaCompraServ llegadaService;
	
	@Autowired
	private IDetalleCompraService detalleCompraServ;
	
	@Autowired
	private IDetalleLlegadaCompraServ detalleLlegadaService;
	
	@Autowired
	private IDetalleDevolucionServ detalleDevolucionService;
	
	@Autowired
	private IDetallePedidoServ detallePedidoService;
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	@Autowired
	private IIngredienteService ingredienteService;
	
	@Autowired
	private IEntradaService entradaService;
	
	@Autowired
	private ISalidaService salidaService;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IVentaService ventaService;
	
	@Autowired
	private IProductoService productoService;
	
	@Override
	@Transactional
	public void comprar(Compra compra, List<DetalleCompra> detalles) {
		
		compraService.guardar(compra);
		
		for (DetalleCompra detalle : detalles) {
			detalle.setIdDetalle(null);
			detalle.setCompra(compra);
		}
		
		detalleCompraServ.guardarLista(detalles);
	}

	@Override
	@Transactional
	public void recibirCompra(LlegadaCompra llegada, List<DetalleLlegadaCompra> detalles,
			List<VencimientoLote> vencimientos) {
		Compra compra = llegada.getCompra();
		List<Entrada> entradas = agregarUnidades(detalles);
		
		for (DetalleLlegadaCompra detalle : detalles) {
			detalle.setIdDetalle(null);
			actualizarEntradasSalidasInsumo(detalle.getInsumo());
		}
		
		llegadaService.guardar(llegada);
		detalleLlegadaService.guardarLista(detalles);
		
		compra.setEstado("RECIBIDA");
		compraService.guardar(compra);
		
		vencimientoService.guardarLista(vencimientos);
		
		for (Entrada entrada : entradas) {
			Insumo insumo = entrada.getInsumo();
			insumo.setStockActual((float) (insumo.getStockActual() + entrada.getCantidad()));
			insumoService.guardar(insumo);
		}
	
		entradaService.guardarLista(entradas);
		actualizarStockActualInsumo();
		actualizarEntradasSalidas();
	}
	
	public List<Entrada> agregarUnidades(List<DetalleLlegadaCompra> detalles) {
		List<Entrada> entradas = new ArrayList<>();
		
		for (DetalleLlegadaCompra detalle : detalles) {
			Entrada entrada = new Entrada();
			
			entrada.setInsumo(detalle.getInsumo());
			entrada.setCantidad(detalle.getCantidad());
			entrada.setLlegada(detalle.getLlegada());
			entrada.setFecha(new Date());
			entrada.setHora(new Date());
			
			entradas.add(entrada);
		}
		return entradas;
	}	

	@Override
	@Transactional
	public void devolver(Devolucion devolucion, List<DetalleDevolucion> detalles) {
		List<Salida> salidas = new ArrayList<>();
 		
		devolucion.setFecha(new Date());
		devolucion.setHora(new Date());
		
		devolucionService.guardar(devolucion);
		
		for (DetalleDevolucion detalleDevolucion : detalles) {
			Salida salida = new Salida();
			Insumo insumo = detalleDevolucion.getInsumo();
			VencimientoLote vencimiento = vencimientoService.buscarPorInsumoFechaLote(insumo, detalleDevolucion.getFechaVencimiento(), detalleDevolucion.getLote());
			
			detalleDevolucion.setIdDetalle(null);
			detalleDevolucion.setDevolucion(devolucion);
			
			salida.setDevolucion(devolucion);
			salida.setAveria(null);
			salida.setPedido(null);
			salida.setCantidad(Float.parseFloat(detalleDevolucion.getCantidad().toString()));
			salida.setFecha(new Date());
			salida.setHora(new Date());
			salida.setInsumo(detalleDevolucion.getInsumo());
			salida.setTipo("DEVOLUCION");
			
			salidas.add(salida);
			
			if (vencimiento.getCantidadDisponible() > 0 && vencimiento.getCantidadDisponible() >= detalleDevolucion.getCantidad()) {
				vencimiento.setCantidadDisponible(vencimiento.getCantidadDisponible() - detalleDevolucion.getCantidad());
			}
			
			vencimientoService.guardar(vencimiento);
			insumoService.guardar(insumo);
			salidaService.guardar(salida);
			
			actualizarEntradasSalidasInsumo(insumo);
		}
		
		detalleDevolucionService.guardarLista(detalles);
		actualizarStockActualInsumo();
	}

	@Override
	@Transactional
	public void averiar(Averia averia, List<DetalleAveria> detalles) {
		List<Salida> salidas = new ArrayList<>();
		
		averia.setFecha(new Date());
		averia.setHora(new Date());
		
		averiaService.guardar(averia);
		
		for (DetalleAveria detalleAveria : detalles) {
			Salida salida = new Salida();
			Insumo insumo = detalleAveria.getInsumo();
			VencimientoLote vencimiento = vencimientoService.buscarPorInsumoFechaLote(insumo, detalleAveria.getFechaVencimiento(), detalleAveria.getLote());
			
			detalleAveria.setIdDetalle(null);
			detalleAveria.setAveria(averia);
			
			salida.setCantidad(Float.parseFloat(detalleAveria.getCantidad().toString()));
			salida.setInsumo(insumo);
			salida.setAveria(averia);
			salida.setDevolucion(null);
			salida.setPedido(null);
			salida.setFecha(new Date());
			salida.setHora(new Date());
			salida.setTipo("AVERIA");
			
			salidas.add(salida);
			
			if (vencimiento.getCantidadDisponible() > 0 && vencimiento.getCantidadDisponible() >= detalleAveria.getCantidad()) {
				vencimiento.setCantidadDisponible(vencimiento.getCantidadDisponible() - detalleAveria.getCantidad());
			}
			
			vencimientoService.guardar(vencimiento);
			insumoService.guardar(insumo);
			salidaService.guardar(salida);
			
			actualizarEntradasSalidasInsumo(insumo);
		}
		
		detalleAveriaService.guardarLista(detalles);
		actualizarStockActualInsumo();
	}

	@Override
	@Transactional
	public void vender(Pedido pedido) {
		List<DetallePedido> detalles = detallePedidoService.buscarDetallesPorPedido(pedido);
		Salida salida = new Salida();
		Venta venta = new Venta();
		
		try {
			
			for (DetallePedido detallePedido : detalles) {
				Producto producto = detallePedido.getProducto();
				int cantidadProducto = detallePedido.getCantidad();
				
				List<Ingrediente> ingredientes = ingredienteService.buscarPorProducto(producto);
				
				for (Ingrediente ingrediente : ingredientes) {
					Insumo insumo = ingrediente.getInsumo();
					float totalIngrediente = cantidadProducto * ingrediente.getCantidad();
					
					restarUnidadesIngrediente(insumo, totalIngrediente);
					
					salida.setPedido(pedido);
					salida.setAveria(null);
					salida.setDevolucion(null);
					salida.setFecha(new Date());
					salida.setHora(new Date());	
					salida.setCantidad(totalIngrediente);
					salida.setInsumo(insumo);
					salida.setTipo("VENTA");
					
					salidaService.guardar(salida);
					
					actualizarEntradasSalidasInsumo(insumo);
				}
				
				producto.setCantVendida(producto.getCantVendida() + cantidadProducto);
				
				productoService.guardar(producto);
			}
			
			venta.setFecha(new Date());
			venta.setHora(new Date());
			venta.setPedido(pedido);
			venta.setTotal(pedido.getTotal());
			
			ventaService.guardar(venta);
			pedidoService.guardar(pedido);
			detallePedidoService.guardarDetalles(detalles);			
			
			actualizarStockActualInsumo();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public void restarUnidadesIngrediente(Insumo insumo, Float cantidad) {
		List<VencimientoLote> vencimientos = vencimientoService.vencimientoParaVender(insumo);
		float cantTotal = cantidad;
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			
			if (vencimientoLote.getCantidadDisponible() < cantTotal && cantTotal > 0) {
				cantTotal -= vencimientoLote.getCantidadDisponible();
				vencimientoLote.setCantidadDisponible((float) 0);
			}
			
			if (vencimientoLote.getCantidadDisponible() >= cantTotal && cantTotal > 0) {
				vencimientoLote.setCantidadDisponible(vencimientoLote.getCantidadDisponible() - cantTotal);
				cantTotal = 0;
			}
			
			vencimientoService.guardar(vencimientoLote);
		}
	}

	@Override
	@Transactional
	public void actualizarStockActualInsumo() {
		List<Insumo> insumos = insumoService.listar();
		List<VencimientoLote> vencimientos = new ArrayList<>();
		
		try {
			for (Insumo insumo : insumos) {
				vencimientos = new ArrayList<>();
				vencimientos = vencimientoService.buscarPorInsumo(insumo);
				
				insumo.setStockActual((float) 0);
				
				for (VencimientoLote vencimientoLote : vencimientos) {
					insumo.setStockActual((float) (vencimientoLote.getCantidadDisponible() + insumo.getStockActual()));
				}
				
				insumoService.guardar(insumo);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	@Transactional
	public void cancelarLlegada(LlegadaCompra llegada) {
		Compra compra = llegada.getCompra();
		List<VencimientoLote> vencimientos = vencimientoService.buscarPorLlegada(llegada);
		List<Entrada> entradas = entradaService.buscarPorLlegada(llegada);
		
		entradaService.eliminarLista(entradas);
		
		for (VencimientoLote vencimiento : vencimientos) {
			actualizarEntradasSalidasInsumo(vencimiento.getInsumo());
		}
		
		vencimientoService.eliminarLista(vencimientos);
		
		actualizarStockActualInsumo();
		
		compra.setEstado("SOLICITADA");
		compraService.guardar(compra);
		
		llegadaService.eliminar(llegada.getIdLlegada());
		
	}

	@Override
	@Transactional
	@SuppressWarnings("deprecation")
	public void cancelarAveria(Averia averia) {
		List<DetalleAveria> detalles = detalleAveriaService.buscarDetallesPorAveria(averia);
		
		averia.getFecha().setHours(0);
		averia.getFecha().setMinutes(0);
		averia.getFecha().setSeconds(0);
		
		salidaService.eliminarLista(salidaService.buscarPorAveria(averia));
		
		for (DetalleAveria detalleAveria : detalles) {
			VencimientoLote vencimiento = vencimientoService.buscarPorInsumoFechaLote(detalleAveria.getInsumo(), detalleAveria.getFechaVencimiento(), detalleAveria.getLote());
			
			vencimiento.setCantidadDisponible(vencimiento.getCantidadDisponible() + detalleAveria.getCantidad());
			vencimientoService.guardar(vencimiento);
			
			actualizarEntradasSalidasInsumo(detalleAveria.getInsumo());
		}
		
		actualizarStockActualInsumo();
		
		detalleAveriaService.eliminarLista(detalles);
		averiaService.eliminar(averia.getIdAveria());
		
	}

	@Override
	@Transactional
	public void actualizarEntradasSalidasInsumo(Insumo insumo) {
		
		insumo.setEntradas(entradaService.buscarPorInsumo(insumo).size());
		insumo.setSalidas(salidaService.buscarPorInsumo(insumo).size());
		
		insumoService.guardar(insumo);
	}

	
	@Override
	@Transactional
	public boolean cancelarVenta(Pedido pedido) {
		List<DetallePedido> detalles = detallePedidoService.buscarDetallesPorPedido(pedido);
		Venta venta = ventaService.buscarPorPedido(pedido);
		
		salidaService.eliminarLista(salidaService.buscarPorPedido(pedido));
		
		for (DetallePedido detalle : detalles) {
			Producto producto = detalle.getProducto();
			List<Ingrediente> ingredientes = ingredienteService.buscarPorProducto(producto);
			List<VencimientoLote> vencimientos = new ArrayList<>();
			VencimientoLote vencimiento = new VencimientoLote();
			
			for (Ingrediente ingrediente : ingredientes) {
				Insumo insumo = ingrediente.getInsumo();
				vencimientos = vencimientoService.vencimientoParaVender(insumo);
				
				if (!vencimientos.isEmpty()) {
					vencimiento = vencimientos.get(0);
					
					vencimiento.setCantidadDisponible(ingrediente.getCantidad() * detalle.getCantidad());
					
					actualizarEntradasSalidasInsumo(insumo);
				} else {
					return false;
				}
			}
			
			producto.setCantVendida(producto.getCantVendida() - detalle.getCantidad());
			
			productoService.guardar(producto);
			vencimientoService.guardar(vencimiento);
		}
		
		pedido.setEstado("CANCELADO");
		pedidoService.guardar(pedido);
		
		ventaService.eliminar(venta.getIdVenta());
		
		actualizarStockActualInsumo();
		
		return true;
	}

	@Override
	@Transactional
	public void actualizarInventario() {
		actualizarStockActualInsumo();
		actualizarEntradasSalidas();
	}

	@Override
	@Transactional
	public void actualizarEntradasSalidas() {
		List<Insumo> insumos = insumoService.listar();
		
		for (Insumo insumo : insumos) {
			List<Entrada> entradas = entradaService.buscarPorInsumo(insumo);
			List<Salida> salidas = salidaService.buscarPorInsumo(insumo);
			
			insumo.setEntradas(entradas.size());
			insumo.setSalidas(salidas.size());
			
			insumoService.guardar(insumo);
		}
		
	}

}
