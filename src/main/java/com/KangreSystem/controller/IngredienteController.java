package com.KangreSystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.KangreSystem.models.entity.Ingrediente;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.repository.IngredienteRepository;
import com.KangreSystem.models.service.IIngredienteService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IProductoService;
import com.KangreSystem.util.IngredientesPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/ingrediente")
@Component("/Views/SI/Inventario/Ingrediente/ingredientes.xlsx")
public class IngredienteController extends AbstractXlsxView{
	
	@Autowired
	private IIngredienteService ingredienteService;
	
	@Autowired
	private IngredienteRepository ingredienteRepo;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IProductoService productoService;
	
	private List<Ingrediente> ingredientes = new ArrayList<>();
	
	private List<Ingrediente> ingredientesAux = new ArrayList<>();
	
	private List<Ingrediente> ingredientesLista;
	
	private List<Ingrediente> ingredientesListaX;
	
	private List<Insumo> insumos;
	
	private List<Insumo> insumosAux = new ArrayList<>();;
	
	private Producto producto;
	
	private Producto productoAux;
	
	private long itemIngre;
	
	private long itemIns;
	
	@GetMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String listar(Model model) {
		this.ingredientesLista = null;
		model.addAttribute("tabla", "d-none");
		model.addAttribute("titulo", "Ingredientes");
		model.addAttribute("ingredientes", this.ingredientesLista);
		model.addAttribute("productos", productoService.findAllByViaProd());
		return "Views/SI/Inventario/Ingrediente/ingredientes";
	}
	
	@GetMapping("/producto/{id}")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String ingredientesProducto(@PathVariable("id") Long idProducto, Model model, RedirectAttributes attr) {
		Producto producto = null;
		List<Ingrediente> ingredientesProd = new ArrayList<>(); 
		
		if (idProducto > 0) {
			producto = productoService.buscarPorId(idProducto);
			
			if (producto == null) {
				attr.addFlashAttribute("error", "El ID del producto que desea consultar no existe!");
				return "redirect:/producto/";
			}
		} else {
			attr.addFlashAttribute("error", "El ID del producto que desea consultar no existe!");
			return "redirect:/producto/";
		}
		
		ingredientesProd = ingredienteService.buscarPorProducto(producto);
		model.addAttribute("ingredientes", ingredientesProd);
		model.addAttribute("producto", producto);
		return "Views/SI/Inventario/Ingrediente/ingredientesProducto";
	}
	
	@PostMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String filtrar(Model model, @Param("producto") Long producto) {
		Producto productoToFilter;
		
		if (producto == null) {
			this.ingredientesListaX = null;
			model.addAttribute("warning", "No se encontro criterio de busqueda!");
			return listar(model);
		}
		
		productoToFilter = productoService.buscarPorId(producto);
		
		this.ingredientesLista = ingredienteService.buscarPorProducto(productoToFilter);
		
		if (this.ingredientesLista.isEmpty()) {
			this.ingredientesListaX = null;
			model.addAttribute("warning", "No se encontraron resultados!");
			return listar(model);
		}
		
		this.ingredientesListaX = this.ingredientesLista;
		
		model.addAttribute("titulo", "Ingredientes");
		model.addAttribute("ingredientes", this.ingredientesLista);
		model.addAttribute("productos", productoService.findAllByViaProd());
		model.addAttribute("tabla", "show");
		return "Views/SI/Inventario/Ingrediente/ingredientes";
	}
	
	@PostMapping("/guardar")
	@Secured("ROLE_ADMIN")
	public String guardar(@Param("idIngredienteMC") Long idIngredienteMC, @Param("cantIngredienteEdit") Float cantIngredienteEdit,
			RedirectAttributes attr) {
		System.out.println("ID INGREDIENTE: "+idIngredienteMC);
		System.out.println("CANTIDAD: "+cantIngredienteEdit);
		Ingrediente ingrediente = ingredienteService.buscarPorId(idIngredienteMC);
		ingrediente.setCantidad(cantIngredienteEdit);
		ingredienteService.guardar(ingrediente);
		attr.addFlashAttribute("success", "Cantidad actualizada correctamente!");
		return "redirect:/ingrediente/editar/"+this.productoAux.getIdProducto();
	}
	
