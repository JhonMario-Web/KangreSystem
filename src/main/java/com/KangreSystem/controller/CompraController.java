package com.KangreSystem.controller;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
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
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.DetalleCompra;
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IDetalleCompraService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IInventarioService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.ComprasPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/proveedor/servicios/compra")
@Component("/Views/SI/Compra/compras.xlsx")
public class CompraController extends AbstractXlsxView{
	
	private List<Compra> compras = new ArrayList<>();
	
	private List<Compra> comprasX;
	
	private List<DetalleCompra> detallesAgregados = new ArrayList<DetalleCompra>();
	
	private List<Insumo> insumos = new ArrayList<>();
	
	private Administrador admin = new Administrador();
	
	private Compra compra = new Compra();
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private IInventarioService inventarioService;
	
	@Autowired
	private ICompraService compraService;
	
	@Autowired
	private IProveedorService proveedorService;
	
	@Autowired
	private IDetalleCompraService detalleService;
	
	@GetMapping("/")
	public String listar(Model model) {
		this.compras = compraService.listar();
		model.addAttribute("compras", this.compras);
		model.addAttribute("compra", new Compra());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/compras";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Compra compra, RedirectAttributes attr, Model model) {
		Proveedor proveedor = null;
		if (compra.getProveedor().getIdProveedor() != null) {
			proveedor = proveedorService.buscarPorId(compra.getProveedor().getIdProveedor());
		}
		compra.setProveedor(proveedor);
		
		if (compra.getIdCompra() != null && compra.getFecha() != null && compra.getProveedor() != null && !compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() != null && compra.getProveedor() == null && compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() != null && compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() == null && !compra.getEstado().isEmpty()) {
			attr.addFlashAttribute("error", "No se puede filtrar por los filtros seleccionados!");
			return "redirect:/proveedor/servicios/compra/";
		} else if (compra.getIdCompra() == null && compra.getFecha() == null && compra.getProveedor() == null && compra.getEstado().isEmpty()) {
			attr.addFlashAttribute("error", "No se encontraron criterios de busqueda!");
			return "redirect:/proveedor/servicios/compra/";
		} else if (compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() == null && compra.getEstado().isEmpty()) {
			this.compras = new ArrayList<Compra>();
			Compra c = compraService.buscarPorId(compra.getIdCompra());
			if (c == null) {
				this.comprasX = null;
				attr.addFlashAttribute("error", "No se encontraron resultados!");
				return "redirect:/proveedor/servicios/compra/";
			}
			this.compras.add(c);
			model.addAttribute("compras", this.compras);
			model.addAttribute("compra", new Compra());
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Compra/compras";
		}
		
		this.compras = compraService.filtrar(compra.getFecha(), compra.getProveedor(), compra.getEstado());
		
		if (this.compras.isEmpty()) {
			this.comprasX = null;
			attr.addFlashAttribute("error", "No se encontraron resultados!");
			return "redirect:/proveedor/servicios/compra/";
		}
		
		this.comprasX = this.compras;
		
		model.addAttribute("compras", this.compras);
		model.addAttribute("compra", new Compra());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/compras";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.comprasX = null;
		return "redirect:/proveedor/servicios/compra/";
	}
	
	@GetMapping("/detalles/{id}")
	public String detalles(@PathVariable("id") Long idCompra, Model model) {
		Compra compraAUX = compraService.buscarPorId(idCompra);
		model.addAttribute("compra", compraAUX);
		model.addAttribute("detalles", detalleService.buscarDetallesPorCompra(compraAUX));
		return "Views/SI/Compra/detallesCompra";
	}
	
	@GetMapping("/{nit}")
	public String compraProveedor(@PathVariable("nit") String nit) {
		Proveedor proveedor = proveedorService.buscarPorNitProveedor(nit).get(0);
		this.compra = new Compra();
		this.detallesAgregados = new ArrayList<>();
		this.compra.setProveedor(proveedor);
		this.compra.setTotal((long)0);
		Compra compraAuxII;
		String numeroCompra;
		
		if (compraService.listar().isEmpty()) {
			numeroCompra = "1";
			this.compra.setNumeroCompra(numeroCompra);
			this.insumos = insumoService.buscarPorProveedor(proveedor);
			return "redirect:/proveedor/servicios/compra/nueva";
		}
		
		compraAuxII = compraService.listar().get(compraService.listar().size() - 1);
		Integer ultimaCompra  = Integer.parseInt(compraAuxII.getNumeroCompra());
		ultimaCompra++;
		numeroCompra = Integer.toString(ultimaCompra);
		this.compra.setNumeroCompra(numeroCompra);
		this.insumos = insumoService.buscarPorProveedor(proveedor);
		return "redirect:/proveedor/servicios/compra/nueva";
	}
	
