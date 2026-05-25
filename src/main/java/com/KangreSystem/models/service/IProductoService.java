package com.KangreSystem.models.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.Producto;

public interface IProductoService {
	
	//METODOS C R U D
	public List<Producto> findAllByViaProd();
	public void guardar(Producto producto);
	public void eliminar(Long idProducto);
	public boolean saveDataFromUploadFile(MultipartFile file);
	
	//METODOS AUXILIARES
	public Producto buscarPorId(Long idProducto);
	public Producto buscarPorNombre(String nombre);
	public boolean existePorId(Long idProducto);
	public boolean existePorNombre(String nombre);
	public boolean checkImgIsValid(MultipartFile file);
	public boolean validProducto(Producto oldProducto, Producto newProducto);
	
	//METODOS PARA LA BUSQUEDA DINAMICA
	public List<Producto> filtrar(CategoriaProducto categoria, String estado);
	public List<Producto> buscarProductosPorNombre(String nombre);
	public List<Producto> buscarPorCategoria(CategoriaProducto categoria);
	public List<Producto> buscarPorEstado(String estado);
	public List<Producto> buscarPorCategoriaEstado(CategoriaProducto categoria, String estado);
	
	//METODOS PARA LA LOGICA DEL SISTEMA
	public Long totalProductos();
	public List<Producto> cincoMasVendidos();
	public List<Producto> cincoMenosVendidos();
	
}
