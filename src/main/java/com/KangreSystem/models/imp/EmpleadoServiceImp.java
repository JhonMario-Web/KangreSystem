package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.EmpleadoRepository;
import com.KangreSystem.models.repository.RolRepository;
import com.KangreSystem.models.service.IEmpleadoService;

@Service
public class EmpleadoServiceImp implements IEmpleadoService{
	
	@Autowired
	private EmpleadoRepository empleadoRepository;
	
	@Autowired
	private RolRepository rolRepository;

	@Override
	public List<Empleado> listar() {
		return (List<Empleado>) empleadoRepository.findAll();
	}

	@Override
	public void guardar(Empleado empleado) {
		empleadoRepository.save(empleado);
	}

	@Override
	public Empleado buscarPorUser(User user) {
		return empleadoRepository.findByUser(user);
	}
	
	protected Empleado userToEmpleado(User user) {
		Empleado empleado = new Empleado();
		empleado.setUser(user);
		return empleado;
	}

	@Override
	public Empleado buscarPorId(Long idEmpleado) {
		return empleadoRepository.findById(idEmpleado).orElse(null);
	}

	@Override
	public void eliminar(Long idEmpleado) {
		Empleado empleado = empleadoRepository.findById(idEmpleado).orElse(null);
		Rol rolUser = rolRepository.findByUserAndRol(empleado.getUser(), "ROLE_USER");
		List<Rol> roles = rolRepository.findByUser(empleado.getUser());
		
		roles.remove(rolUser);
		
		empleadoRepository.deleteById(idEmpleado);
		rolRepository.deleteAll(roles);
	}

	@Override
	public boolean existePorUser(User user) {
		return empleadoRepository.existsByUser(user);
	}

}
