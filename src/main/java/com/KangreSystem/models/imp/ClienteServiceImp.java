package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.ClienteRepository;
import com.KangreSystem.models.service.IClienteService;

@Service
public class ClienteServiceImp implements IClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;

	@Override
	public void guardar(Cliente cliente) {
		clienteRepository.save(cliente);
	}

	@Override
	public List<Cliente> listar() {
		return (List<Cliente>) clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long idCliente) {
		return clienteRepository.findById(idCliente).orElse(null);
	}

	@Override
	public Cliente buscarPorUser(User user) {
		return clienteRepository.findByUser(user);
	}

}
