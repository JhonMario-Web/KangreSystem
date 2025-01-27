package com.KangreSystem.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;

public interface IInsumoService {
	
	//METODOS C R U D
	public List<Insumo> listar();
	public void guardar(Insumo insumo);
	public void eliminar(Long idInsumo);
	public boolean saveDataFromUploadFile(MultipartFile file);
	
	//METODOS AUXILIARES
	public Insumo buscarPorId(Long idInsumo);
	public Insumo buscarPorNombre(String nombre);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Insumo> buscarPorNombreList(String nombre);
	public List<Insumo> buscarPorCategoria(CategoriaInsumo categoria);
	public List<Insumo> buscarPorProveedor(Proveedor proveedor);
	public List<Insumo> buscarPorEstado(String estado);
	public List<Insumo> buscarPorCategoriaProveedorEstado(CategoriaInsumo categoria, Proveedor proveedor, String estado);
	public List<Insumo> buscarPorCategoriaProveedor(CategoriaInsumo categoria, Proveedor proveedor);
	public List<Insumo> buscarPorCategoriaEstado(CategoriaInsumo categoria, String estado);
	public List<Insumo> buscarPorProveedorEstado(Proveedor proveedor, String estado);
	public List<Insumo> buscarPorProveedorDevolucion(Proveedor proveedor, boolean devolucion);
	
	//METODOS LOGICOS DEL SISTEMA
	public Long totalInsumos();
	public List<Insumo> insumosConExistencia();
	
}
