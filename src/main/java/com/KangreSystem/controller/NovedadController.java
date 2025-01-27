package com.KangreSystem.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Novedad;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IEmpleadoService;
import com.KangreSystem.models.service.INovedadService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.EmailAux;
import com.KangreSystem.util.NovedadPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/novedad")
@Component("/Views/SI/Novedad/novedades")
public class NovedadController extends AbstractXlsxView{
	
	private List<Novedad> novedades;
	
	private List<Novedad> novedadesX;
	
	private List<EmailAux> correosAgregados = new ArrayList<>();
	
	private List<Administrador> admins;
	
	private List<Cliente> clientes;
	
	private List<Empleado> empleados;  
	
	private List<Proveedor> proveedores;
	
	private List<User> usuarios;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private INovedadService novedadService;
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IProveedorService proveedorService;
	
	@Autowired
	private IEmpleadoService empleadoService;
	
	@GetMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String listar(Model model) {
		this.novedades = novedadService.listar();
		model.addAttribute("titulo", "Novedades");
		model.addAttribute("novedades", this.novedades);
		model.addAttribute("novedad", new Novedad());
		return "/Views/SI/Novedad/novedades";
	}
	
	@PostMapping("/")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String filtrar(@ModelAttribute Novedad novedad, Model model, RedirectAttributes attr) {
		
		if (novedad.getIdNovedad() == null && novedad.getFecha() == null) {
			this.novedadesX = null;
			attr.addFlashAttribute("warning", "No se encontraron criterios de busqueda");
			return "redirect:/novedad/";
		} else if (novedad.getIdNovedad() != null && novedad.getFecha() != null) {
			this.novedadesX = null;
			attr.addFlashAttribute("warning", "No se puede filtrar por ambos criterios de busqueda");
			return "redirect:/novedad/";
		} else if (novedad.getIdNovedad() != null && novedad.getFecha() == null) {
			this.novedades = new ArrayList<>();
			
			if (novedadService.existePorId(novedad.getIdNovedad())) {
				this.novedades.add(novedadService.buscarPorId(novedad.getIdNovedad()));
				this.novedadesX = this.novedades;
			}
			
		} else if (novedad.getIdNovedad() == null && novedad.getFecha() != null) {
			this.novedades = novedadService.buscarPorFecha(novedad.getFecha());
			this.novedadesX = this.novedades;
		}
		
		if (this.novedades.isEmpty()) {
			this.novedadesX = null;
			attr.addFlashAttribute("warning", "No se encontraron resultados!");
			return "redirect:/novedad/";
		}
		
		model.addAttribute("titulo", "Novedades");
		model.addAttribute("novedades", this.novedades);
		return "/Views/SI/Novedad/novedades";
	}
	
	@GetMapping("/limpiar")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String limpiarFiltro() {
		this.novedadesX = null;
		return "redirect:/novedad/";
	}
	
