package com.KangreSystem.models.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.repository.AdminRepository;
import com.KangreSystem.models.service.IAdminService;


@Service
public class AdminServiceImp implements IAdminService {
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Override
	public void guardar(Administrador admin) {
		adminRepository.save(admin);
	}

	@Override
	public void eliminar(Long idAdmin) {
		adminRepository.deleteById(idAdmin);
	}

	@Override
	public List<Administrador> listar() {
		return (List<Administrador>) adminRepository.findAll();
	}

	@Override
	public Administrador buscarPorId(Long idAdmin) {
		return adminRepository.findById(idAdmin).orElse(null);
	}

	@Override
	public Administrador buscarPorUser(User user) {
		return adminRepository.findByUser(user);
	}

	@Override
	public boolean existePorUser(User user) {
		return adminRepository.existsByUser(user);
	}

}
