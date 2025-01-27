package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.LlegadaCompraRepository;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IDetalleCompraService;
import com.KangreSystem.models.service.IDetalleLlegadaCompraServ;
import com.KangreSystem.models.service.ILlegadaCompraServ;

@Service
public class LlegadaCompraServImp implements ILlegadaCompraServ{
	
	@Autowired
	private LlegadaCompraRepository llegadaRepo; 
	
	@Autowired
	private IDetalleLlegadaCompraServ detalleLlegadaService;
	
	@Autowired
	private IDetalleCompraService detalleCompraService;
	
	@Autowired
	private ICompraService compraService;

	@Override
	public List<LlegadaCompra> listar() {
		return (List<LlegadaCompra>) llegadaRepo.findAll();
	}

	@Override
	public void guardar(LlegadaCompra llegada) {
		llegada.setNumeroFactura(llegada.getNumeroFactura().toUpperCase());
		if (!llegada.getNovedad().isEmpty()) {
			llegada.setNovedad(llegada.getNovedad().toUpperCase());
		}
		
		llegadaRepo.save(llegada);
	}

	@Override
	public void eliminar(Long idLlegada) {
		llegadaRepo.deleteById(idLlegada);
	}

	@Override
	public LlegadaCompra buscarPorId(Long idLlegada) {
		return llegadaRepo.findById(idLlegada).orElse(null);
	}

	@Override
	public String numeroLlegadaCompra() {
		List<LlegadaCompra> llegadas = (List<LlegadaCompra>) llegadaRepo.findAll(); 
		
		if (llegadas.isEmpty()) {
			return "1";
		}
		
		LlegadaCompra ultimaLlegada = llegadas.get(llegadas.size() - 1);
		Integer ultimoNumeroLlegada = Integer.parseInt(ultimaLlegada.getNumeroLlegada());
		ultimoNumeroLlegada++;
		
		return ultimoNumeroLlegada.toString();
	}

	@Override
	public boolean cantidadesIguales(List<DetalleLlegadaCompra> aRecibir) {
		for (DetalleLlegadaCompra detalleLlegadaCompra : aRecibir) {
			if (!detalleLlegadaCompra.isCantEquals()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean insumosFaltantes(LlegadaCompra llegada, List<DetalleLlegadaCompra> aRecibir) {
		Compra compra = llegada.getCompra();
		List<DetalleCompra> solicitados = detalleCompraService.buscarDetallesPorCompra(compra);
		
		if (solicitados.size() == aRecibir.size()) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean preciosIguales(LlegadaCompra llegada, List<DetalleLlegadaCompra> aRecibir) {
		Compra compra = llegada.getCompra();
		List<DetalleCompra> solicitados = detalleCompraService.buscarDetallesPorCompra(compra);
		
		for (DetalleCompra detalleCompra : solicitados) {
			for (DetalleLlegadaCompra detalleLlegada : aRecibir) {
				if (detalleCompra.getInsumo().getIdInsumo().equals(detalleLlegada.getInsumo().getIdInsumo())) {
					if (!detalleCompra.getInsumo().getPrecio().equals(detalleLlegada.getInsumo().getPrecio())) {
						return false;
					}					
				}
			}
		}
		
		return true;
	}

	@Override
	@Transactional
	public void recibirCompraEnCero(Compra compra) {
		LlegadaCompra llegada = new LlegadaCompra();
		List<DetalleCompra> detallesCompra = detalleCompraService.buscarDetallesPorCompra(compra);
		List<DetalleLlegadaCompra> detallesLlegada = new ArrayList<>();
		
		llegada.setFecha(new Date());
		llegada.setHora(new Date());
		llegada.setTotal((long) 0);
		llegada.setCompra(compra);
		llegada.setNumeroLlegada(numeroLlegadaCompra());
		llegada.setAdmin(null);
		llegada.setNumeroFactura(null);
		llegada.setNovedad("NO LLEGO LA COMPRA, SE RECIBE TODO EN CEROS");
		
		for (DetalleCompra detalleCompra : detallesCompra) {
			DetalleLlegadaCompra detalle = new DetalleLlegadaCompra();
			
			detalle.setInsumo(detalleCompra.getInsumo());
			detalle.setCantidad(0);
			detalle.setLlegada(llegada);
			detalle.setSubtotal((long)0);
			
			detallesLlegada.add(detalle);
		}
		
		llegadaRepo.save(llegada);
		detalleLlegadaService.guardarLista(detallesLlegada);
		
		compra.setEstado("RECIBIDA");
		compraService.guardar(compra);
	}

	@Override
	public boolean existePorNumeroFactura(String numeroFactura) {
		return llegadaRepo.existsByNumeroFactura(numeroFactura);	
	}

	@Override
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
	public List<LlegadaCompra> buscarPorProveedor(Proveedor proveedor) {
		return llegadaRepo.findByProveedor(proveedor);
	}

	@Override
	public LlegadaCompra buscarPorNumeroFactura(String numeroFactura) {
		return llegadaRepo.findByNumeroFactura(numeroFactura);
	}

}
