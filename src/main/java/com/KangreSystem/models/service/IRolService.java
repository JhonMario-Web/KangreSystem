package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;

public interface IRolService {
	public List<Rol> listar();
	public void guardar(Rol rol);
	public void eliminar(Long idRol);
	public Rol buscarPorId(Long idRol);
	public List<Rol> buscarPorRol(String rol);
	public List<Rol> buscarRolesPorUser(User user);
	public Rol buscarRolPorUserOrRol(User user, String rol);
	public Rol buscarPorUserAndRol(User user, String rol);
	public boolean existePorUser(User user);
	public boolean existePorUserRol(User user, String rol);
	public void guardar(Rol rol, String sueldo);
	public long contarPorRol(String rol);
	
}
