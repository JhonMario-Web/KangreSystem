package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.User;

public interface IEmpleadoService {
	public List<Empleado> listar();
	public void guardar(Empleado empleado);
	public Empleado buscarPorUser(User user);
	public Empleado buscarPorId(Long idEmpleado);
	public boolean existePorUser(User user);
	public void eliminar(Long idEmpleado);
}
