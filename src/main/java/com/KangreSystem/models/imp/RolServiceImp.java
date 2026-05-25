package com.KangreSystem.models.imp;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.AdminRepository;
import com.KangreSystem.models.repository.EmpleadoRepository;
import com.KangreSystem.models.repository.RolRepository;
import com.KangreSystem.models.service.IRolService;

@Service
public class RolServiceImp implements IRolService{
	
	@Autowired
	public RolRepository rolRepository;
	
	@Autowired
	public EmpleadoRepository empleadoRepository;
	
	@Autowired
	public AdminRepository adminRepository;

	@Override
	public List<Rol> listar() {
		return (List<Rol>) rolRepository.findAll();
	}
	
	@Override
	public void guardar(Rol rol) {
		rolRepository.save(rol);
	}
	
	@Override
	public void guardar(Rol rol, String sueldo) {
		Empleado empleado = new Empleado();
		Administrador admin = new Administrador(); 
		
		admin.setFechaRegistro(new Date());
		empleado.setUser(rol.getUser());
		admin.setUser(rol.getUser());
		empleado.setSueldo(sueldo);
		empleado.setFechaRegistro(new Date());
		
		if (empleadoRepository.existsByUser(rol.getUser())) {
			empleado = empleadoRepository.findByUser(rol.getUser());
			empleado.setSueldo(sueldo);
			if (!adminRepository.existsByUser(rol.getUser()) && rol.getRol().equals("ROLE_ADMIN")) {
				adminRepository.save(admin);
			}
		}
		
		if(!empleadoRepository.existsByUser(rol.getUser())) {
			
			empleadoRepository.save(empleado);
			empleado.setSueldo(sueldo);
			if (!adminRepository.existsByUser(rol.getUser()) && rol.getRol().equals("ROLE_ADMIN")) {
				adminRepository.save(admin);
			}
		} 
		
		rol.setFechaRegistro(new Date());
		rol.setHoraRegistro(new Date());
		rolRepository.save(rol);
	}

	@Override
	public void eliminar(Long idRol) {
		Rol rol = rolRepository.findById(idRol).orElse(null);
		Rol rolUser = rolRepository.findByUserAndRol(rol.getUser(), "ROLE_USER");
		List<Rol> roles =  rolRepository.findByUser(rol.getUser());
		Administrador admin = adminRepository.findByUser(rol.getUser());
		Empleado empleado = empleadoRepository.findByUser(rol.getUser());
		
		if (roles.size() == 2 && roles.contains(rolUser)) {
			empleadoRepository.delete(empleado);
		}
		
		rolRepository.deleteById(idRol);
		
		if (rol.getRol().equals("ROLE_ADMIN")) {
			roles.remove(rolUser);
			rolRepository.deleteAll(roles);
			adminRepository.delete(admin);
			empleadoRepository.delete(empleado);
		}
	}

	@Override
	public Rol buscarPorId(Long idRol) {
		return rolRepository.findById(idRol).orElse(null);
	}

	@Override
	public List<Rol> buscarPorRol(String rol) {
		return rolRepository.findByRol(rol);
	}

	@Override
	public boolean existePorUser(User user) {
		return rolRepository.existsByUser(user);
	}

	@Override
	public boolean existePorUserRol(User user, String numeroDoc) {
		return rolRepository.existsByUserAndRol(user, numeroDoc);
	}

	@Override
	public List<Rol> buscarRolesPorUser(User user) {
		return rolRepository.findByUser(user);
	}

	@Override
	public Rol buscarPorUserAndRol(User user, String rol) {
		return rolRepository.findByUserAndRol(user, rol);
	}

	@Override
	public Rol buscarRolPorUserOrRol(User user, String rol) {
		return rolRepository.findByUserOrRol(user, rol);
	}

	@Override
	public long contarPorRol(String rol) {
		return rolRepository.countByRol(rol);
	}

}
