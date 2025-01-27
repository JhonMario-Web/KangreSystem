package com.KangreSystem.models.imp;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.KangreSystem.models.entity.PagoEfectivo;
import com.KangreSystem.models.repository.PagoEfectivoRepository;
import com.KangreSystem.models.service.IPagoEfectivoService;

@Service
public class PagoEfectivoServImp implements IPagoEfectivoService{
	
	@Autowired
	private PagoEfectivoRepository pagoRepository;

	@Override
	public List<PagoEfectivo> listar() {
		return (List<PagoEfectivo>) pagoRepository.findAll();
	}

	@Override
	public void guardar(PagoEfectivo pagoEfectivo) {
		pagoRepository.save(pagoEfectivo);
	}

	@Override
	public void guardarLista(List<PagoEfectivo> pagos) {
		pagoRepository.saveAll(pagos);
	}

	@Override
	public void eliminar(Long idPago) {
		pagoRepository.deleteById(idPago);
	}

	@Override
	public PagoEfectivo buscarPorId(Long idPago) {
		return pagoRepository.findById(idPago).orElse(null);
	}

}
