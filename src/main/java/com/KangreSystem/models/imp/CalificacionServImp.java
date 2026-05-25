package com.KangreSystem.models.imp;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Calificacion;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.repository.CalificacionRepository;
import com.KangreSystem.models.service.ICalificacionService;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IUserService;

@Service
public class CalificacionServImp implements ICalificacionService {
	
	@Autowired
	private CalificacionRepository calificacionRepo;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClienteService clienteService;

	@Override
	public List<Calificacion> listar() {
		return (List<Calificacion>) calificacionRepo.findAll();
	}

	@Override
	public void guardar(Calificacion calificacion) {
		calificacion.setDescripcion(calificacion.getDescripcion().toUpperCase());
		calificacionRepo.save(calificacion);
	}

	@Override
	public void eliminar(Long idCalificacion) {
		calificacionRepo.deleteById(idCalificacion);
	}

	@Override
	public Calificacion buscarPorId(Long idcalificacion) {
		return calificacionRepo.findById(idcalificacion).orElse(null);
	}

	@Override
	public boolean validPermitida(Calificacion calificacion, Principal principal) {
		Cliente cajero = clienteService.buscarPorUser(userService.buscarPorNumeroDoc(principal.getName()));
		Cliente cliente = calificacion.getPedido().getCliente();
		
		if (cliente == null) {
			return false;
		}
		
		if (!cajero.getUser().getUsername().equals(cliente.getUser().getUsername())) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean validIsCalificada(Calificacion calificacion) {
		if (calificacion.getPedido().isCalificado()) {
			return true;
		}
		return false;
	}

	@Override
	public List<Calificacion> filtrar(Calificacion calificacion) {
		if (calificacion.getFecha() != null && !calificacion.getCalificacion().isEmpty()) {
			return calificacionRepo.findByFechaAndCalificacion(calificacion.getFecha(), calificacion.getCalificacion());
		}
		
		if (calificacion.getFecha() != null && calificacion.getCalificacion().isEmpty()) {
			return calificacionRepo.findByFecha(calificacion.getFecha());
		}
		
		if (calificacion.getFecha() == null && !calificacion.getCalificacion().isEmpty()) {
			return calificacionRepo.findByCalificacion(calificacion.getCalificacion());
		}
		return null;
	}

}
