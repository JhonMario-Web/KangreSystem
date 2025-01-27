package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Proveedor;

public interface IDevolucionServ {
	
	//METODOS CRUD
	public List<Devolucion> listar();
	public void guardar(Devolucion devolucion);
	public void eliminar(Long idDevolucion);

	//METODOS AUXILIARES
	public Devolucion buscarPorId(Long idDevolucion);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Devolucion> buscarPorFecha(Date fecha);
	public List<Devolucion> buscarPorProveedor(Proveedor proveedor);
	public List<Devolucion> buscarPorFechaAndProveedor(Date fecha, Proveedor proveedor);
	
}