	@GetMapping("/mensaje/{id}")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public ResponseEntity<Novedad> details(@PathVariable("id") Long idNovedad) {
		try {
			return new ResponseEntity<Novedad>(novedadService.buscarPorId(idNovedad), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Novedad>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/delete/{id}")
	@Secured("ROLE_ADMIN")
	public String eliminar(@PathVariable("id") Long idNovedad, RedirectAttributes attr) {
		Novedad novedad;
		
		if (idNovedad > 0) {
			novedad = novedadService.buscarPorId(idNovedad);
			
			if (novedad == null) {
				attr.addFlashAttribute("error", "El ID de la novedad a eliminar no existe!");
				return "redirect:/novedad/";
			}
			
		} else {
			attr.addFlashAttribute("error", "El ID de la novedad a eliminar no existe!");
			return "redirect:/novedad/";
		}
		
		novedadService.eliminar(idNovedad);
		attr.addFlashAttribute("warning", "Novedad eliminada correctamente!");
		return "redirect:/novedad/";
	}

	@GetMapping("/nueva")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String nuevaNovedad(Model model) {
		model.addAttribute("titulo", "Compartir nueva novedad");
		model.addAttribute("novedad", new Novedad());
		
		int i = 1;
		for (EmailAux emailAux : correosAgregados) {
			emailAux.setItem(i);
			i++;
		}
		
		model.addAttribute("correos", this.correosAgregados);
		return "/Views/SI/Novedad/nuevaNovedad";
	}
	
	@PostMapping("/agregar-email")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String agregarEmail(@RequestParam("correo") String correo, RedirectAttributes attr) {
		
		if (this.correosAgregados.size() == 5) {
			attr.addFlashAttribute("warning", "Ya alcanzaste el tope máximo de correos permitidos para agregar (5).");
			return "redirect:/novedad/nueva";
		}
		
		if (!this.correosAgregados.isEmpty()) {
			for (EmailAux emailAux : correosAgregados) {
				if (emailAux.getCorreo().toLowerCase().equals(correo.toLowerCase())) {
					attr.addFlashAttribute("error", "El correo a ingresar ya esta agregado a la lista!");
					attr.addFlashAttribute("correo", correo);
					return "redirect:/novedad/nueva";
				}
			}
		}
		
		EmailAux email = new EmailAux();
		email.setCorreo(correo);
		this.correosAgregados.add(email);
		return "redirect:/novedad/nueva";
	}
	
	@GetMapping("/quitar-email/{item}")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String quitarEmail(@PathVariable("item") int item, RedirectAttributes attr) {
		try {
			this.correosAgregados.remove(item);
		} catch (Exception e) {
			attr.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/novedad/nueva";
	}
	
	@PostMapping("/nueva")
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	public String compartir(@ModelAttribute Novedad novedad, RedirectAttributes attr, Principal principal,
			Model model, @Param("destinatarios") String destinatarios) 
			throws MessagingException, UnsupportedEncodingException{
		List<String> listaCorreos = new ArrayList<>();
		
		novedad.setFecha(new Date());
		novedad.setHora(new Date());
		
		if (destinatarios.isEmpty() && this.correosAgregados.isEmpty()) {
			model.addAttribute("error", "No se ha seleccionado ningun destinatario!");
			model.addAttribute("novedad", novedad);
			model.addAttribute("titulo", "Compartir nueva novedad");
			return "/Views/SI/Novedad/nuevaNovedad";
		}
		
		this.admins = adminService.listar();
		this.usuarios = userService.findAllViaProc();
		this.clientes = clienteService.listar();
		this.proveedores = proveedorService.listar();
		this.empleados = empleadoService.listar();
		
		switch (destinatarios) {
		case "ADMIN":
			
			if (this.correosAgregados.isEmpty()) {
				listaCorreos = EmailAux.getCorreosAdmins(this.admins);
				break;
			}
			
			for (EmailAux email : this.correosAgregados) {
				listaCorreos = EmailAux.getCorreosAdmins(this.admins);
				listaCorreos.add(email.getCorreo());
			}
			
			break;
		case "CLIENTES":
			
			if (this.correosAgregados.isEmpty()) {
				listaCorreos = EmailAux.getCorreosClientes(this.clientes);
				break;
			}
			
			for (EmailAux email : this.correosAgregados) {
				listaCorreos = EmailAux.getCorreosClientes(this.clientes);
				listaCorreos.add(email.getCorreo());
			}
			
			break;
		case "EMPLEADOS":
			
			if (this.correosAgregados.isEmpty()) {
				listaCorreos = EmailAux.getCorreosEmpleados(this.empleados);
				break;
			}
			
			for (EmailAux email : this.correosAgregados) {
				listaCorreos = EmailAux.getCorreosEmpleados(this.empleados);
				listaCorreos.add(email.getCorreo());
			}
			
			break;
		case "PROVEEDORES":
			
			if (this.correosAgregados.isEmpty()) {
				listaCorreos = EmailAux.getCorreosProveedores(this.proveedores);
				break;
			}
			
			for (EmailAux email : this.correosAgregados) {
				listaCorreos = EmailAux.getCorreosProveedores(this.proveedores);
				listaCorreos.add(email.getCorreo());
			}
			
			break;
		case "TODOS":
			
			if (this.correosAgregados.isEmpty()) {
				listaCorreos = EmailAux.getCorreosUsuarios(this.usuarios);
				break;
			}
			
			for (EmailAux email : this.correosAgregados) {
				listaCorreos = EmailAux.getCorreosUsuarios(this.usuarios);
				listaCorreos.add(email.getCorreo());
			}
			break;

		default:
			for(EmailAux email : this.correosAgregados) {
				listaCorreos.add(email.getCorreo());
			}
			break;
		}
		
		String username = principal.getName();
		User user = userService.buscarPorNumeroDoc(username);
		novedad.setUser(user);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String asunto = novedad.getAsunto();
		String destinatario = "";
		
		//correos.toArray(String[]::new);
		for (String correo : listaCorreos) {
			destinatario += correo+", ";
		}
		novedad.setDestinatarios(destinatario);
		String mailContent = "<p><b>ASUNTO:</b> " + novedad.getAsunto() + "</p>";
		mailContent += "<p><b>MENSAJE:</b> " + novedad.getMensaje() + "</p>";
		mailContent += "<p><b>DESTINATARIOS:</b> " + destinatario + "</p>";
		mailContent += "<hr><img src='cid:logoImage' width='200'/>";
		
		try {
			String[] correos = new String[listaCorreos.size()];
			for (int i = 0; i < listaCorreos.size(); i++) {
				correos[i] = listaCorreos.get(i);
			}
			
			helper.setTo(correos);
			helper.setSubject(asunto);
			helper.setText(mailContent, true);
			
			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);
			
			if(!novedad.getFile().isEmpty()) {
				String fileName = StringUtils.cleanPath(novedad.getFile().getOriginalFilename());
				InputStreamSource source = new InputStreamSource() {
					
					@Override
					public InputStream getInputStream() throws IOException {
						return novedad.getFile().getInputStream();
					}
				};
				
				helper.addAttachment(fileName, source);
			}
			
			mailSender.send(message);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("novedad", novedad);
			model.addAttribute("titulo", "Compartir nueva novedad");
			return "/Views/SI/Novedad/nuevaNovedad";
		}
		
		novedadService.guardar(novedad);
		this.correosAgregados = new ArrayList<>();
		return "redirect:/novedad/success";
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO", "ROLE_COCINERO"})
	@GetMapping("/success")
	public String success() {
		return "/Views/SI/Novedad/successMessage";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Novedades_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (novedadesX == null) {
			NovedadPDFExporter exporter = new NovedadPDFExporter(novedadService.listar());
			exporter.export(response);
		}
		else {
			NovedadPDFExporter exporter = new NovedadPDFExporter(novedadesX);
			exporter.export(response);
		}
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"novedades.xlsx\"");
		Sheet hoja = workbook.createSheet("Novedades");

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
		String[] columnas = { "ID", "Usuario", "Asunto", "Fecha", "Mensaje"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.novedadesX == null) {
			this.novedadesX = novedadService.listar();
		}

		for (Novedad novedad : this.novedadesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(novedad.getIdNovedad());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(novedad.getUser().getNombres()+" "+novedad.getUser().getApellidos());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(novedad.getAsunto());
			hoja.autoSizeColumn(2);
			
			String fecha = novedad.getFecha().getDate()+"-"+(novedad.getFecha().getMonth()+1)+"-"+(novedad.getFecha().getYear()+1900);
			
			filaData.createCell(3).setCellValue(fecha);
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(novedad.getMensaje());
			hoja.autoSizeColumn(4);
			numFila++;
		}
		
	}

}
