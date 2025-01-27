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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Calificacion;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Pedido;
import com.KangreSystem.models.service.ICalificacionService;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IPedidoService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.CalificacionPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/pedido/calificacion")
@Component("/Views/SI/Fidelizacion/Calificacion/calificaciones.xlsx")
public class CalificacionController extends AbstractXlsxView{
	
	private Pedido pedido = new Pedido();
	
	private List<Calificacion> calificaciones = new ArrayList<>();
	
	private List<Calificacion> calificacionesX;
	
	@Autowired
	private IPedidoService pedidoService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private ICalificacionService calificacionService;
	
	@GetMapping("/")
	@Secured("ROLE_ADMIN")
	public String listar(Model model) {
		this.calificaciones = calificacionService.listar();
		model.addAttribute("calificaciones", this.calificaciones);
		model.addAttribute("calificacion", new Calificacion());
		return "Views/SI/Fidelizacion/Calificacion/calificaciones";
	}
	
	@PostMapping("/")
	@Secured("ROLE_ADMIN")
	public String filtrar(@Param("idCalificacionMF") Long idCalificacionMF, @ModelAttribute Calificacion calificacion,
			RedirectAttributes attr, Model model) {
		
		if (idCalificacionMF != null && calificacion.getFecha() != null && !calificacion.getCalificacion().isEmpty()
				|| idCalificacionMF != null && calificacion.getFecha() != null && calificacion.getCalificacion().isEmpty()
				|| idCalificacionMF != null && calificacion.getFecha() == null && !calificacion.getCalificacion().isEmpty()) {
			
			attr.addFlashAttribute("error", "No se puede filtrar por los criterios seleccionados!");
			return "redirect:/pedido/calificacion/";
		} else if (idCalificacionMF == null && calificacion.getFecha() == null && calificacion.getCalificacion().isEmpty()) {
			
			attr.addFlashAttribute("warning", "No se encontraron criterios de busqueda!");
			return "redirect:/pedido/calificacion/";
		} else if (idCalificacionMF != null && calificacion.getFecha() == null && calificacion.getCalificacion().isEmpty()) {
			this.calificaciones = new ArrayList<Calificacion>();
			Calificacion c = calificacionService.buscarPorId(idCalificacionMF);
			
			if (c == null) {
				this.calificacionesX = null;
				attr.addFlashAttribute("warning", "No se encontraron resultados de busqueda!");
				return "redirect:/pedido/calificacion/";
			}
			
			this.calificaciones.add(c);
			this.calificacionesX = this.calificaciones;
			
			model.addAttribute("calificaciones", this.calificaciones);
			model.addAttribute("calificacion", new Calificacion());
			return "Views/SI/Fidelizacion/Calificacion/calificaciones";
		}
		
		this.calificaciones = calificacionService.filtrar(calificacion);
		
		if (this.calificaciones.isEmpty()) {
			this.calificacionesX = null;
			attr.addFlashAttribute("warning", "No se encontraron resultados de busqueda!");
			return "redirect:/pedido/calificacion/";
		}
		this.calificacionesX = this.calificaciones;
		
		model.addAttribute("calificaciones", this.calificaciones);
		model.addAttribute("calificacion", new Calificacion());
		return "Views/SI/Fidelizacion/Calificacion/calificaciones";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.calificacionesX = null;
		return "redirect:/pedido/calificacion/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/details/{id}")
	public ResponseEntity<Calificacion> details(@PathVariable("id") Long idCalificacion) {
		try {
			return new ResponseEntity<>(calificacionService.buscarPorId(idCalificacion), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Calificacion>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/nueva")
	public String nuevaCalificacion(Model model) {
		model.addAttribute("calificacion", new Calificacion());
		return "Views/SI/Fidelizacion/Calificacion/nuevaCalificacion";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/agregar-pedido")
	public String agregarPedido(@RequestParam("idPedido") Long idPedido, RedirectAttributes attr, Principal principal) {
		Cliente cliente = clienteService.buscarPorUser(userService.buscarPorNumeroDoc(principal.getName()));
		Calificacion calificacion = new Calificacion();
		this.pedido = null;
		
		if (idPedido > 0) {
			pedido = pedidoService.buscarPorId(idPedido);
			
			if (pedido == null) {
				attr.addFlashAttribute("error", "No se encontro ningun pedido con ese ID");
				return "redirect:/pedido/calificacion/nueva";
			}
		} else {
			attr.addFlashAttribute("error", "No se encontro ningun pedido con ese ID");
			return "redirect:/pedido/calificacion/nueva";
		}
		
		calificacion.setCliente(cliente);
		calificacion.setPedido(this.pedido);
		calificacion.setFecha(new Date());
		
		if (!calificacionService.validPermitida(calificacion, principal)) {
			attr.addFlashAttribute("error", "El pedido que desea calificar no esta disponible!");
			return "redirect:/pedido/calificacion/nueva";
		} else if (calificacionService.validIsCalificada(calificacion)) {
			attr.addFlashAttribute("error", "El pedido que desea evaluar ya esta calificado!");
			return "redirect:/pedido/calificacion/nueva";
		}
		
		attr.addFlashAttribute("pedido", pedido);
		return "redirect:/pedido/calificacion/nueva";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/calificar")
	public String calificar(@ModelAttribute Calificacion calificacion, RedirectAttributes attr, Principal principal) {
		Cliente cliente = clienteService.buscarPorUser(userService.buscarPorNumeroDoc(principal.getName()));
		
		calificacion.setCliente(cliente);
		calificacion.setPedido(this.pedido);
		calificacion.setFecha(new Date());
		calificacion.setHora(new Date());
		
		calificacionService.guardar(calificacion);
		this.pedido.setCalificado(true);
		pedidoService.guardar(this.pedido);

		attr.addFlashAttribute("success", "Hemos recibido tu calificacion!");
		this.pedido = new Pedido();
		return "redirect:/pedido/calificacion/nueva";
	}
	
	@GetMapping("/cancelar")
	public String cancelarCalificacion() {
		return "redirect:/pedido/calificacion/nueva";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Proveedores_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		List<Calificacion> listCalificacion = calificacionService.listar();
		
		CalificacionPDFExporter exporter = new CalificacionPDFExporter(listCalificacion);
		exporter.export(response);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setHeader("Content-Disposition", "attachment; filename=\"calificaciones.xlsx\"");
		Sheet hoja = workbook.createSheet("Calificaciones");

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
		String[] columnas = { "ID", "Fecha", "Pedido", "Cliente", "Calificacion", "Descripcion"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.calificacionesX == null) {
			this.calificacionesX = calificacionService.listar();
		}

		for (Calificacion calificacion : this.calificacionesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(calificacion.getIdCalificacion());
			hoja.autoSizeColumn(0);
			
			String fechaRegistro = calificacion.getFecha().getDate()+"-"+(calificacion.getFecha().getMonth() + 1)+"-"+(calificacion.getFecha().getYear() + 1900);
			
			filaData.createCell(1).setCellValue(fechaRegistro);
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(calificacion.getPedido().getIdPedido());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(calificacion.getCliente().getUser().getNombres()+" "+calificacion.getCliente().getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(calificacion.getCalificacion());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(calificacion.getDescripcion());
			hoja.autoSizeColumn(5);
			numFila++;
		}
		
	}

}
