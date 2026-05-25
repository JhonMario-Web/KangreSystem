package com.KangreSystem.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.entity.Proveedor;

public interface IProveedorService {
	
	//METODOS C R U D
	public List<Proveedor> listar();
	public void guardar(Proveedor proveedor) throws Exception;
	public void eliminar(Long idProveedor);
	public void eliminarTodo();
	public boolean saveDataFromUploadFile(MultipartFile file);
	public boolean existsProveedorByNitAndNombre(String nitProveedor, String nombre) throws Exception;
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Proveedor> buscarPorNitProveedor(String nitProveedor);
	public List<Proveedor> buscarPorCategoria(CategoriaProveedor categoria);
	public List<Proveedor> buscarPorCiudad(String ciudad);
	public List<Proveedor> buscarPorEnabled(boolean enabled);
	public List<Proveedor> buscarPorCategoriaCiudadEnabled(CategoriaProveedor categoria, String ciudad, String estado);
	public List<Proveedor> buscarPorCategoriaCiudad(CategoriaProveedor categoria, String ciudad);
	public List<Proveedor> buscarPorCategoriaEnabled(CategoriaProveedor categoria, boolean enabled);
	public List<Proveedor> buscarPorCiudadEnabled(String ciudad, boolean enabled);
	
	//METODOS PARA LOGICA DEL SISTEMA
	public boolean checkImgIsValid(MultipartFile file);
	public Long totalProveedores();
	public Proveedor buscarPorId(Long idProveedor);
	public Proveedor buscarProveedorPorNit(String nitProveedor);
}
