package com.KangreSystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.KangreSystem.models.entity.CategoriaProducto;
import com.KangreSystem.models.entity.Producto;
import com.KangreSystem.models.service.ICategoriaProductoServ;
import com.KangreSystem.models.service.IProductoService;
import com.KangreSystem.util.ProductoPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/producto")
@Component("/Views/SI/Inventario/Producto/productos.xlsx")
public class ProductoController extends AbstractXlsxView{
	
	@Autowired
	private ICategoriaProductoServ categoriaService;
	
	@Autowired
	private IProductoService productoService;
	
	private List<Producto> productos;
	
	private List<Producto> productosX;
	
	@GetMapping("/catalogo/")
	public String catalogo(Model model) {
		model.addAttribute("productos", productoService.findAllByViaProd());
		return "/Views/SI/Inventario/Producto/catalogoProductos";
	}
	
	@GetMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String listar(Model model) {
		this.productos = productoService.findAllByViaProd();
		model.addAttribute("producto", new Producto());
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("productos", this.productos);
		return "/Views/SI/Inventario/Producto/productos";
	}
	
	@PostMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String filtrar(@Param("nombre") String nombre, @Param("categoria") Long categoria,
			@Param("estado") String estado, Model model) {
		CategoriaProducto categoriaObj = null;
		if (categoria != null) {
			categoriaObj = categoriaService.buscarPorId(categoria);
		} else {
			categoriaObj = null;
		}

		if (!nombre.isEmpty() && categoria != null && !estado.isEmpty()) {
			this.productosX = null;
			model.addAttribute("error", "No se puede filtrar por todos los criterios!");
			return listar(model);
		}
		
		if (!validFilter(nombre, categoria, estado)) {
			this.productosX = null;
			model.addAttribute("error", "No se puede filtrar por los criterios seleccionados!");
			return listar(model);
		}
		
		if (!nombre.isEmpty() && categoria == null && estado.isEmpty()) {
			this.productos = productoService.buscarProductosPorNombre(nombre);
			this.productosX = this.productos;
			
			if (this.productos.isEmpty()) {
				this.productosX = null;
				model.addAttribute("warning", "No se encontraron reseultados!");
				return listar(model);
			}
			
			model.addAttribute("producto", new Producto());
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("productos", this.productos);
			return "/Views/SI/Inventario/Producto/productos";
		}
		
		this.productos = productoService.filtrar(categoriaObj, estado);
		this.productosX = this.productos;
		
		if (this.productos.isEmpty()) {
			this.productosX = null;
			model.addAttribute("warning", "No se encontraron reseultados!");
			return listar(model);
		}
		
		model.addAttribute("producto", new Producto());
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("productos", this.productos);
		return "/Views/SI/Inventario/Producto/productos";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/agregar")
	public String agregar(Model model) {
		model.addAttribute("titulo", "Agregar producto | Menú");
		model.addAttribute("producto", new Producto());
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Inventario/Producto/crearProducto";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/agregar")
	public String agregar(@ModelAttribute Producto producto, RedirectAttributes attribute,
			Model model) {
		Producto productoAux1 = productoService.buscarPorNombre(producto.getNombre());
		boolean sameObj = false;
		if (productoAux1 != null) {
			if (producto.getIdProducto().equals(productoAux1.getIdProducto())) {
				sameObj = true;
			}
		}
		
		if (productoService.existePorNombre(producto.getNombre().toUpperCase()) && !sameObj) {
			model.addAttribute("error", "El producto ya se encuentra guardado!");
			model.addAttribute("titulo", "Agregar producto | Menú");
			model.addAttribute("producto", producto);
			model.addAttribute("categorias", categoriaService.listar());
			return "/Views/SI/Inventario/Producto/crearProducto";
		}
		
		producto.setCantVendida(0);	
		producto.setFoto("producto.png");
		
		productoService.guardar(producto);
		attribute.addFlashAttribute("producto", producto);
		attribute.addFlashAttribute("success", "Producto agregado correctamente!");				
		return "redirect:/ingrediente/agregar-item";
	}
	
	@GetMapping("/limpiar")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String limpiar() {
		this.productosX = null;
		return "redirect:/producto/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/limpiar-form")
	public String limpiarForm() {
		return "redirect:/producto/agregar";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/edit/{id}")
	public String editar(@PathVariable ("id") Long idProducto, Model model, RedirectAttributes attribute) {
		Producto producto = null;
		
		if (idProducto > 0) {
			producto = productoService.buscarPorId(idProducto);
			
			if(producto == null) {
				System.out.println("El id del producto no existe");
				return "redirect:/producto/";
			}
		}	else {
			System.out.println("El id del producto no existe");
			return "redirect:/producto/";
		}
		
		model.addAttribute("titulo", "Editar Producto");
		model.addAttribute("producto", producto);
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Inventario/Producto/editProducto";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/editar")
	public String editar(@ModelAttribute Producto producto, RedirectAttributes attr, Model model) {
		Producto productoAux2 = productoService.buscarPorId(producto.getIdProducto());
		
		if (!productoService.validProducto(productoAux2, producto)) {
			model.addAttribute("error", "El producto ya se encuentra guardado!");
			model.addAttribute("titulo", "Agregar producto | Menú");
			model.addAttribute("producto", producto);
			model.addAttribute("categorias", categoriaService.listar());
			return "/Views/SI/Inventario/Producto/editProducto";
		}
		
		productoService.guardar(producto);
		attr.addFlashAttribute("success", "Cambios guardados correctamente!");
		return "redirect:/producto/";
	}
	
	
	@GetMapping("/details/{id}")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public ResponseEntity<Producto> details(@PathVariable("id") Long idProducto) {
		try {
			return new ResponseEntity<Producto>(productoService.buscarPorId(idProducto), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Producto>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable ("id") Long idProducto, RedirectAttributes attr) {
		
		if (idProducto > 0) {
			Producto producto = productoService.buscarPorId(idProducto);
			
			if(producto == null) {
				System.out.println("El id del producto no existe");
				attr.addFlashAttribute("error", "El ID del producto a eliminar no existe!");
				return "redirect:/producto/";
			}
		}	else {
			System.out.println("El id del producto no existe");
			attr.addFlashAttribute("error", "El ID del producto a eliminar no existe!");
			return "redirect:/producto/";
		}
		
		productoService.eliminar(idProducto);
		attr.addFlashAttribute("warning", "Producto eliminado correctamente!");
		
		return "redirect:/producto/";
	}

	public boolean validFilter(String nombre, Long categoria, String estado) {
		
		if (!nombre.isEmpty() && (categoria != null || !estado.isEmpty())) {
			return false;
		}
		
		return true;
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/cargar-info")
	public String cargarInfo(Model model) {
		model.addAttribute("titulo", "Carga de productos");
		model.addAttribute("producto", new Producto());		
		return "/Views/SI/Inventario/Producto/cargarInfoProducto";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/cargar-info")
	public String cargarArchivo(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
		boolean isFlag = false;

		try {
			isFlag = productoService.saveDataFromUploadFile(producto.getFile());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		if (isFlag) {
			redirectAttributes.addFlashAttribute("success","Se ha cargado la informacion correctamente a la base de datos!");
		} else {
			redirectAttributes.addFlashAttribute("error","Oh no!, algo ha ocurrido, por favor revise las recomendaciones.");
			return "redirect:/producto/cargar-info";
		}

		return "redirect:/producto/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Productos_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (productosX == null) {
			ProductoPDFExporter exporter = new ProductoPDFExporter(productoService.findAllByViaProd());
			exporter.export(response);
		}
		else {
			ProductoPDFExporter exporter = new ProductoPDFExporter(productosX);
			exporter.export(response);
		}
		
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"productos.xlsx\"");
		Sheet hoja = workbook.createSheet("Productos");

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
		String[] columnas = { "ID", "Nombre", "Detalle", "Precio", "Categoria", "Estado"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (productosX == null) {
			productosX = productoService.findAllByViaProd();
		}

		for (Producto producto : productosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(producto.getIdProducto());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(producto.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(producto.getDetalle());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(producto.getPrecio());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(producto.getCategoria().getCategoria());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(producto.getEstado());
			hoja.autoSizeColumn(5);
			numFila++;
		}
		
	}
	
	@GetMapping("/top/")
	public String topProductos(Model model) {
		model.addAttribute("masVendidos", productoService.cincoMasVendidos());
		model.addAttribute("menosVendidos", productoService.cincoMenosVendidos());
		return "/Views/SI/Inventario/Producto/topProductos";
	}
	
}
