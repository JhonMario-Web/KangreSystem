package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;

import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PuntoAcumulado;

public interface IPuntoAcumuladoService {
	
	//METODOS CRUD
	public List<PuntoAcumulado> listar();
	public void eliminar(Long idTrxn);
	public void eliminarLista(List<PuntoAcumulado> acumulados);
	public void guardar(PuntoAcumulado trxn);
	public void guardarAcumulados(List<PuntoAcumulado> trxns);
	
	//METODOS AUXILIARES
	public PuntoAcumulado buscarPorId(Long idTrxn);
	public Date calcularFechaVencimiento(Date fechaTrxn);
	public List<PuntoAcumulado> buscarPorCliente(Cliente cliente);
	public int calcularTotalPuntos(Cliente cliente);
	public PuntoAcumulado buscarUltimoVencer(Cliente cliente);
	public List<PuntoAcumulado> buscarTrxnDisponibles(Cliente cliente);
	public List<PuntoAcumulado> buscarPorPedido(Pedido pedido);
	
	//METODOS LOGICOS DEL SISTEMA
	public void acumularPuntos(Pedido pedido);
	public void actualizarKangrePuntos();
	
}
