package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;

import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.PagoEfectivo;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.PedidoMesa;

public interface IPedidoService {
	
	//METODOS CRUD
	public List<Pedido> listar();
	public void guardar(Pedido pedido);
	public void eliminar(Long idPedido);
	
	//METODOS AUXILIRES
	public Pedido buscarPorId(Long idPedido);
	public Pedido buscarPorNumeroOrden(String numeroOrden);
	public boolean filterIsValid(Pedido pedido);
	public List<Pedido> buscarPorEmpleado(Empleado empleado);
	public List<Pedido> buscarPorEstado(String estado);
	public List<Pedido> buscarPorFecha(Date fecha);
	public List<Pedido> buscarPorEstadoFecha(String estado, Date fecha);
	
	//METODOS LOGICOS DEL SISTEMA
	public Long calcularSubtotalPedido(List<DetallePedido> detalles);
	public Long calcularIva(Long subtotal);
	public Integer calcularKangrepuntos(Long subtotal);
	public Long calcularTotalPedido(Long subtotal);
	public void tomarOrden(Pedido pedido, List<DetallePedido> detalles, List<PedidoMesa> mesasAgregadas);
	public PagoEfectivo pagarPedidoEfectivo(Pedido pedido, Long efectivo);
	public void ordenSolicitadaVencida();
	
}