	@GetMapping("/pdf")	
	@Secured("ROLE_ADMIN")
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Ingredientes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		List<Ingrediente> listIngrediente = ingredienteService.listar();
		
		IngredientesPDFExporter exporter = new IngredientesPDFExporter(listIngrediente);
		exporter.export(response);
	}
	
	
	@GetMapping("/limpiar")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String limpiar() {
		this.ingredientesListaX = null;
		return "redirect:/ingrediente/";
	}
	
	@GetMapping("/edit/{idIngrediente}")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<Ingrediente> details(@PathVariable("idIngrediente") Long idIngrediente) {
		try {
			return new ResponseEntity<Ingrediente>(ingredienteService.buscarPorId(idIngrediente), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Ingrediente>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/cantidad-insumo/{nombre}")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<Insumo> details(@PathVariable("nombre") String nombre) {
		try {
			return new ResponseEntity<Insumo>(insumoService.buscarPorNombre(nombre), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Insumo>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/agregar-item")
	@Secured("ROLE_ADMIN")
	public String agregarItem(Model model) {
		this.producto = (Producto) model.getAttribute("producto");
		
		if (this.producto == null) {
			this.insumos = insumoService.listar();
			this.ingredientes = new ArrayList<>();
			return "redirect:/producto/agregar";
		}
		
		if (this.ingredientes.isEmpty()) {
			this.insumos = insumoService.listar();
			itemIns = 1;
			for (Insumo insumo : this.insumos) {
				insumo.setIdInsumo(null);
				insumo.setIdInsumo(itemIns);
				itemIns++;
			}
			model.addAttribute("insumos", this.insumos);
		}
		
		itemIns = 1;
		for (Insumo insumo : this.insumos) {
			insumo.setIdInsumo(null);
			insumo.setIdInsumo(itemIns);
			itemIns++;
		}
		
		Long total = (long) 0;
		for (Ingrediente ingrediente : this.ingredientes) {
			total += (ingrediente.getInsumo().getPrecio() * ingrediente.getCantidad().longValue());
		}
		
		model.addAttribute("ingrediente", new Ingrediente());
		model.addAttribute("titulo", "Agregar ingredientes");
		model.addAttribute("ingredientes", this.ingredientes);
		model.addAttribute("total", total);
		return "Views/SI/Inventario/Ingrediente/agregarIngrediente";
	}
	
	@PostMapping("/agregar-item")
	@Secured("ROLE_ADMIN")
	public String agregarItem(@ModelAttribute Ingrediente ingrediente, @Param("nombreInsumo") String nombreInsumo, @Param("idInsumo") Long idInsumo, RedirectAttributes attr, Model model) {
		Insumo insumo = insumoService.buscarPorNombre(nombreInsumo);
		insumo.setIdInsumo(idInsumo);
		
		itemIngre = ingredientes.size();
		itemIngre++;
		ingrediente.setIdIngrediente(itemIngre);
		ingrediente.setInsumo(insumo);
		ingrediente.setProducto(this.producto);
		
		this.ingredientes.add(ingrediente);
		this.insumos.remove(Integer.parseInt(insumo.getIdInsumo().toString()) - 1);
		
		attr.addFlashAttribute("insumos", this.insumos);
		attr.addFlashAttribute("ingrediente", ingrediente);
		attr.addFlashAttribute("producto", this.producto);
		return "redirect:/ingrediente/agregar-item";
	}
	
	@GetMapping("/quitar-item/{item}/{nombre}")
	@Secured("ROLE_ADMIN")
	public String quitarItem(@PathVariable("item") Long item, @PathVariable("nombre") String nombre, RedirectAttributes attr) {
		Insumo insumo = insumoService.buscarPorNombre(nombre);
		this.ingredientes.remove(Integer.parseInt(item.toString()) - 1);
		item = (long) 1;
		for (Ingrediente ingrediente : ingredientes) {
			ingrediente.setIdIngrediente(item);
			item++;
		}
		this.insumos.add(insumo);
		attr.addFlashAttribute("producto", this.producto);
		attr.addFlashAttribute("insumos", this.insumos);
		return "redirect:/ingrediente/agregar-item";
	}
	
	@GetMapping("/guardar-todo")
	@Secured("ROLE_ADMIN")
	public String guardarTodo(RedirectAttributes attr) {
		
		if (this.ingredientes.isEmpty()) {
			this.ingredientes = new ArrayList<>();
			this.insumos = null;
			attr.addFlashAttribute("error", "No se ha agregado ningun ingrediente!");
			attr.addFlashAttribute("producto", this.producto);
			attr.addFlashAttribute("ingrediente", new Ingrediente());
			return "redirect:/ingrediente/agregar-item";
		}
		
		for (Ingrediente ingrediente : ingredientes) {
			Insumo insumo = insumoService.buscarPorNombre(ingrediente.getInsumo().getNombre());
			ingrediente.setInsumo(insumo);
			
		}
		ingredienteService.guardarTodo(this.ingredientes);
		attr.addFlashAttribute("success", "Ingredientes guardados correctamente!");
		this.insumos = new ArrayList<>();
		this.ingredientes = new ArrayList<>();
		this.producto = new Producto();
		this.itemIngre = 0;
		this.itemIns = 0;
		return "redirect:/producto/";
	}
	
	@GetMapping("/quitar-todo")
	@Secured("ROLE_ADMIN")
	public String quitarTodo(RedirectAttributes attr) {
		this.ingredientes = new ArrayList<>();
		this.insumos = null;
		attr.addFlashAttribute("producto", this.producto);
		attr.addFlashAttribute("ingrediente", new Ingrediente());
		return "redirect:/ingrediente/agregar-item";
	}
	
	@GetMapping("/editar/{idProducto}")
	@Secured("ROLE_ADMIN")
	public String editar(@PathVariable("idProducto") Long idProducto, Model model, RedirectAttributes attr) {
		List<Insumo> insumosUsed = new ArrayList<>();
		this.insumosAux = insumoService.listar();
		
		if (idProducto > 0) {
			this.productoAux = productoService.buscarPorId(idProducto);
			
			if (this.productoAux == null) {
				attr.addFlashAttribute("error", "El id del producto a editar no existe!");
				return "redirect:/producto/";
			}
		} else {
			attr.addFlashAttribute("error", "El id del producto a editar no existe!");
			return "redirect:/producto/";
		}
		
		this.ingredientesAux = ingredienteService.buscarPorProducto(this.productoAux);
		
		Long total = (long) 0;
		for (Ingrediente ingrediente : this.ingredientesAux) {
			total += (ingrediente.getInsumo().getPrecio() * ingrediente.getCantidad().longValue());
			insumosUsed.add(ingrediente.getInsumo());
		}
		System.out.println("TOTAL: "+total);
		this.insumosAux.removeAll(insumosUsed);
		
		long itemInsAux = 1;
		for (Insumo insumo : this.insumosAux) {
			insumo.setIdInsumo(itemInsAux);
			itemInsAux++;
		}
		
		model.addAttribute("titulo", "Editar ingredientes");
		model.addAttribute("ingredientesAux", this.ingredientesAux);
		model.addAttribute("producto", this.productoAux);		
		model.addAttribute("insumos", this.insumosAux);
		model.addAttribute("ingrediente", new Ingrediente());
		model.addAttribute("total", total);
		return "Views/SI/Inventario/Ingrediente/editarIngredientes";
	}
	
	@PostMapping("/agregar-item-edit")
	@Secured("ROLE_ADMIN")
	public String agregarItemEdit(@ModelAttribute Ingrediente ingrediente, @Param("idInsumo") Long idInsumo, 
			@Param("nombreInsumo") String nombreInsumo, RedirectAttributes attr) {
		Insumo insumo = insumoService.buscarPorNombre(nombreInsumo);
		
		ingrediente.setInsumo(insumo);
		ingrediente.setProducto(this.productoAux);
		this.insumosAux.remove(Integer.parseInt(idInsumo.toString()) - 1);
		ingredienteService.guardar(ingrediente);
		attr.addFlashAttribute("success", "Ingrediente agregado correctamente!");
		attr.addFlashAttribute("insumos", this.insumosAux);
		return "redirect:/ingrediente/editar/"+this.productoAux.getIdProducto();
	}
	
	@GetMapping("/quitar-item-edit/{id}")
	@Secured("ROLE_ADMIN")
	public String quitarItemEdit(@PathVariable("id") Long idIngrediente, Model model, RedirectAttributes attr) {
		Ingrediente ingrediente = ingredienteService.buscarPorId(idIngrediente);
		Insumo insumo = ingrediente.getInsumo();
		if (this.ingredientesAux.size() == 1) {
			attr.addFlashAttribute("error", "No se puede eliminar todos los ingredientes del producto!");
			attr.addFlashAttribute("producto", this.productoAux);
			return "redirect:/ingrediente/editar/"+this.productoAux.getIdProducto();
		}
		
		ingredienteService.eliminar(idIngrediente);
		this.insumosAux.add(insumo);
		attr.addFlashAttribute("warning", "Ingrediente eliminado correctamente!");
		attr.addFlashAttribute("producto", this.productoAux);
		attr.addFlashAttribute("insumos", this.insumosAux);
		return "redirect:/ingrediente/editar/"+this.productoAux.getIdProducto();
	}
	
	public void guardarLista(List<Ingrediente> toDelete, List<Ingrediente> toSave){
		for(Ingrediente ingrediente : toSave){
			Insumo insumo = insumoService.buscarPorNombre(ingrediente.getInsumo().getNombre());
			ingrediente.setInsumo(insumo);
		}
		
		ingredienteRepo.deleteAll(toDelete);
		ingredienteService.guardarTodo(toSave);
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"ingredientes.xlsx\"");
		Sheet hoja = workbook.createSheet("Ingredientes");

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		Row filaTitulo = hoja.createRow(0);
		Cell celda = filaTitulo.createCell(0);
		celda.setCellValue("");
		celda.setCellStyle(style);

		Row filaData = hoja.createRow(0);
		String[] columnas = { "ID", "ID Producto", "nombre", "ID Insumo", "Nombre", "Cantidad"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.ingredientesListaX == null) {
			this.ingredientesListaX = ingredienteService.listar();
		}

		for (Ingrediente ingrediente : this.ingredientesListaX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(ingrediente.getIdIngrediente());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(ingrediente.getProducto().getIdProducto());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(ingrediente.getProducto().getNombre());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(ingrediente.getInsumo().getIdInsumo());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(ingrediente.getInsumo().getNombre());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(ingrediente.getCantidad().toString());
			hoja.autoSizeColumn(5);
			numFila++;
		}
		
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/cargar-info")
	public String cargarInfo(Model model) {
		model.addAttribute("titulo", "Carga de ingredientes");
		model.addAttribute("ingrediente", new Ingrediente());
		return "Views/SI/Inventario/Ingrediente/cargarInfoIngrediente";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/cargar-info")
	public String cargarInfo(@ModelAttribute Ingrediente ingrediente, RedirectAttributes attr) {
		boolean isFlag = false;

		try {
			isFlag = ingredienteService.saveDataFromUploadFile(ingrediente.getFile());
		} catch (Exception e) {
			attr.addFlashAttribute("error", e.getMessage());
		}

		if (isFlag) {
			attr.addFlashAttribute("success","Se ha cargado la informacion correctamente a la base de datos!");
		} else {
			attr.addFlashAttribute("error","Oh no!, algo ha ocurrido, por favor revise las recomendaciones.");
			return "redirect:/ingrediente/cargar-info";
		}

		attr.addFlashAttribute("tabla", "show");
		return "redirect:/ingrediente/";
	}
	
	
	
}
