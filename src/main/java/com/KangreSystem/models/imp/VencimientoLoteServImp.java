package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.repository.VencimientoLoteRepo;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IVencimientoLoteServ;

@Service
public class VencimientoLoteServImp implements IVencimientoLoteServ{
	
	@Autowired
	private VencimientoLoteRepo vencimientoRepo;
	
	@Autowired
	private IInsumoService insumoService;

	@Override
	public List<VencimientoLote> listar() {
		return (List<VencimientoLote>) vencimientoRepo.findAll();
	}

	@Override
	public void guardar(VencimientoLote vencimiento) {
		vencimientoRepo.save(vencimiento);
	}

	@Override
	public void eliminar(Long idVencimiento) {
		vencimientoRepo.deleteById(idVencimiento);
	}

	@Override
	public VencimientoLote buscarPorId(Long idVencimiento) {
		return vencimientoRepo.findById(idVencimiento).orElse(null);
	}

	@Override
	public void guardarLista(List<VencimientoLote> vencimientos) {
		vencimientoRepo.saveAll(vencimientos);
	}

	@Override
	public List<VencimientoLote> buscarPorInsumo(Insumo insumo) {
		return vencimientoRepo.findByInsumo(insumo);
	}

	@Override
	public VencimientoLote buscarPorInsumoFechaLote(Insumo insumo, Date fechaVencimiento, String lote) {
		return vencimientoRepo.findByInsumoAndFechaAndLote(insumo, fechaVencimiento, lote);
	}

	@Override
	public boolean existePorInsumoFechaLote(Insumo insumo, Date fechaVencimiento, String lote) {
		return vencimientoRepo.existsByInsumoAndFechaAndLote(insumo, fechaVencimiento, lote);
	}

	@Override
	public boolean vencimientoAvailable(Insumo insumo, Date fechaVencimiento, String lote) {
		VencimientoLote vencimiento = buscarPorInsumoFechaLote(insumo, fechaVencimiento, lote);
		
		if (vencimiento.getCantidadDisponible() == 0 ) {
			return false;
		}
		
		return true;
	}

	@Override
	public List<VencimientoLote> buscarPorLlegada(LlegadaCompra llegada) {
		return vencimientoRepo.findByLlegada(llegada);
	}

	@Override
	public void eliminarLista(List<VencimientoLote> vencimientos) {
		vencimientoRepo.deleteAll(vencimientos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<VencimientoLote> vencenEnTreintaDias() {
		List<VencimientoLote> vencimientos = vencimientosDisponibles();
		List<VencimientoLote> vencenEnTreintaDias = new ArrayList<>();
		Date fechaLimite = new Date();
		
		fechaLimite.setMonth(fechaLimite.getMonth() - 1);
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimientoLote.getFecha().equals(fechaLimite) || vencimientoLote.getFecha().after(fechaLimite)) {
				vencenEnTreintaDias.add(vencimientoLote);
			}
		}
		
		return vencenEnTreintaDias;
	}

	@Override
	public List<VencimientoLote> vencimientosDisponibles() {
		List<VencimientoLote> vencimientos = (List<VencimientoLote>) vencimientoRepo.findAll();
		List<VencimientoLote> vencimientosDisponibles = new ArrayList<>();
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimientoLote.getCantidadDisponible() > 0) {
				vencimientosDisponibles.add(vencimientoLote);
			}
		}
		
		return vencimientosDisponibles;
	}

	@Override
	public List<VencimientoLote> vencimientoParaVender(Insumo insumo) {
		List<VencimientoLote> vencimientos = vencimientoRepo.findByInsumoOrderByFechaAsc(insumo);
		List<VencimientoLote> vencimientosDisponibles = new ArrayList<>();
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimientoLote.getCantidadDisponible() > 0) {
				vencimientosDisponibles.add(vencimientoLote);
			}
		}
		
		return vencimientosDisponibles;
	}

	@Override
	public List<VencimientoLote> vencimientosDisponibles(List<VencimientoLote> vencimientos) {
		List<VencimientoLote> vencimientosDisponibles = new ArrayList<>();
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimientoLote.getCantidadDisponible() > 0) {
				vencimientosDisponibles.add(vencimientoLote);
			}
		}
		
		return vencimientosDisponibles;
	}

	@Override
	public List<VencimientoLote> buscarPorInsumoFecha(Insumo insumo, Date fecha) {
		return vencimientoRepo.findByInsumoAndFecha(insumo, fecha);
	}

	@Override
	public List<VencimientoLote> buscarPorInsumoLote(Insumo insumo, String lote) {
		return vencimientoRepo.findByInsumoAndLote(insumo, lote);
	}

	@Override
	public List<VencimientoLote> buscarPorFecha(Date fecha) {
		return vencimientoRepo.findByFecha(fecha);
	}

	@Override
	public List<VencimientoLote> buscarPorLote(String lote) {
		return vencimientoRepo.findByLote(lote);
	}

	@Override
	public List<VencimientoLote> buscarPorFechaLote(Date fecha, String lote) {
		return vencimientoRepo.findByFechaAndLote(fecha, lote);
	}

	@Override
	public List<VencimientoLote> filtrar(VencimientoLote vencimiento) {
		Insumo insumo = null;
		
		if (vencimiento.getInsumo().getIdInsumo() != null) {
			insumo = insumoService.buscarPorId(vencimiento.getInsumo().getIdInsumo());
		}
		
		if (insumo != null && !vencimiento.getLote().isEmpty() && vencimiento.getFecha() != null) {
			List<VencimientoLote> vencimientos = new ArrayList<>();
			vencimientos.add(vencimientoRepo.findByInsumoAndFechaAndLote(insumo, vencimiento.getFecha(), vencimiento.getLote()));
			return vencimientos;
		} else if (insumo != null && vencimiento.getLote().isEmpty() && vencimiento.getFecha() == null) {
			return vencimientoRepo.findByInsumo(insumo);
		} else if (insumo == null && !vencimiento.getLote().isEmpty() && vencimiento.getFecha() == null) {
			return vencimientoRepo.findByLote(vencimiento.getLote());
		} else if (insumo == null && vencimiento.getLote().isEmpty() && vencimiento.getFecha() != null) {
			return vencimientoRepo.findByFecha(vencimiento.getFecha());
		} else if (insumo != null && !vencimiento.getLote().isEmpty() && vencimiento.getFecha() == null) {
			return vencimientoRepo.findByInsumoAndLote(insumo, vencimiento.getLote());
		} else if (insumo != null && vencimiento.getLote().isEmpty() && vencimiento.getFecha() != null) {
			return vencimientoRepo.findByInsumoAndFecha(insumo, vencimiento.getFecha());
		} else if (insumo == null && !vencimiento.getLote().isEmpty() && vencimiento.getFecha() != null) {
			return vencimientoRepo.findByFechaAndLote(vencimiento.getFecha(), vencimiento.getLote());
		}
		
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<VencimientoLote> vencenEnQuinceDias() {
		List<VencimientoLote> vencimientos = vencimientosDisponibles();
		List<VencimientoLote> vencenEnQuinceDias = new ArrayList<>();
		Date fechaLimite = new Date();
		
		fechaLimite.setDate(fechaLimite.getDate() - 15);;
		
		for (VencimientoLote vencimientoLote : vencimientos) {
			if (vencimientoLote.getFecha().equals(fechaLimite) || vencimientoLote.getFecha().after(fechaLimite)) {
				vencenEnQuinceDias.add(vencimientoLote);
			}
		}
		
		return vencenEnQuinceDias;
	}

}
