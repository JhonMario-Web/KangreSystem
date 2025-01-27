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
import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.InsumoRepository;
import com.KangreSystem.models.service.ICategoriaInsumoService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.util.InsumoPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/insumo")
@Component("/Views/SI/Inventario/Insumo/insumos.xlsx")
public class InsumoController extends AbstractXlsxView{
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private InsumoRepository insumoRepository;
	
	@Autowired
	private ICategoriaInsumoService categoriaService;
	
	@Autowired
	private IProveedorService proveedorService;

	private List<Insumo> insumos;
	
	private List<Insumo> insumosX;
	
	@Secured({"ROLE_ADMIN", "ROLE_COCINERO", "ROLE_CAJERO"})
	@GetMapping("/")
	public String listar(Model model) {
		insumos = insumoService.listar();
		model.addAttribute("insumos", insumos);
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("proveedores", proveedorService.listar());
		model.addAttribute("titulo", "Insumos");
		return "Views/SI/Inventario/Insumo/insumos";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/agregar")
	public String guardar(Model model) {
		model.addAttribute("insumo", new Insumo());
		model.addAttribute("titulo", "Agregar nuevo insumo");
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Inventario/Insumo/formCrear";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/agregar")
	public String guardar(@ModelAttribute Insumo insumo, RedirectAttributes attr, Model model) {
		Insumo insumoAux = null;
		insumoAux = insumoService.buscarPorNombre(insumo.getNombre());
		
		if (insumo.getStockMin() > insumo.getStockMax() || insumo.getStockMin() == insumo.getStockMax()) {
			model.addAttribute("error", "El stock minimo debe ser menor al stock maximo!");
			model.addAttribute("titulo", "Agregar nuevo insumo");
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Inventario/Insumo/formCrear";
		}
		
		if (insumoAux != null) {
			model.addAttribute("error", "El insumo a agregar ya existe!");
			model.addAttribute("titulo", "Agregar nuevo insumo");
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Inventario/Insumo/formCrear";
		}
		
		insumoService.guardar(insumo);
		attr.addFlashAttribute("success", "Insumo guardado correctamente!");
		return "redirect:/insumo/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/edit/{id}")
	public String editar(@PathVariable("id") Long idInsumo, Model model, RedirectAttributes attr) {
		Insumo insumo = null;
		
		if (idInsumo > 0) {
			insumo = insumoService.buscarPorId(idInsumo);
			
			if(insumo == null) {
				System.out.println("El id del insumo no existe");
				attr.addFlashAttribute("error", "El id del insumoa editar no existe!");
				return "redirect:/insumo/";
			}
		}	else {
			System.out.println("El id del insumo no existe");
			attr.addFlashAttribute("error", "El id del insumo a editar no existe!");
			return "redirect:/insumo/";
		}
		
		model.addAttribute("titulo", "Editar Insumo");
		model.addAttribute("insumo", insumo);
		model.addAttribute("nombre", insumo.getNombre());
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Inventario/Insumo/formEdit";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/edit")
	public String editar(@ModelAttribute Insumo insumo, RedirectAttributes attr, Model model) {
		Insumo insumoAux = null;
		String nameFound = null;
		insumoAux = insumoService.buscarPorId(insumo.getIdInsumo());
		String nameToSave = insumo.getNombre().toUpperCase();
		
		if (insumoAux != null) {
			nameFound = insumoAux.getNombre().toUpperCase();
		}
				
		if (!nameFound.equals(nameToSave)) {
			System.out.println("Nombres no son iguales");
			if (!insumoRepository.existsByNombre(insumo.getNombre())) {
				insumoService.guardar(insumo);
				attr.addFlashAttribute("success", "Insumo guardado correctamente!");
				return "redirect:/insumo/";
			}
			System.out.println("Existe y nombres diferentes");
			model.addAttribute("error", "El insumo a guardar ya existe!");
			return editar(insumo.getIdInsumo(), model, attr);
		}
		
		insumo.setNombre(insumo.getNombre());
		insumoRepository.save(insumo);
		attr.addFlashAttribute("success", "Insumo guardado correctamente!");
		return "redirect:/insumo/";	
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") Long idInsumo, RedirectAttributes attr) {
		Insumo insumo = null;
		
		if (idInsumo > 0) {
			insumo = insumoService.buscarPorId(idInsumo);
			
			if(insumo == null) {
				System.out.println("El id del insumo no existe");
				attr.addFlashAttribute("error", "El id del insumo a eliminar no existe!");
				return "redirect:/insumo/";
			}
		}	else {
			System.out.println("El id del insumo no existe");
			attr.addFlashAttribute("error", "El id del insumoa eliminar no existe!");
			return "redirect:/insumo/";
		}
		
		insumoService.eliminar(idInsumo);
		attr.addFlashAttribute("warning", "Insumo eliminado con exito!");
		return "redirect:/insumo/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Insumos_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (insumosX == null) {
			InsumoPDFExporter exporter = new InsumoPDFExporter(insumoService.listar());
			exporter.export(response);
		}
		else {
			InsumoPDFExporter exporter = new InsumoPDFExporter(insumosX);
			exporter.export(response);
		}
		
	}

	
	@GetMapping("/limpiar")
	@Secured({"ROLE_ADMIN", "ROLE_COCINERO", "ROLE_CAJERO"})
	public String limpiar(Model model) {
		insumosX =  null;
		return "redirect:/insumo/";
	}
	
