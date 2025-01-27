package com.KangreSystem.models.service;

import java.util.List;
import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleAveria;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.DetalleLlegadaCompra;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.entity.VencimientoLote;

public interface IInventarioService {
	
	//METODOS LOGICOS DEL SISTEMA
	public void actualizarInventario();
	public void actualizarStockActualInsumo();
	public void actualizarEntradasSalidas();
	public void actualizarEntradasSalidasInsumo(Insumo insumo);
	public void comprar(Compra compra, List<DetalleCompra> detalles);
	public void recibirCompra(LlegadaCompra llegada, List<DetalleLlegadaCompra> detalles, List<VencimientoLote> vencimientos);
	public void devolver(Devolucion devolucion, List<DetalleDevolucion> detalles);
	public void averiar(Averia averia, List<DetalleAveria> detalles);
	public void vender(Pedido pedido);
	public void cancelarLlegada(LlegadaCompra llegada);
	public void cancelarAveria(Averia averia);
	public boolean cancelarVenta(Pedido pedido);

}
