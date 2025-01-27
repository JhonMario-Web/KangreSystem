package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.PagoEfectivo;

public interface IPagoEfectivoService {
	
	//METODOS CRUD
	public List<PagoEfectivo> listar();
	public void guardar(PagoEfectivo pagoEfectivo);
	public void guardarLista(List<PagoEfectivo> pagos);
	public void eliminar(Long idPago);
	
	//METODOS AUXILIARES
	public PagoEfectivo buscarPorId(Long idPago);

}
