package com.KangreSystem.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.DetalleDevolucion;
import com.KangreSystem.models.entity.Devolucion;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.IDetalleDevolucionServ;
import com.KangreSystem.models.service.IDevolucionServ;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.models.service.IVencimientoLoteServ;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/proveedor/servicios/devolucion")
@Component("/Views/SI/Devolucion/devoluciones.xlsx")
public class DevolucionController extends AbstractXlsxView{
	
	@Autowired
	private IInsumoService insumosService;
	
	@Autowired
	private IProveedorService proveedorService;
	
	@Autowired
	private IDevolucionServ devolucionService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private IInventarioService inventarioService;
	
	@Autowired
	private IDetalleDevolucionServ detalleDevolucionServ;
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	private Proveedor proveedor = new Proveedor();
	
	private Devolucion devolucion = new Devolucion();
	
	private Administrador admin = new Administrador();
	
	private List<DetalleDevolucion> detallesAgregados = new ArrayList<>();
	
	private List<Insumo> insumos = new ArrayList<>();
	
	private List<Devolucion> devoluciones = new ArrayList<>();
	
	private List<Devolucion> devolucionesX;
	
	@GetMapping("/")
	public String listar(Model model) {
		this.devoluciones = devolucionService.listar();
		model.addAttribute("devoluciones", this.devoluciones);
		model.addAttribute("devolucion", new Devolucion());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Devolucion/devoluciones";
	}
	
	@PostMapping("/")
	public String filtrar(Model model, @ModelAttribute Devolucion dev, RedirectAttributes attr) {
		Proveedor prov = null;
		Date fechaActual = new Date();
		
		if (dev.getFecha() != null) {
			if (dev.getFecha().after(fechaActual)) {
				this.devolucionesX = null;
				attr.addFlashAttribute("warning", "La fecha ingresada es incorrecta, debe ser inferior a la actual!");
				return "redirect:/proveedor/servicios/devolucion/";
			}
		}
		
		if (dev.getProveedor().getIdProveedor() != null) {
			prov = proveedorService.buscarPorId(dev.getProveedor().getIdProveedor());
		}
		
		dev.setProveedor(prov);
		
		if (dev.getIdDevolucion() == null && dev.getProveedor() == null && dev.getFecha() == null) {
			this.devolucionesX = null;
			attr.addFlashAttribute("warning", "No se encontro ningun criterio de busqueda!");
			return "redirect:/proveedor/servicios/devolucion/";
		} else if (dev.getIdDevolucion() != null && (dev.getProveedor() != null || dev.getFecha() != null)) {
			this.devolucionesX = null;
			attr.addFlashAttribute("error", "No se puede filtrar por los filtros seleccionados!");
			return "redirect:/proveedor/servicios/devolucion/";
		} else if (dev.getIdDevolucion() != null && dev.getProveedor() == null && dev.getFecha() == null) {
			this.devoluciones = new ArrayList<>();
			this.devoluciones.add(devolucionService.buscarPorId(dev.getIdDevolucion()));
			this.devolucionesX = this.devoluciones;
		} else if (dev.getIdDevolucion() == null && dev.getProveedor() != null && dev.getFecha() == null) {
			this.devoluciones = devolucionService.buscarPorProveedor(dev.getProveedor());
			this.devolucionesX = this.devoluciones;
		} else if (dev.getIdDevolucion() == null && dev.getProveedor() == null && dev.getFecha() != null) {
			this.devoluciones = devolucionService.buscarPorFecha(dev.getFecha());
			this.devolucionesX = this.devoluciones;
		} else if (dev.getIdDevolucion() == null && dev.getProveedor() != null && dev.getFecha() != null) {
			this.devoluciones = devolucionService.buscarPorFechaAndProveedor(dev.getFecha(), dev.getProveedor());
			this.devolucionesX = this.devoluciones;
		}
		
		if (this.devoluciones.isEmpty()) {
			this.devolucionesX = null;
			attr.addFlashAttribute("warning", "No se encontraron resultados!");
			return "redirect:/proveedor/servicios/devolucion/";
		}
		
		model.addAttribute("devoluciones", this.devoluciones);
		model.addAttribute("devolucion", new Devolucion());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Devolucion/devoluciones";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.devolucionesX = null;
		return "redirect:/proveedor/servicios/devolucion/";
	}
	
