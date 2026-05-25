package com.KangreSystem.controller;

import java.io.IOException;
import java.security.Principal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.entity.CategoriaInsumo;
import com.KangreSystem.models.entity.DetalleAveria;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.IAveriaService;
import com.KangreSystem.models.service.ICategoriaInsumoService;
import com.KangreSystem.models.service.IDetalleAveriaService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.models.service.IVencimientoLoteServ;
import com.KangreSystem.util.AveriaPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/inventario/averia")
@Secured("ROLE_ADMIN")
@Component("/Views/SI/Inventario/Averias/averias")
public class AveriaController extends AbstractXlsxView{
	
	private List<Averia> averias;
	
	private List<Averia> averiasX;
	
	private List<DetalleAveria> detallesAgregados = new ArrayList<>();
	
	private List<Insumo> insumos;
	
	private CategoriaInsumo categoria;
	
	private Administrador admin = new Administrador();
	
	private Averia averia = new Averia();
	
	private boolean mode = false;
	
	@Autowired
	private ICategoriaInsumoService categoriaService;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IAveriaService averiaService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private IInventarioService inventarioService;
	
	@Autowired
	private IDetalleAveriaService detalleService;
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	@GetMapping("/")
	public String listar(Model model) {
		this.averias = averiaService.listar();		
		model.addAttribute("averia", new Averia());
		model.addAttribute("averias", this.averias);
		return "Views/SI/Inventario/Averias/averias";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Averia averia, RedirectAttributes attr, Model model) {
		if (averia.getNumeroAveria().isEmpty() && averia.getFecha() == null) {
			this.averiasX = null;
			attr.addFlashAttribute("warning", "No se encontro ningun criterio de busqueda!");
			return "redirect:/inventario/averia/";
		} else if (!averia.getNumeroAveria().isEmpty() && averia.getFecha() != null) {
			this.averiasX = null;
			attr.addFlashAttribute("warning", "No se puede filtrar por ambos criterios de busqueda!");
			return "redirect:/inventario/averia/";
		} else if (!averia.getNumeroAveria().isEmpty() && averia.getFecha() == null) {
			
			if (averiaService.buscarPorNumeroAveria(averia.getNumeroAveria()) == null) {
				this.averiasX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
			this.averias = new ArrayList<>();
			this.averias.add(averiaService.buscarPorNumeroAveria(averia.getNumeroAveria()));
			this.averiasX = this.averias;
			
		} else if (averia.getNumeroAveria().isEmpty() && averia.getFecha() != null) {
			this.averias = averiaService.buscarPorFecha(averia.getFecha());
			this.averiasX = this.averias;
			
			if (this.averias.isEmpty()) {
				this.averiasX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
		}
		
		model.addAttribute("averia", new Averia());
		model.addAttribute("averias", this.averias);
		return "Views/SI/Inventario/Averias/averias";
	}
	
	@GetMapping("/limpiar")
	public String limpiarFiltro() {
		this.averiasX = null;
		return "redirect:/inventario/averia/";
	}
	
	@GetMapping("/preparar-nueva")
	public String preparar(RedirectAttributes attr) {
		Averia averiaAux;
		this.insumos = insumoService.insumosConExistencia();
		this.mode = true;
		this.averia = new Averia();
		String numeroAveria;
		
		if (averiaService.listar().isEmpty()) {
			numeroAveria = "1";
			this.averia.setNumeroAveria(numeroAveria);
			this.averia.setTotal((long)0);
			attr.addFlashAttribute("averia", this.averia);
			attr.addFlashAttribute("mode", this.mode);
			return "redirect:/inventario/averia/nueva";
		}
		
		averiaAux = averiaService.listar().get(averiaService.listar().size() - 1);
		Integer ultimaAveria  = Integer.parseInt(averiaAux.getNumeroAveria());
		ultimaAveria++;
		numeroAveria = Integer.toString(ultimaAveria);
		this.averia.setNumeroAveria(numeroAveria);
		this.averia.setTotal((long)0);
		attr.addFlashAttribute("averia", this.averia);
		attr.addFlashAttribute("mode", this.mode);
		attr.addFlashAttribute("insumos", insumoService.listar());
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/nueva")
	public String averiaIndex(Model model, Principal principal) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		this.admin = adminService.buscarPorUser(user);
		this.averia.setAdmin(this.admin);
	
		if (!this.mode) {
			Averia averiaAux = new Averia();
			averiaAux.setNumeroAveria("0");
			averiaAux.setTotal((long) 0);
			averiaAux.setAdmin(this.admin);
			this.detallesAgregados = new ArrayList<>();
			model.addAttribute("detalle", new DetalleAveria());
			model.addAttribute("averia", averiaAux);
			model.addAttribute("mode", this.mode);
			return "Views/SI/Inventario/Averias/averiaIndex";
		}
		
		long i = 1;
		for (DetalleAveria detalle : detallesAgregados) {
			detalle.setIdDetalle(i);
			i++;
		}
		
		model.addAttribute("agregados", this.detallesAgregados);
		model.addAttribute("detalle", new DetalleAveria());
		model.addAttribute("mode", this.mode);
		model.addAttribute("averia", this.averia);
		model.addAttribute("insumos", this.insumos);
		model.addAttribute("fechaActual", new Date());
		model.addAttribute("totalAgregados", this.detallesAgregados.size());
		return "/Views/SI/Inventario/Averias/averiaIndex";
	}
	
	@GetMapping("/insumo/{id}")
	public ResponseEntity<Insumo> details(@PathVariable("id") Long idInsumo) {
		
		try {
			return new ResponseEntity<Insumo>(insumoService.buscarPorId(idInsumo), HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<Insumo>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/agregar-item-list")
	public String agregarItemList(@ModelAttribute DetalleAveria detalle, RedirectAttributes attr, Model model) {
		Long total = (long) 0;
		detalle.setLote(detalle.getLote().toUpperCase());
		
		if (detalle.getCausa().equals("VENCIMIENTO") && !validFechaActual(detalle.getFechaVencimiento())) {
			attr.addFlashAttribute("error", "La fecha de vencimiento debe ser inferior a la actual!");
			return "redirect:/inventario/averia/nueva";
		} else if (!vencimientoService.existePorInsumoFechaLote(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote())
				|| !vencimientoService.vencimientoAvailable(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote())) {
			attr.addFlashAttribute("error", "El insumo a devolver no se encuentra disponible!");
			return "redirect:/inventario/averia/nueva";
		} 
		
		if (detalle.getCantidad() <= 0) {
			attr.addFlashAttribute("error", "La cantidad ingresada es incorrecta!");
			return "redirect:/inventario/averia/nueva";
		}
		
		Insumo insumo = insumoService.buscarPorId(detalle.getInsumo().getIdInsumo());
		detalle.setAveria(this.averia);
		detalle.setInsumo(insumo);
		detalle.setSubtotal(detalle.getInsumo().getPrecio() * detalle.getCantidad());
		
		if (!validCantidadExistencia(detalle)) {
			attr.addFlashAttribute("error", "La cantidad total agregada no debe ser mayor a la existente en el registro de vencimientos!");
			return "redirect:/inventario/averia/nueva";
		}
		
		if (itemIsAdded(detalle) != null) {
			DetalleAveria detalleAux = itemIsAdded(detalle);
			Long indice = detalleAux.getIdDetalle();
			detalleAux.setCantidad(detalle.getCantidad() + detalleAux.getCantidad());
			detalleAux.setSubtotal(detalle.getInsumo().getPrecio() * detalleAux.getCantidad());
			this.detallesAgregados.add(detalleAux);
			this.detallesAgregados.remove(Integer.parseInt(indice.toString()) - 1);
			
			for (DetalleAveria detalleAveria : this.detallesAgregados) {
				total += detalleAveria.getSubtotal();
			}
			
			this.averia.setTotal(total);
			return "redirect:/inventario/averia/nueva";
		}
		
		this.detallesAgregados.add(detalle);
		
		for (DetalleAveria detalleAveria : this.detallesAgregados) {
			total += detalleAveria.getSubtotal();
		}
		
		this.averia.setTotal(total);
		return "redirect:/inventario/averia/nueva";
	}
	
	@PostMapping("/agregar-item-cod")
	public String agregarItemCod(@RequestParam("idInsumoCod") Long idInsumoCod, Model model, RedirectAttributes attr) {
		Insumo insumo = insumoService.buscarPorId(idInsumoCod);
		
		if (insumo == null) {
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		this.insumos = new ArrayList<>();
		this.insumos.add(insumo);
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/quitar-item/{id}")
	public String quitarItem(@PathVariable("id") int idDetalle, RedirectAttributes attr) {
		this.detallesAgregados.remove(idDetalle);
		
		long total = 0;
		for (DetalleAveria detalleAveria : this.detallesAgregados) {
			total += detalleAveria.getSubtotal();
		}
		this.averia.setTotal(total);
		attr.addFlashAttribute("insumos", insumoService.listar());
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/limpiar-filtro-categorias")
	public String limpiarFiltroCategorias() {
		this.insumos = insumoService.listar();
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/vaciar-lista")
	public String vaciarLista() {
		this.detallesAgregados = new ArrayList<>();
		this.averia.setTotal((long)0);
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/guardar")
	public String guardar(RedirectAttributes attr) {
		if (this.detallesAgregados.isEmpty()) {
			attr.addFlashAttribute("error", "La lista de detalles no debe estar vacia!");
			return "redirect:/inventario/averia/nueva";
		}
		
		if (averiaService.unicaPorDia()) {
			attr.addFlashAttribute("error", "No se puede realizar mas de una averia por dia!");
			return "redirect:/inventario/averia/nueva";
		}
		
		inventarioService.averiar(this.averia, this.detallesAgregados);
		
		this.mode = false;
		return "redirect:/inventario/averia/guardar/success";
	}
	
	@GetMapping("/guardar/success")
	public String guardarSuccess() {
		return "Views/SI/Inventario/Averias/guardarSuccess";
	}
	
	@GetMapping("/cancelar")
	public String cancelar() {
		this.mode = false;
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idAveria, RedirectAttributes attr) {
		Averia averiaAux = null;
		if (idAveria > 0) {
			averiaAux = averiaService.buscarPorId(idAveria);
			
			if (averiaAux == null) {
				attr.addFlashAttribute("error", "El ID de la averia a eliminar no existe!");
				return "redirect:/inventario/averia/";
			}
			
		} else {
			attr.addFlashAttribute("error", "El ID de la averia a eliminar no existe!");
			return "redirect:/inventario/averia/";
		}
		
		if (!availableToCancel(averiaAux)) {
			attr.addFlashAttribute("error", "La averia solo puede cancelarse el mismo dia que se realiza!");
			return "redirect:/inventario/averia/";
		}
		
		inventarioService.cancelarAveria(averiaAux);
		
		attr.addFlashAttribute("warning", "Averia eliminada correctamente!");
		return "redirect:/inventario/averia/";
	}
	
	@GetMapping("/detalles/{id}")
	public String detalles(@PathVariable("id") Long idAveria, Model model) {
		Averia averiaAux = averiaService.buscarPorId(idAveria);
		model.addAttribute("averia", averiaAux);
		model.addAttribute("detalles", detalleService.buscarDetallesPorAveria(averiaAux));
		return "Views/SI/Inventario/Averias/detallesAveria";
	}
	
	@GetMapping("/categoria/frutas")
	public String categoriaFrutas(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("FRUTAS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/verduras-especias")
	public String verdurasEspecias(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("VERDURAS Y ESPECIAS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/legumbres")
	public String legumbres(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("LEGUMBRES");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/productos-lacteos")
	public String productosLacteos(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("PRODUCTOS LACTEOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/panaderia")
	public String panaderia(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("PANADERIA");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/carnes-derivados")
	public String carnesDerivados(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("CARNES Y DERIVADOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/confiteria")
	public String confiteria(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("CONFITERIA");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/gaseosas-jugos")
	public String gaseosasJugos(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("GASEOSAS Y JUGOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/bebidas-alcoholicas")
	public String bebidasAlcoholicas(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("BEBIDAS ALCOHOLICAS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/salsas")
	public String salsas(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("SALSAS Y ESPECIAS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/productos-procesados")
	public String productosProcesados(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("PRODUCTOS PROCESADOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/condimentos")
	public String condimentos(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("CONDIMENTOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/bebidas-calientes")
	public String bebidasCalientes(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("BEBIDAS CALIENTES");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/aceites-derivados")
	public String aceitesDerivados(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("ACEITES Y DERIVADOS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/categoria/hierbas")
	public String hierbas(Model model, RedirectAttributes attr) {
		this.categoria = categoriaService.buscarPorCategoria("HIERBAS");
		this.insumos = new ArrayList<>();
		this.insumos = insumoService.buscarPorCategoria(this.categoria);
		
		if (this.insumos.isEmpty()) {
			this.insumos = insumoService.listar();
			attr.addFlashAttribute("warning", "No se encontro ningun resultado!");
			return "redirect:/inventario/averia/nueva";
		}
		
		return "redirect:/inventario/averia/nueva";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Averias_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (averiasX == null) {
			AveriaPDFExporter exporter = new AveriaPDFExporter(averiaService.listar());
			exporter.export(response);
		}
		else {
			AveriaPDFExporter exporter = new AveriaPDFExporter(this.averiasX);
			exporter.export(response);
		}
		
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"averias.xlsx\"");
		Sheet hoja = workbook.createSheet("Averias");

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
		String[] columnas = { "ID", "Numero averia", "Fecha", "Total", "Administrador"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.averiasX == null) {
			this.averiasX = averiaService.listar();
		}

		for (Averia averia : this.averiasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(averia.getIdAveria());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(averia.getNumeroAveria());
			hoja.autoSizeColumn(1);
			
			String fechaRegistro = averia.getFecha().getDate()+"-"+(averia.getFecha().getMonth() + 1)+"-"+(averia.getFecha().getYear() + 1900);
			
			filaData.createCell(2).setCellValue(fechaRegistro);
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue("$"+averia.getTotal());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(averia.getAdmin().getUser().getNombres()+" "+averia.getAdmin().getUser().getApellidos());
			hoja.autoSizeColumn(4);
			numFila++;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean validFechaActual(Date fecha) {
		Date fechaActual = new Date();
		
		int diaActual = fechaActual.getDate();
		int mesActual = fechaActual.getMonth();
		int anioActual = fechaActual.getYear();
		
		int diaVencimiento = fecha.getDate();
		int mesVencimiento = fecha.getMonth();
		int anioVencimiento = fecha.getYear();
		
		if (diaVencimiento <= diaActual && mesVencimiento <= mesActual && anioVencimiento <= anioActual) {
			return true;
		}
		
		return false;
	}
	
	public boolean validCantidadExistencia(DetalleAveria detalle) {
		int total = 0;
		VencimientoLote vencimiento = vencimientoService.buscarPorInsumoFechaLote(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote());
		
		if (detalle.getCantidad() > vencimiento.getCantidadDisponible()) {
			return false;
		}
		
		for (DetalleAveria detalleAveria : this.detallesAgregados) {
			
			
			if (detalle.getInsumo().getIdInsumo().equals(detalleAveria.getInsumo().getIdInsumo())
					&& detalle.getLote().equals(detalleAveria.getLote())) {
				total += detalle.getCantidad() + detalleAveria.getCantidad();
				
				if (total > vencimiento.getCantidadDisponible()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public DetalleAveria itemIsAdded(DetalleAveria detalle) {
		
		detalle.getFechaVencimiento().setHours(0);
		detalle.getFechaVencimiento().setMinutes(0);
		detalle.getFechaVencimiento().setSeconds(0);
		
		for (DetalleAveria detalleAveria : detallesAgregados) {
			
			detalleAveria.getFechaVencimiento().setHours(0);
			detalleAveria.getFechaVencimiento().setMinutes(0);
			detalleAveria.getFechaVencimiento().setSeconds(0);
			
			if (detalleAveria.getInsumo().getIdInsumo().equals(detalle.getInsumo().getIdInsumo())
					&& detalle.getCausa().equals(detalleAveria.getCausa())
					&& detalle.getLote().equals(detalleAveria.getLote())
					&& detalle.getFechaVencimiento().equals(detalleAveria.getFechaVencimiento())) {
				return detalleAveria;
			}
			
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public boolean availableToCancel(Averia averia) {
		Date fechaActual = new Date();
		
		fechaActual.setHours(0);
		fechaActual.setMinutes(0);
		fechaActual.setSeconds(0);
		
		averia.getFecha().setHours(0);
		averia.getFecha().setMinutes(0);
		averia.getFecha().setSeconds(0);
		
		int diaActual = fechaActual.getDate();
		int mesActual = fechaActual.getMonth();
		int anioActual = fechaActual.getYear();
		
		int diaAveria = averia.getFecha().getDate();
		int mesAveria = averia.getFecha().getMonth();
		int anioAveria = averia.getFecha().getYear();
		
		if (diaActual == diaAveria && mesActual == mesAveria && anioActual == anioAveria) {
			return true;
		}
		
		return false;
	}
}
