package com.KangreSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.service.ICategoriaInsumoService;
import com.KangreSystem.models.service.ICategoriaProductoServ;
import com.KangreSystem.models.service.ICategoriaProveedorService;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/gestor-categorias")
public class GestorCategoriaController {
	
	@Autowired
	private ICategoriaProveedorService catProvService;
	
	@Autowired
	private ICategoriaProductoServ catProdService;
	
	@Autowired
	private ICategoriaInsumoService catInsService;
	
	@GetMapping("/")
	public String gestor(Model model) {
		model.addAttribute("titulo", "Gestor de categorias");
		return "Views/SI/Categorias/gestorCategorias";
	}
	
	@GetMapping("/proveedor/")
	public String categoriasProveedor(Model model) {
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catProveedor", new CategoriaProveedor());
		model.addAttribute("catProveedores", catProvService.listar());
		return "Views/SI/Categorias/Proveedor/categoriasProveedor";
	}
	
	@PostMapping("/proveedor/agregar")
	public String agregarCatProv(@ModelAttribute CategoriaProveedor categoria, RedirectAttributes attr) {
		CategoriaProveedor categoriaAux = null;
		String oldCategoria = "";
		String newCategoria = categoria.getCategoria().toUpperCase();;
		boolean existe = catProvService.existePorCategoria(categoria.getCategoria());
		
		if (categoria.getIdCategoria() != null) {
			categoriaAux = catProvService.buscarPorId(categoria.getIdCategoria());
			oldCategoria = categoriaAux.getCategoria().toUpperCase();
		}
		
		if(existe && !oldCategoria.equals(newCategoria)) {
			attr.addFlashAttribute("error", "El nombre de la categoria ya existe!");
			return "redirect:/gestor-categorias/proveedor/";
		}
		
		catProvService.guardar(categoria);
		attr.addFlashAttribute("success", "Categoria guardada correctamente!");
		return "redirect:/gestor-categorias/proveedor/";
	}
	
	@GetMapping("/proveedor/edit/{id}")
	public String editCatProv(@PathVariable("id") Long idCategoria, Model model) {
		CategoriaProveedor categoria = catProvService.buscarPorId(idCategoria);
		model.addAttribute("catProveedor", categoria);
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catProveedores", catProvService.listar());
		model.addAttribute("id", "editar ID: "+ categoria.getIdCategoria());		
		return "Views/SI/Categorias/Proveedor/categoriasProveedor";
	}
	
	@GetMapping("/proveedor/delete/{id}")
	public String deteleCatProv(@PathVariable("id") Long idCategoria, RedirectAttributes attr) {
		catProvService.eliminar(idCategoria);
		attr.addFlashAttribute("warning", "Categoria eliminada correctamente!");
		return "redirect:/gestor-categorias/proveedor/";
	}
	
	@GetMapping("/producto/")
	public String categoriasProducto(Model model) {
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catProducto", new CategoriaProducto());
		model.addAttribute("catProductos", catProdService.listar());
		return "Views/SI/Categorias/Producto/categoriasProducto";
	}
	
	@PostMapping("/producto/agregar")
	public String agregarCatProd(@ModelAttribute CategoriaProducto categoria, RedirectAttributes attr) {
		CategoriaProducto categoriaAux = null;
		String oldCategoria = "";
		String newCategoria = categoria.getCategoria().toUpperCase();;
		boolean existe = catProdService.existePorCategoria(categoria.getCategoria());
		
		if (categoria.getIdCategoria() != null) {
			categoriaAux = catProdService.buscarPorId(categoria.getIdCategoria());
			oldCategoria = categoriaAux.getCategoria().toUpperCase();
		}
		
		if(existe && !oldCategoria.equals(newCategoria)) {
			attr.addFlashAttribute("error", "El nombre de la categoria ya existe!");
			return "redirect:/gestor-categorias/producto/";
		}
		
		catProdService.guardar(categoria);
		attr.addFlashAttribute("success", "Categoria guardada correctamente!");
		return "redirect:/gestor-categorias/producto/";
	}
	
	@GetMapping("/producto/edit/{id}")
	public String editCatProd(@PathVariable("id") Long idCategoria, Model model) {
		CategoriaProducto categoria = catProdService.buscarPorId(idCategoria);
		model.addAttribute("catProducto", categoria);
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catProductos", catProdService.listar());
		model.addAttribute("id", "editar ID: "+ categoria.getIdCategoria());		
		return "Views/SI/Categorias/Producto/categoriasProducto";
	}
	
	@GetMapping("/producto/delete/{id}")
	public String deteleCatProd(@PathVariable("id") Long idCategoria, RedirectAttributes attr) {
		catProdService.eliminar(idCategoria);
		attr.addFlashAttribute("warning", "Categoria eliminada correctamente!");
		return "redirect:/gestor-categorias/producto/";
	}
	
	@GetMapping("/insumo/")
	public String categoriasInsumo(Model model) {
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catInsumo", new CategoriaInsumo());
		model.addAttribute("catInsumos", catInsService.listar());
		return "Views/SI/Categorias/Insumo/categoriasInsumo";
	}
	
	@PostMapping("/insumo/agregar")
	public String agregarCatIns(@ModelAttribute CategoriaInsumo categoria, RedirectAttributes attr) {
		CategoriaInsumo categoriaAux = null;
		String oldCategoria = "";
		String newCategoria = categoria.getCategoria().toUpperCase();;
		boolean existe = catInsService.existePorCategoria(categoria.getCategoria());
		
		if (categoria.getIdCategoria() != null) {
			categoriaAux = catInsService.buscarPorId(categoria.getIdCategoria());
			oldCategoria = categoriaAux.getCategoria().toUpperCase();
		}
		
		if(existe && !oldCategoria.equals(newCategoria)) {
			attr.addFlashAttribute("error", "El nombre de la categoria ya existe!");
			return "redirect:/gestor-categorias/insumo/";
		}
		
		catInsService.guardar(categoria);
		attr.addFlashAttribute("success", "Categoria guardada correctamente!");
		return "redirect:/gestor-categorias/insumo/";
	}
	
	@GetMapping("/insumo/edit/{id}")
	public String editCatIns(@PathVariable("id") Long idCategoria, Model model) {
		CategoriaInsumo categoria = catInsService.buscarPorId(idCategoria);
		model.addAttribute("catInsumo", categoria);
		model.addAttribute("titulo", "Gestor de categorias");
		model.addAttribute("catInsumos", catInsService.listar());
		model.addAttribute("id", "editar ID: "+ categoria.getIdCategoria());		
		return "Views/SI/Categorias/Insumo/categoriasInsumo";
	}
	
	@GetMapping("/insumo/delete/{id}")
	public String deteleCatIns(@PathVariable("id") Long idCategoria, RedirectAttributes attr) {
		catProdService.eliminar(idCategoria);
		attr.addFlashAttribute("warning", "Categoria eliminada correctamente!");
		return "redirect:/gestor-categorias/insumo/";
	}
	
}