	@GetMapping("/nueva")
	public String compraIndex(Model model, Principal principal) {
		
		if (this.compra.getProveedor() == null) {
			return "redirect:/proveedor/servicios";
		}
		
		User user = userService.buscarPorNumeroDoc(principal.getName());
		this.admin = adminService.buscarPorUser(user);
		
		this.compra.setAdmin(this.admin);
		
		long i = 1;
		for (DetalleCompra detalle : detallesAgregados) {
			detalle.setIdDetalle(i);
			i++;
		}
		
		model.addAttribute("agregados", this.detallesAgregados);
		model.addAttribute("detalle", new DetalleCompra());
		model.addAttribute("compra", this.compra);
		model.addAttribute("insumos", this.insumos);
		return "Views/SI/Compra/compraIndex";
	}
	
	@PostMapping("/agregar-item-list")
	public String agregarItemList(@ModelAttribute DetalleCompra detalle, RedirectAttributes attr, Model model) {
		Long total = (long) 0;
		
		if (detalle.getCantidad() <= 0 || detalle.getCantidad() > 100) {
			attr.addFlashAttribute("error", "La cantidad ingresada no es permitida!");
			return "redirect:/proveedor/servicios/compra/nueva";
		}
		
		Insumo insumo = insumoService.buscarPorId(detalle.getInsumo().getIdInsumo());
		detalle.setCompra(this.compra);
		detalle.setInsumo(insumo);
		detalle.setSubtotal(detalle.getInsumo().getPrecio() * detalle.getCantidad());
		
		if (itemIsAdded(detalle) != null) {
			DetalleCompra detalleAux = itemIsAdded(detalle);
			Long indice = detalleAux.getIdDetalle();
			detalleAux.setCantidad(detalle.getCantidad() + detalleAux.getCantidad());
			detalleAux.setSubtotal(detalle.getInsumo().getPrecio() * detalleAux.getCantidad());
			this.detallesAgregados.add(detalleAux);
			this.detallesAgregados.remove(Integer.parseInt(indice.toString()) - 1);
			
			for (DetalleCompra detalleCompra : this.detallesAgregados) {
				total += detalleCompra.getSubtotal();
			}
			
			this.compra.setTotal(total);
			return "redirect:/proveedor/servicios/compra/nueva";
		}
		
		this.detallesAgregados.add(detalle);
		
		for (DetalleCompra detalleCompra : this.detallesAgregados) {
			total += detalleCompra.getSubtotal();
		}
		
		this.compra.setTotal(total);
		return "redirect:/proveedor/servicios/compra/nueva";
	}
	
	private DetalleCompra itemIsAdded(DetalleCompra detalle) {
		for (DetalleCompra detallePedido : detallesAgregados) {
			if (detallePedido.getInsumo().getIdInsumo().equals(detalle.getInsumo().getIdInsumo())) {
				return detallePedido;
			}
		}
		return null;
	}
	
	@GetMapping("/quitar-item/{id}")
	public String quitarItem(@PathVariable("id") int idDetalle, RedirectAttributes attr) {
		this.detallesAgregados.remove(idDetalle);
		
		long total = 0;
		for (DetalleCompra detalleCompra : this.detallesAgregados) {
			total += detalleCompra.getSubtotal();
		}
		this.compra.setTotal(total);
		attr.addFlashAttribute("insumos", insumoService.listar());
		return "redirect:/proveedor/servicios/compra/nueva";
	}
	
	@GetMapping("/vaciar-lista")
	public String vaciarLista() {
		this.detallesAgregados = new ArrayList<>();
		this.compra.setTotal((long)0);
		return "redirect:/proveedor/servicios/compra/nueva";
	}
	
