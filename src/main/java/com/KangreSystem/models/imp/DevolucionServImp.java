package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.DevolucionRepository;
import com.KangreSystem.models.service.IDevolucionServ;

@Service
public class DevolucionServImp implements IDevolucionServ{
	
	@Autowired
	private DevolucionRepository devolucionRepo;

	@Override
	public List<Devolucion> listar() {
		return (List<Devolucion>) devolucionRepo.findAll();
	}

	@Override
	public void guardar(Devolucion devolucion) {
		devolucionRepo.save(devolucion);
	}

	@Override
	public void eliminar(Long idDevolucion) {
		devolucionRepo.deleteById(idDevolucion);
	}

	@Override
	public Devolucion buscarPorId(Long idDevolucion) {
		return devolucionRepo.findById(idDevolucion).orElse(null);
	}

	@Override
	public List<Devolucion> buscarPorFecha(Date fecha) {
		return devolucionRepo.findByFecha(fecha);
	}

	@Override
	public List<Devolucion> buscarPorProveedor(Proveedor proveedor) {
		return devolucionRepo.findByProveedor(proveedor);
	}

	@Override
	public List<Devolucion> buscarPorFechaAndProveedor(Date fecha, Proveedor proveedor) {
		return devolucionRepo.findByFechaAndProveedor(fecha, proveedor);
	}

}
