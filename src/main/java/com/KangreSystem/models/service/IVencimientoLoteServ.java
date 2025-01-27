package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.entity.VencimientoLote;

public interface IVencimientoLoteServ {
	
	//METODOS CRUD
	public List<VencimientoLote> listar();
	public void guardar(VencimientoLote vencimiento);
	public void guardarLista(List<VencimientoLote> vencimientos);
	public void eliminar(Long idVencimiento);
	public void eliminarLista(List<VencimientoLote> vencimientos);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<VencimientoLote> filtrar(VencimientoLote vencimiento); 
	public List<VencimientoLote> buscarPorInsumo(Insumo insumo);
	public List<VencimientoLote> buscarPorInsumoFecha(Insumo insumo, Date fecha);
	public List<VencimientoLote> buscarPorInsumoLote(Insumo insumo, String Lote);
	public List<VencimientoLote> buscarPorFecha(Date fecha);
	public List<VencimientoLote> buscarPorLote(String lote);
	public List<VencimientoLote> buscarPorFechaLote(Date fecha, String lote);
	
	//METODOS AUXILIARES
	public VencimientoLote buscarPorId(Long idVencimiento);
	public List<VencimientoLote> buscarPorLlegada(LlegadaCompra llegada);
	public VencimientoLote buscarPorInsumoFechaLote(Insumo insumo, Date fechaVencimiento, String lote);
	public boolean existePorInsumoFechaLote(Insumo insumo, Date fechaVencimiento, String lote);
	
	//METODOS LOGICOS DEL SISTEMA
	public boolean vencimientoAvailable(Insumo insumo, Date fechaVencimiento, String lote);
	public List<VencimientoLote> vencenEnQuinceDias();
	public List<VencimientoLote> vencenEnTreintaDias();
	public List<VencimientoLote> vencimientosDisponibles();
	public List<VencimientoLote> vencimientoParaVender(Insumo insumo);
	public List<VencimientoLote> vencimientosDisponibles(List<VencimientoLote> vencimientos);

}