	@GetMapping("/{nit}")
	public String devolucionProveedor(@PathVariable("nit") String nit, RedirectAttributes attr) {
		this.proveedor = proveedorService.buscarPorNitProveedor(nit).get(0);
		this.insumos = insumosService.buscarPorProveedorDevolucion(proveedor, true);
		this.devolucion = new Devolucion();
		this.detallesAgregados = new ArrayList<>();
		this.devolucion.setProveedor(proveedor);
		this.devolucion.setTotal((long) 0);
		Devolucion devolucionAux;
		String numeroDevolucion;
		
		if (this.insumos.isEmpty()) {
			attr.addFlashAttribute("error", "Este proveedor no tiene ningun insumo para devolución!");
			return "redirect:/proveedor/servicios";
		}
		
		if (devolucionService.listar().isEmpty()) {
			numeroDevolucion = "1";
			this.devolucion.setNumeroDevolucion(numeroDevolucion);
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		devolucionAux = devolucionService.listar().get(devolucionService.listar().size() - 1);
		Integer ultimaDevolucion = Integer.parseInt(devolucionAux.getNumeroDevolucion());
		ultimaDevolucion++;
		numeroDevolucion = Integer.toString(ultimaDevolucion);
		this.devolucion.setNumeroDevolucion(numeroDevolucion);
		return "redirect:/proveedor/servicios/devolucion/index";
	}

	@GetMapping("/index")
	public String devolucionIndex(Model model, Principal principal) {
		User user = userService.buscarPorNumeroDoc(principal.getName());
		
		this.admin = adminService.buscarPorUser(user);
		this.devolucion.setAdmin(admin);
		
		if (this.devolucion.getProveedor() == null) {
			return "redirect:/proveedor/servicios";
		}
		
		long i = 1;
		for (DetalleDevolucion detalleDevolucion : detallesAgregados) {
			detalleDevolucion.setIdDetalle(i);
			i++;
		}
		
		model.addAttribute("devolucion", this.devolucion);
		model.addAttribute("insumos", this.insumos);
		model.addAttribute("detalle", new DetalleDevolucion());
		model.addAttribute("detalles", this.detallesAgregados);
		return "Views/SI/Devolucion/devolucionIndex";
	}
	
	@PostMapping("/agregar-item")
	public String agregarItem(@ModelAttribute DetalleDevolucion detalle, RedirectAttributes attr,
			Model model) {
		
		Long total = (long) 0;
		Insumo insumo = insumosService.buscarPorId(detalle.getInsumo().getIdInsumo());
		detalle.setInsumo(insumo);
		detalle.setLote(detalle.getLote().toUpperCase());
		
		if (detalle.getMotivo().equals("VENCIMIENTO") && !validFechaActual(detalle.getFechaVencimiento())) {
			attr.addFlashAttribute("error", "La fecha de vencimiento debe ser inferior a la actual!");
			return "redirect:/proveedor/servicios/devolucion/index";
		} else if (!vencimientoService.existePorInsumoFechaLote(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote())
				|| !vencimientoService.vencimientoAvailable(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote())) {
			attr.addFlashAttribute("error", "El insumo a devolver no se encuentra disponible!");
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		if (detalle.getCantidad() <= 0 || detalle.getCantidad() > detalle.getInsumo().getStockActual()) {
			attr.addFlashAttribute("error", "La cantidad ingresada es incorrecta!");
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		detalle.setDevolucion(this.devolucion);
		detalle.setSubtotal(detalle.getInsumo().getPrecio() * detalle.getCantidad());
		
		if (!validCantidadExistencia(detalle)) {
			attr.addFlashAttribute("error", "La cantidad total agregada no debe ser mayor a la existente en el registro de vencimientos.");
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		if (itemIsAdded(detalle) != null) {
			DetalleDevolucion detalleAux = itemIsAdded(detalle);
			Long indice = detalleAux.getIdDetalle();
			detalleAux.setCantidad(detalle.getCantidad() + detalleAux.getCantidad());
			detalleAux.setSubtotal(detalleAux.getInsumo().getPrecio() * detalleAux.getCantidad());
			
			this.detallesAgregados.add(detalleAux);
			this.detallesAgregados.remove(Integer.parseInt(indice.toString()) - 1);
			
			for (DetalleDevolucion detalleDevolucion : detallesAgregados) {
				total += detalleDevolucion.getSubtotal();
			}
			
			this.devolucion.setTotal(total);
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		this.detallesAgregados.add(detalle);
		
		for (DetalleDevolucion detalleDevolucion : detallesAgregados) {
			total += detalleDevolucion.getSubtotal();
		}
		
		this.devolucion.setTotal(total);
		return "redirect:/proveedor/servicios/devolucion/index";
	}
	
	@GetMapping("/quitar-item/{item}")
	public String quitarItem(@PathVariable("item") int item) {
		this.detallesAgregados.remove(item);
		
		long total = 0;
		for (DetalleDevolucion detalleDevolucion : detallesAgregados) {
			total += detalleDevolucion.getSubtotal();
		}
		
		this.devolucion.setTotal(total);
		return "redirect:/proveedor/servicios/devolucion/index";
	}
	
	@GetMapping("/vaciar-lista")
	public String vaciarLista() {
		this.devolucion.setTotal((long) 0);
		this.detallesAgregados = new ArrayList<>();
		return "redirect:/proveedor/servicios/devolucion/index";
	}
	
	@GetMapping("/guardar")
	public String guardarDevolucion(RedirectAttributes attr) {
		
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String asunto = "DEVOLUCION DE INSUMOS | LA HAMBUGUESERIA";
			String insumos = "<!DOCTYPE html'>";
			insumos += "<html>";
			insumos += "<head>";
			insumos += "<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css' >";
			insumos += "</head>";
			insumos += "<table class='table'>";
			insumos += "<thead class='bg-dark'>";
			insumos += "<tr>";
			insumos += "<th scope='col'>#</th>";
			insumos += "<th scope='col'>Insumo</th>";
			insumos += "<th scope='col'>Cantidad</th>";
			insumos += "</tr>";
			insumos += "</thead>";
			insumos += "<tbody>";
			
			for (DetalleDevolucion detalleDevolucion : detallesAgregados) {
				insumos += "<tr>";
				insumos += "<th>"+detalleDevolucion.getIdDetalle()+"</th>";
				insumos += "<th>"+detalleDevolucion.getInsumo().getNombre()+"</th>";
				insumos += "<th>"+detalleDevolucion.getCantidad()+"</th>";
				insumos += "</tr>";
			}
			
			insumos += "</tbody>";
			insumos += "</table>";
			insumos += "</html>";
			
			String mailContent = "<p><b>ASUNTO: </b>DEVOLUCION DE INSUMOS</p>";
			mailContent += "<p><b>EMPRESA: </b>'LA HAMBURGUESERIA'</p>";
			mailContent += "<p><b>DIRECCION: </b>Av. Carrera 15 # 146-60 Local 201 - Bogotá</p>";
			mailContent += "<p><b>TELEFONO: </b>316252063</p>";
			mailContent += insumos;
			mailContent += "<hr><img src='cid:logoImage' width='200'/>";
			
			helper.setTo(this.devolucion.getProveedor().getEmail().toLowerCase());
			helper.setSubject(asunto);
			helper.setText(mailContent, true);
			
			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);
			
			mailSender.send(message);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			attr.addFlashAttribute("error", e.getMessage());
			return "redirect:/proveedor/servicios/devolucion/index";
		}
		
		inventarioService.devolver(this.devolucion, this.detallesAgregados);
		
		this.devolucion = new Devolucion();
		this.detallesAgregados = new ArrayList<>();
		this.insumos = new ArrayList<Insumo>();
		return "redirect:/proveedor/servicios/devolucion/guardar/success";
	}
	
	@GetMapping("/guardar/success")
	public String guardarSuccess() {
		return "Views/SI/Devolucion/devolucionSuccess";
	}
	
	@GetMapping("/detalles/{id}")
	public String detalles(Model model, @PathVariable("id") Long idDevolucion) {
		Devolucion dev = devolucionService.buscarPorId(idDevolucion);
		
		model.addAttribute("detalles", detalleDevolucionServ.buscarPorDevolucion(dev));
		model.addAttribute("devolucion", dev);
		return "Views/SI/Devolucion/detallesDevolucion";
	}
	
	@SuppressWarnings("deprecation")
	public DetalleDevolucion itemIsAdded(DetalleDevolucion detalle) {
		
		detalle.getFechaVencimiento().setHours(0);
		detalle.getFechaVencimiento().setMinutes(0);
		detalle.getFechaVencimiento().setSeconds(0);
		
		for (DetalleDevolucion detallePedido : detallesAgregados) {
			
			detallePedido.getFechaVencimiento().setHours(0);
			detallePedido.getFechaVencimiento().setMinutes(0);
			detallePedido.getFechaVencimiento().setSeconds(0);
			
			if (detallePedido.getInsumo().getIdInsumo().equals(detalle.getInsumo().getIdInsumo())
					&& detallePedido.getMotivo().equals(detalle.getMotivo())
					&& detallePedido.getFechaVencimiento().equals(detalle.getFechaVencimiento())
					&& detallePedido.getLote().toUpperCase().equals(detalle.getLote().toUpperCase())) {
				return detallePedido;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"devoluciones.xlsx\"");
		Sheet hoja = workbook.createSheet("Devoluciones");
		
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
		String[] columnas = { "ID", "Proveedor", "Fecha", "Hora", "Total"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.devolucionesX == null) {
			this.devolucionesX = devolucionService.listar();
		}

		for (Devolucion devolucion : this.devolucionesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(devolucion.getIdDevolucion());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(devolucion.getProveedor().getNombre());
			hoja.autoSizeColumn(1);
			String fecha = devolucion.getFecha().getDate()+"-"+(devolucion.getFecha().getMonth()+1)+"-"+(devolucion.getFecha().getYear()+1900);
			filaData.createCell(2).setCellValue(fecha);
			hoja.autoSizeColumn(2);
			String hora = devolucion.getHora().getHours()+":"+devolucion.getHora().getMinutes();
			filaData.createCell(3).setCellValue(hora);
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue("$"+devolucion.getTotal());
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
	
	public boolean validCantidadExistencia(DetalleDevolucion detalle) {
		int total = 0;
		VencimientoLote vencimiento = vencimientoService.buscarPorInsumoFechaLote(detalle.getInsumo(), detalle.getFechaVencimiento(), detalle.getLote());
		
		if (detalle.getCantidad() > vencimiento.getCantidadDisponible()) {
			System.out.println("CANTIDAD MAYOR");
			return false;
		}
		
		for (DetalleDevolucion detalleDevolucion : this.detallesAgregados) {
			
			
			if (detalle.getInsumo().getIdInsumo().equals(detalleDevolucion.getInsumo().getIdInsumo())
					&& detalle.getLote().equals(detalleDevolucion.getLote())) {
				total += detalle.getCantidad() + detalleDevolucion.getCantidad();
				
				if (total > vencimiento.getCantidadDisponible()) {
					System.out.println("SUMA MAYOR");
					return false;
				}
			}
		}
		
		return true;
	}
}
