package com.KangreSystem.models.imp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.Mesa;
import com.KangreSystem.models.repository.MesaRepository;
import com.KangreSystem.models.service.IMesaService;

@Service
public class MesaServImp implements IMesaService{
	
	@Autowired
	private MesaRepository mesaRepo;

	@Override
	public List<Mesa> listar() {
		return (List<Mesa>) mesaRepo.findAll();
	}

	@Override
	public void guardar(Mesa mesa) {
		mesaRepo.save(mesa);
	}

	@Override
	public void eliminar(Long idMesa) {
		mesaRepo.deleteById(idMesa);
	}

	@Override
	public Mesa buscarPorId(Long idMesa) {
		return mesaRepo.findById(idMesa).orElse(null);
	}

	@Override
	public Mesa buscarPorNumeroMesa(String numeroMesa) {
		return mesaRepo.findByNumeroMesa(numeroMesa);
	}

	@Override
	public List<Mesa> mesasDisponibles() {
		List<Mesa> mesas = (List<Mesa>) mesaRepo.findAll();
		List<Mesa> mesasDisponibles = new ArrayList<>();
		
		for (Mesa mesa : mesas) {
			if (mesa.getEstado().equals("DESOCUPADA")) {
				mesasDisponibles.add(mesa);
			}
		}
		
		return mesasDisponibles;
	}

}
