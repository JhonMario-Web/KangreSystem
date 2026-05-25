package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.User;

public interface IClienteService {
	public void guardar(Cliente cliente);
	public List<Cliente> listar();
	public Cliente buscarPorId(Long idCliente);
	public Cliente buscarPorUser(User user);
}
