package com.KangreSystem.models.service;

import java.util.List;

import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.User;

public interface IAdminService {
	public void guardar(Administrador admin);
	public void eliminar(Long idAdmin);
	public List<Administrador> listar();
	public Administrador buscarPorId(Long idAdmin);
	public Administrador buscarPorUser(User user);
	public boolean existePorUser(User user);
}
