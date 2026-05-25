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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.CategoriaProveedor;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.repository.ProveedorRepository;
import com.KangreSystem.models.service.ICategoriaProveedorService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.util.ProveedorPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/proveedor")
@Component("/Views/SI/Proveedores/proveedores.xlsx")
public class ProveedorController extends AbstractXlsxView {

	@Autowired
	private IProveedorService proveedorService;

	@Autowired
	private ProveedorRepository proveedorRepository;

	@Autowired
	private ICategoriaProveedorService categoriaService;
	
	@Autowired
	private IInsumoService insumoService;

	private List<Proveedor> proveedores;

	private List<Proveedor> proveedoresX;

	@GetMapping("/")
	public String listar(Model model) {
		proveedores = proveedorService.listar();
		model.addAttribute("proveedores", proveedores);
		model.addAttribute("titulo", "Proveedores");
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Proveedores/proveedores";
	}
	
	@GetMapping("/servicios")
	public String servicios(Model model) {
		model.addAttribute("proveedores", proveedorService.listar());
		return "/Views/SI/Proveedores/serviciosProveedor";
	}

	@GetMapping("/agregar")
	public String agregar(Model model) {
		model.addAttribute("proveedor", new Proveedor());
		model.addAttribute("titulo", "Agregar proveedor");
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Proveedores/formAgregarProv";
	}

	@PostMapping("/agregar")
	public String agregar(@ModelAttribute Proveedor proveedor, RedirectAttributes attr,
			 Model model) {
		
		proveedor.setTipoArchivo("normal");
		proveedor.setEnabled(true);
		proveedor.setLogo("proveedor.png");

		try {
			proveedorService.guardar(proveedor);
			attr.addFlashAttribute("success", "Proveedor guardado correctamente!");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			model.addAttribute("error", e.getMessage());
			model.addAttribute("proveedor", proveedor);
			model.addAttribute("titulo", "Agregar proveedor");
			model.addAttribute("categorias", categoriaService.listar());
			return "/Views/SI/Proveedores/formAgregarProv";
		}

		return "redirect:/proveedor/";
	}

