package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.CompraRepository;
import com.KangreSystem.models.service.ICompraService;

@Service
public class CompraServImp implements ICompraService{
	
	@Autowired
	private CompraRepository compraRepository;

	@Override
	public List<Compra> listar() {
		return (List<Compra>) compraRepository.findAll();
	}

	@Override
	public void guardar(Compra compra) {
		compraRepository.save(compra);
	}

	@Override
	public void eliminar(Long idCompra) {
		compraRepository.deleteById(idCompra);
	}

	@Override
	public Compra buscarPorId(Long idCompra) {
		return compraRepository.findById(idCompra).orElse(null);
	}

	@Override
	public List<Compra> filtrar(Date fecha, Proveedor proveedor, String estado) {
		
		if (fecha != null && proveedor == null && estado.isEmpty()) {
			return compraRepository.findByFecha(fecha);
		} else if (fecha == null && proveedor != null && estado.isEmpty()) {
			return compraRepository.findByProveedor(proveedor);
		} else if (fecha == null && proveedor == null && !estado.isEmpty()) {
			return compraRepository.findByEstado(estado);
		} else if (fecha != null && proveedor != null && estado.isEmpty()) {
			return compraRepository.findByFechaAndProveedor(fecha, proveedor);
		} else if (fecha != null && proveedor == null && !estado.isEmpty()) {
			return compraRepository.findByFechaAndEstado(fecha, estado);
		} else if (fecha == null && proveedor != null && !estado.isEmpty()) {
			return compraRepository.findByProveedorAndEstado(proveedor, estado);
		} else if (fecha != null && proveedor != null && !estado.isEmpty()) {
			return compraRepository.findByFechaAndProveedorAndEstado(fecha, proveedor, estado);
		}
		
		return null;
	}

	@Override
	public List<Compra> buscarPorFecha(Date fecha) {
		return compraRepository.findByFecha(fecha);
	}

	@Override
	public List<Compra> buscarPorProveedor(Proveedor proveedor) {
		return compraRepository.findByProveedor(proveedor);
	}

	@Override
	public List<Compra> buscarPorEstado(String estado) {
		return compraRepository.findByEstado(estado);
	}

	@Override
	public List<Compra> buscarPorFechaProveedor(Date fecha, Proveedor proveedor) {
		return compraRepository.findByFechaAndProveedor(fecha, proveedor);
	}

	@Override
	public List<Compra> buscarPorFechaEstado(Date fecha, String estado) {
		return compraRepository.findByFechaAndEstado(fecha, estado);
	}

	@Override
	public List<Compra> buscarPorProveedorEstado(Proveedor proveedor, String estado) {
		return compraRepository.findByProveedorAndEstado(proveedor, estado);
	}

	@Override
	public List<Compra> buscarPorFechaProveedorEstado(Date fecha, Proveedor proveedor, String estado) {
		return compraRepository.findByFechaAndProveedorAndEstado(fecha, proveedor, estado);
	}

	@Override
	public Compra buscarPorNumeroCompra(String numeroCompra) {
		return compraRepository.findByNumeroCompra(numeroCompra);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Compra> comprasMesActual() {
		List<Compra> compras = (List<Compra>) compraRepository.findAll();
		List<Compra> comprasMesActual = new ArrayList<>();
		Date fechaActual = new Date();
		
		for (Compra compra : compras) {
			if (compra.getFecha().getMonth() == fechaActual.getMonth()) {
				comprasMesActual.add(compra);
			}
		}
		
		return comprasMesActual;
	}

}