	@GetMapping("/guardar")
	public String guardar(RedirectAttributes attr) {
		if (this.detallesAgregados.isEmpty()) {
			attr.addFlashAttribute("error", "La lista de detalles no debe estar vacia!");
			return "redirect:/proveedor/servicios/compra/nueva";
		} else if (this.detallesAgregados.size() < 3) {
			attr.addFlashAttribute("error", "Para solicitar una compra debe agregar almenos tres insumos!");
			return "redirect:/proveedor/servicios/compra/nueva";
		}
		
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String asunto = "SOLICITUD COMPRA DE INSUMOS";
			String insumos = "<table>";
			insumos += "<thead>";
			insumos += "<tr>";
			insumos += "<th scope='col'>#</th>";
			insumos += "<th scope='col'>Insumo</th>";
			insumos += "<th scope='col'>Cantidad</th>";
			insumos += "</tr>";
			insumos += "<tr><th><hr></th><th><hr></th><th><hr></th></tr>";
			insumos += "</thead>";
			insumos += "<tbody>";
			//correos.toArray(String[]::new);
			for (DetalleCompra detalle : this.detallesAgregados) {
				insumos += "<tr>";
				insumos += "<th>"+detalle.getIdDetalle()+"</th>";
				insumos += "<th>"+detalle.getInsumo().getNombre()+"</th>";
				insumos += "<th>"+detalle.getCantidad()+"</th>";
				insumos += "</tr>";
			}
			
			insumos += "</tbody>";
			insumos += "</table>";
			
			String mailContent = "<p><b>ASUNTO: </b>SOLICITUD COMPRA DE INSUMOS</p>";
			mailContent += "<p><b>EMPRESA: </b>'LA HAMBURGUESERIA'</p>";
			mailContent += "<p><b>DIRECCION: </b>Av. Carrera 15 # 146-60 Local 201 - Bogotá</p>";
			mailContent += "<p><b>TELEFONO: </b>316252063</p>";
			mailContent += insumos;
			mailContent += "<hr><img src='cid:logoImage' width='200'/>";
			
			helper.setTo(this.compra.getProveedor().getEmail().toLowerCase());
			helper.setSubject(asunto);
			helper.setText(mailContent, true);
			
			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);
			
			mailSender.send(message);
			
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
			attr.addFlashAttribute("error", e.getMessage());
			return "redirect:/proveedor/servicios/compra/";
		}
		
		this.compra.setEstado("SOLICITADA");
		this.compra.setFecha(new Date());
		this.compra.setHora(new Date());
		
		inventarioService.comprar(this.compra, this.detallesAgregados);
		
		this.compra = new Compra();
		this.detallesAgregados = new ArrayList<DetalleCompra>();
		this.insumos = new ArrayList<Insumo>();
		return "redirect:/proveedor/servicios/compra/guardar/success";
	}
	
	@GetMapping("/guardar/success")
	public String guardarSuccess() {
		return "Views/SI/Compra/compraSuccess";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idCompra, RedirectAttributes attr) {
		Compra compra = null;
		
		if (idCompra > 0) {
			compra = compraService.buscarPorId(idCompra);
			if (compra == null) {
				attr.addFlashAttribute("error", "El ID de la compra de desea eliminar no existe!");
				return "redirect:/proveedor/servicios/compra/";
			}
			
		} else {
			attr.addFlashAttribute("error", "El ID de la compra de desea eliminar no existe!");
			return "redirect:/proveedor/servicios/compra/";
		}
		
		compraService.eliminar(idCompra);
		attr.addFlashAttribute("warning", "Compra eliminada correctamente!");
		return "redirect:/proveedor/servicios/compra/";
	}

	@GetMapping("/cancelar/{id}")
	public String cancelarCompra(@PathVariable("id") Long idCompra) {
		return "Views/SI/Compra/cancelarCompra";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Compras_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		List<Compra> listCompra = compraService.listar();
		
		ComprasPDFExporter exporter = new ComprasPDFExporter(listCompra);
		exporter.export(response);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"compras.xlsx\"");
		Sheet hoja = workbook.createSheet("Compras");

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
		String[] columnas = { "ID", "Numero", "Proveedor", "Fecha", "Hora", "Estado", "Total", "Administrador"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.comprasX == null) {
			this.comprasX = compraService.listar();
		}

		for (Compra compra : this.comprasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(compra.getIdCompra());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(compra.getNumeroCompra());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(compra.getProveedor().getNombre());
			hoja.autoSizeColumn(2);
			
			String fecha = compra.getFecha().getDate()+"-"+(compra.getFecha().getMonth() + 1)+"-"+(compra.getFecha().getYear() + 1900);
			
			filaData.createCell(3).setCellValue(fecha);
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(compra.getHora().toString());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(compra.getEstado());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(compra.getTotal());
			hoja.autoSizeColumn(6);
			filaData.createCell(7).setCellValue(compra.getAdmin().getUser().getNombres()+" "+compra.getAdmin().getUser().getApellidos());
			hoja.autoSizeColumn(7);
			numFila++;
		}
		
	}

}
