package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.PuntoTransferido;

public interface IPuntoTransferidoService {
	
	//METODOS CRUD
	public List<PuntoTransferido> listar();
	public void guardar(PuntoTransferido trxn);
	public void eliminar(Long idTrxn);
	
	//METODOS AUXILIARES
	public PuntoTransferido buscarPorId(Long idTrxn);
	
	//METODOS LOGICOS DEL SISTEMA
	public void transferir(PuntoTransferido trxn);
	public boolean validTranferencia(PuntoTransferido trxn);
	public List<PuntoTransferido> buscarPorCliente(Cliente cliente);

}