	@PostMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_COCINERO", "ROLE_CAJERO"})
	public String filtrar(@Param("nombre") String nombre, @Param("categoria") Long categoria,
			@Param("proveedor") Long proveedor, @Param("estado") String estado, Model model) {

		CategoriaInsumo categoriaAux = null;
		Proveedor proveedorAux = null; 
		
		if (categoria != null) {
			categoriaAux = categoriaService.buscarPorId(categoria);
		} else {
			categoriaAux = null;
		}
		
		if (proveedor != null) {
			proveedorAux = proveedorService.buscarPorId(proveedor);
		} else {
			proveedorAux = null;
		}

		if (!validFilter(nombre, categoria, proveedor, estado)) {
			insumos = insumoService.listar();
			insumosX = null;
			insumosX = insumos;
			
			if (insumos.isEmpty()) {
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
			
			System.out.println(insumos);
			model.addAttribute("warning", "No se puede filtrar los criterios seleccionados!");
			model.addAttribute("insumos", insumos);
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("proveedores", proveedorService.listar());
			model.addAttribute("titulo", "Insumos");	
			return "Views/SI/Inventario/Insumo/insumos";
		}

		if (nombre.isEmpty() && categoria == null && proveedor == null && estado.isEmpty()) {
			insumos = insumoService.listar();
			insumosX = null;
			insumosX = insumos;
			
			if (insumos.isEmpty()) {
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
			
			System.out.println(insumos);
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			model.addAttribute("insumos", insumos);
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("proveedores", proveedorService.listar());
			model.addAttribute("titulo", "Insumos");	
			return "Views/SI/Inventario/Insumo/insumos";
		}

		if (!nombre.isEmpty() && categoria == null && proveedor == null && estado.isEmpty()) {
			insumos = insumoService.buscarPorNombreList(nombre);
			insumosX = null;
			insumosX = insumos;
			System.out.println(insumos);
			
			if (insumos.isEmpty()) {
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
			
			model.addAttribute("insumos", insumos);
			model.addAttribute("categorias", categoriaService.listar());
			model.addAttribute("proveedores", proveedorService.listar());
			model.addAttribute("titulo", "Insumos");	
			return "Views/SI/Inventario/Insumo/insumos";
		}

		insumos = insumoService.buscarPorCategoriaProveedorEstado(categoriaAux, proveedorAux, estado);
		insumosX = null;
		insumosX = insumos;
		
		if (insumos.isEmpty()) {
			model.addAttribute("warning", "No se encontraron resultados!");
			return listar(model);
		}

		System.out.println(insumos);
		model.addAttribute("insumos", insumos);
		model.addAttribute("categorias", categoriaService.listar());
		model.addAttribute("proveedores", proveedorService.listar());
		model.addAttribute("titulo", "Insumos");	
		return "Views/SI/Inventario/Insumo/insumos";
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_COCINERO", "ROLE_CAJERO"})
	@GetMapping("/details/{idInsumo}")
	public ResponseEntity<Insumo> details(@PathVariable("idInsumo") Long idInsumo) {
		try {
			return new ResponseEntity<Insumo>(insumoService.buscarPorId(idInsumo), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Insumo>(HttpStatus.BAD_REQUEST);
		}
	}
	
	public boolean validFilter(String nombre, Long categoria, Long proveedor, String estado) {
		if ((!nombre.isEmpty() && categoria != null && proveedor != null && !estado.isEmpty())
				|| (!nombre.isEmpty() && categoria != null && proveedor == null && estado.isEmpty())
				|| (!nombre.isEmpty() && categoria == null && proveedor != null && estado.isEmpty())
				|| (!nombre.isEmpty() && categoria == null && proveedor == null && !estado.isEmpty())) {
			return false;
		}
		return true;
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/cargar-info")
	public String cargarInfo(Model model) {
		model.addAttribute("insumo", new Insumo());
		model.addAttribute("titulo", "Carga de insumos");
		return "Views/SI/Inventario/Insumo/cargarInfoInsumo";
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/cargar-info")
	public String cargarArchivo(@ModelAttribute Insumo insumo, RedirectAttributes redirectAttributes) {
		boolean isFlag = false;

		try {
			isFlag = insumoService.saveDataFromUploadFile(insumo.getFile());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		if (isFlag) {
			redirectAttributes.addFlashAttribute("success","Se ha cargado la informacion correctamente a la base de datos!");
		} else {
			redirectAttributes.addFlashAttribute("error","Oh no!, algo ha ocurrido, por favor revise las recomendaciones.");
			return "redirect:/insumo/cargar-info";
		}

		return "redirect:/insumo/";
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"insumos.xlsx\"");
		Sheet hoja = workbook.createSheet("Insumos");

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
		String[] columnas = { "ID", "Nombre", "Categoria", "Proveedor", "Precio", "Estado"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (insumosX == null) {
			insumosX = insumoService.listar();
		}

		for (Insumo insumo : insumosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(insumo.getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(insumo.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(insumo.getCategoria().getCategoria());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(insumo.getProveedor().getNombre());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(insumo.getPrecio());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(insumo.getEstado());
			hoja.autoSizeColumn(5);
			numFila++;
		}
	}

	
}
