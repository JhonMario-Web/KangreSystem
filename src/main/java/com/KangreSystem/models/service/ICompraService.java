package com.KangreSystem.models.service;

import java.util.Date;
import java.util.List;
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.Proveedor;

public interface ICompraService {
	
	//METODOS CRUD
	public List<Compra> listar();
	public void guardar(Compra compra);
	public void eliminar(Long idCompra);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Compra> filtrar(Date fecha, Proveedor proveedor, String estado);
	public List<Compra> buscarPorFecha(Date fecha);
	public List<Compra> buscarPorProveedor(Proveedor proveedor);
	public List<Compra> buscarPorEstado(String estado);
	public List<Compra> buscarPorFechaProveedor(Date fecha, Proveedor proveedor);
	public List<Compra> buscarPorFechaEstado(Date fecha, String estado);
	public List<Compra> buscarPorProveedorEstado(Proveedor proveedor, String estado);
	public List<Compra> buscarPorFechaProveedorEstado(Date fecha, Proveedor proveedor, String estado);
	
	//METODOS AUXILIARES
	public Compra buscarPorId(Long idCompra);
	public Compra buscarPorNumeroCompra(String numeroCompra);
	public List<Compra> comprasMesActual();

}