	@PostMapping("/")
	public String filtrar(@Param("nitProveedor") String nitProveedor, @Param("categoria") Long categoria,
			@Param("ciudad") String ciudad, @Param("estado") String estado, Model model) {

		CategoriaProveedor categoriaObj = null;
		if (categoria != null) {
			categoriaObj = categoriaService.buscarPorId(categoria);
		} else {
			categoriaObj = null;
		}

		if (!validFilter(nitProveedor, categoria, ciudad, estado)) {
			proveedores = proveedorService.listar();
			proveedoresX = null;
			proveedoresX = proveedores;
			System.out.println(proveedores);
			model.addAttribute("warning", "No se puede filtrar los criterios seleccionados!");
			return listar(model);
		}

		if (nitProveedor.isEmpty() && categoria == null && ciudad.isEmpty() && estado.isEmpty()) {
			proveedores = proveedorService.listar();
			proveedoresX = null;
			proveedoresX = proveedores;
			
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			return listar(model);
		}

		if (!nitProveedor.isEmpty() && categoria == null && ciudad.isEmpty() && estado.isEmpty()) {
			proveedores = proveedorService.buscarPorNitProveedor(nitProveedor);
			proveedoresX = null;
			proveedoresX = proveedores;
			System.out.println(proveedores);
			model.addAttribute("proveedores", proveedores);
			model.addAttribute("titulo", "Proveedores");
			model.addAttribute("categorias", categoriaService.listar());
			return "/Views/SI/Proveedores/proveedores";
		}

		proveedores = proveedorService.buscarPorCategoriaCiudadEnabled(categoriaObj, ciudad, estado);
		proveedoresX = null;
		proveedoresX = proveedores;

		System.out.println(proveedores);
		model.addAttribute("proveedores", proveedores);
		model.addAttribute("titulo", "Proveedores");
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Proveedores/proveedores";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Proveedores_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (proveedoresX == null) {
			ProveedorPDFExporter exporter = new ProveedorPDFExporter(proveedorService.listar());
			exporter.export(response);
		}
		else {
			ProveedorPDFExporter exporter = new ProveedorPDFExporter(proveedoresX);
			exporter.export(response);
		}
		
	}

	@GetMapping("/limpiar")
	public String limpiar(Model model) {
		proveedoresX = null;
		return listar(model);
	}

	@GetMapping("/cargar-info")
	public String cargarInfo(Model model) {
		model.addAttribute("proveedor", new Proveedor());
		model.addAttribute("titulo", "Carga de proveedores");
		return "/Views/SI/Proveedores/cargarInfo";
	}

	@PostMapping("/cargar-info")
	public String cargarArchivo(@ModelAttribute Proveedor proveedor, RedirectAttributes redirectAttributes) {
		boolean isFlag = false;

		try {
			isFlag = proveedorService.saveDataFromUploadFile(proveedor.getFile());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errormessage", e.getMessage());
		}

		if (isFlag) {
			redirectAttributes.addFlashAttribute("success",
					"Se ha cargado la informacion correctamente a la base de datos!");
		} else {
			redirectAttributes.addFlashAttribute("error",
					"Oh no! algo ha ocurrido, por favor revise las recomendaciones.");
			return "redirect:/proveedor/cargar-info";
		}

		return "redirect:/proveedor/";
	}

	@GetMapping("/details/{idProveedor}")
	public ResponseEntity<Proveedor> details(@PathVariable("idProveedor") Long idProveedor) {
		try {
			return new ResponseEntity<Proveedor>(proveedorService.buscarPorId(idProveedor), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Proveedor>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long idProveedor, Model model, RedirectAttributes attr) {
		Proveedor proveedor = null;
		if (idProveedor > 0) {
			proveedor = proveedorService.buscarPorId(idProveedor);
			
			if (proveedor == null) {
				System.out.println("El id del producto no existe");
				attr.addFlashAttribute("error", "El id del proveedor a editar no existe!");
				return "redirect:/proveedor/";
			}
		} else {
			System.out.println("El id del producto no existe");
			attr.addFlashAttribute("error", "El id del proveedor a editar no existe!");
			return "redirect:/proveedor/";
		}
		 
		model.addAttribute("titulo", "Editar proveedor");
		model.addAttribute("proveedor", proveedor);
		model.addAttribute("categorias", categoriaService.listar());
		return "/Views/SI/Proveedores/formEditProv";
	}

	@PostMapping("/edit")
	public String editar(@ModelAttribute Proveedor proveedor, RedirectAttributes attr,
			@RequestParam(value = "file", required = false) MultipartFile logo, Model model) {
		
		Proveedor oldProveedor = proveedorRepository.findById(proveedor.getIdProveedor()).orElse(null);
		boolean available = validProveedor(oldProveedor, proveedor);
		proveedor.setEmail(proveedor.getEmail().toUpperCase());
		proveedor.setDireccion(proveedor.getDireccion().toUpperCase());
		proveedor.setNombre(proveedor.getNombre().toUpperCase());
		
		try {
			if (!available) {
				model.addAttribute("error", "El proveedor ya esta guardado!");
				model.addAttribute("proveedor", proveedor);
				model.addAttribute("titulo", "Editar proveedor");
				model.addAttribute("categorias", categoriaService.listar());
				return edit(proveedor.getIdProveedor(), model, attr);
			} 
			proveedor.setLogo(oldProveedor.getLogo());
			proveedorRepository.save(proveedor);
			attr.addFlashAttribute("success", "Proveedor actualizado correctamente!");
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("proveedor", proveedor);
			model.addAttribute("titulo", "Editar proveedor");
			model.addAttribute("categorias", categoriaService.listar());
			return edit(proveedor.getIdProveedor(), model, attr);
		}
		return "redirect:/proveedor/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idProveedor, RedirectAttributes attribute) {
		proveedorService.eliminar(idProveedor);
		attribute.addFlashAttribute("success", "Proveedor eliminado correctamente");
		return "redirect:/proveedor/";
	}

	@GetMapping("/delete-all")
	public String deleteAll(RedirectAttributes attr) {
		proveedorService.eliminarTodo();
		attr.addFlashAttribute("success", "Proveedores eliminados correctamente");
		return "redirect:/proveedor/";
	}
	
	@GetMapping("/insumos/{nit}")
	public String insumosProveedor(@PathVariable("nit") String nit, Model model) {
		Proveedor proveedor = proveedorService.buscarProveedorPorNit(nit);
		if (proveedor == null) {
			model.addAttribute("error", "El proveedor que desea consultar no existe!");
			return listar(model);
		}
		
		proveedorService.buscarProveedorPorNit(nit);
		List<Insumo> insumosProveedor = insumoService.buscarPorProveedor(proveedor);
		model.addAttribute("insumosProv", insumosProveedor);
		return "/Views/SI/Proveedores/insumosProveedor";
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=\"proveedores.xlsx\"");
		Sheet hoja = workbook.createSheet("Proveedores");

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
		String[] columnas = { "ID", "Nit", "Nombre", "Telefono", "Celular", "Email", "Ciudad", "Direccion", "Categoria",
				"Estado" };

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (proveedoresX == null) {
			proveedoresX = proveedorService.listar();
		}

		for (Proveedor proveedor : proveedoresX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(proveedor.getIdProveedor());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(proveedor.getNitProveedor());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(proveedor.getNombre());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(proveedor.getTelefono());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(proveedor.getCelular());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(proveedor.getEmail());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(proveedor.getCiudad());
			hoja.autoSizeColumn(6);
			filaData.createCell(7).setCellValue(proveedor.getDireccion());
			hoja.autoSizeColumn(7);
			filaData.createCell(8).setCellValue(proveedor.getCategoria().getCategoria());
			hoja.autoSizeColumn(8);
			filaData.createCell(9).setCellValue(proveedor.isEnabled());
			hoja.autoSizeColumn(9);
			numFila++;
		}
	}
	
	public boolean validProveedor(Proveedor oldProveedor, Proveedor newProveedor) {
		boolean nameAvaliable = proveedorRepository.existsByNombre(newProveedor.getNombre());
		boolean nitAvaliable = proveedorRepository.existsByNitProveedor(newProveedor.getNitProveedor());
		
		if ((oldProveedor.getNombre().equals(newProveedor.getNombre()) && oldProveedor.getNitProveedor().equals(newProveedor.getNitProveedor()))) {
			return true;
		} else if (oldProveedor.getNitProveedor().equals(newProveedor.getNitProveedor()) && !nameAvaliable) {
			return true;
		} else if (oldProveedor.getNombre().equals(newProveedor.getNombre()) && !nitAvaliable) {
			return true;
		} else if (!nitAvaliable && !nameAvaliable) {
			return true;
		} 
		
		return false;
	}
	
	public boolean validFilter(String nitProveedor, Long categoria, String ciudad, String estado) {
		if ((!nitProveedor.isEmpty() && categoria != null && !ciudad.isEmpty() && !estado.isEmpty())
				|| (!nitProveedor.isEmpty() && categoria != null && ciudad.isEmpty() && estado.isEmpty())
				|| (!nitProveedor.isEmpty() && categoria == null && !ciudad.isEmpty() && estado.isEmpty())
				|| (!nitProveedor.isEmpty() && categoria == null && ciudad.isEmpty() && !estado.isEmpty())) {
			return false;
		}
		return true;
	}
	
	
}
