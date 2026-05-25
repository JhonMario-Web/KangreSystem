package com.KangreSystem.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.Oferta;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IOfertaService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.EmailAux;
import com.KangreSystem.util.OfertaPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/oferta")
@Component("/Views/SI/Fidelizacion/Ofertas/ofertas.xlsx")
public class OfertaController extends AbstractXlsxView{
	
	@Autowired
	private IOfertaService ofertaService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	public JavaMailSender mailSender;
	
	@Autowired
	private IAdminService adminService;
	
	//private List<User> listaUsuarios;
	
	private List<Cliente> listaClientes;
	
	private String correosCliente[];
	
	private List<Oferta> ofertas;
	
	private List<Oferta> ofertasX;

	@GetMapping("/")
	public String listar(Model model) {
		this.ofertas = ofertaService.listar();
		model.addAttribute("titulo", "Ofertas");
		model.addAttribute("ofertas", this.ofertas);
		model.addAttribute("oferta", new Oferta());
		return "/Views/SI/Fidelizacion/Ofertas/ofertas";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Oferta oferta ,@Param("estado") String estado, Model model) {

		if (!oferta.getNombre().isEmpty() && !estado.isEmpty()) {
			this.ofertasX = null;
			model.addAttribute("warning", "No se puede filtrar por los criterios seleccionados!");
			return listar(model);
		}

		if (!oferta.getNombre().isEmpty()) {
			this.ofertas = ofertaService.buscarOfertasPorNombre(oferta.getNombre());
			this.ofertasX = this.ofertas;
			
			if (this.ofertas.isEmpty()) {
				this.ofertasX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
			
			model.addAttribute("titulo", "Ofertas");
			model.addAttribute("ofertas", this.ofertas);
			model.addAttribute("oferta", new Oferta());
			return "/Views/SI/Fidelizacion/Ofertas/ofertas";
		}
		
		this.ofertas = ofertaService.buscarPorEnabled(estado);
		this.ofertasX = this.ofertas;
		
		if (this.ofertas.isEmpty()) {
			this.ofertasX = null;
			model.addAttribute("warning", "No se encontraron resultados!");
			return listar(model);
		}
		
		model.addAttribute("ofertas", this.ofertas);
		model.addAttribute("titulo", "Ofertas");
		return "/Views/SI/Fidelizacion/Ofertas/ofertas";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Ofertas_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (ofertasX == null) {
			OfertaPDFExporter exporter = new OfertaPDFExporter(ofertaService.listar());
			exporter.export(response);
		}
		else {
			OfertaPDFExporter exporter = new OfertaPDFExporter(ofertasX);
			exporter.export(response);
		}
	}
	
	@GetMapping("/limpiar")
	public String limpiar() {
		this.ofertasX = null;
		return "redirect:/oferta/";
	}
	
	@GetMapping("/compartir")
	public String compartir(Model model) {
		model.addAttribute("titulo", "Compartir nueva oferta");
		model.addAttribute("oferta", new Oferta());
		return "/Views/SI/Fidelizacion/Ofertas/nuevaOferta";
	}
	
	@SuppressWarnings("deprecation")
	@PostMapping("/compartir")
	public String compartir(@ModelAttribute Oferta oferta, RedirectAttributes attr, Principal principal, Model model) 
			throws MessagingException, UnsupportedEncodingException{
		if (!validarVigencia(oferta.getFechaInicio(), oferta.getFechaFin())) {
			model.addAttribute("error", "Las fechas ingresadas son incorrectas, recuerda leer las recomendaciones!");
			model.addAttribute("oferta", oferta);
			model.addAttribute("titulo", "Compartir nueva oferta");
			return "/Views/SI/Fidelizacion/Ofertas/nuevaOferta";
		}
		
		listaClientes = clienteService.listar();
		correosCliente = EmailAux.getCorreoClientes(listaClientes);
		
		String username = principal.getName();
		User user = userService.buscarPorNumeroDoc(username);
		Administrador admin = adminService.buscarPorUser(user);
		oferta.setAdmin(admin);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String asunto = "OFERTA | LA HAMBURGUESERIA";
		String fechaInicio = "<p><b>Fecha Inicio: </b>"+ oferta.getFechaInicio().getDate()+" / "+ oferta.getFechaInicio().getMonth() + " / " + (oferta.getFechaInicio().getYear()+ 1900) + "</p>";
		String fechaFin = "<p><b>Fecha Fin: </b>"+ oferta.getFechaFin().getDate()+" / "+ oferta.getFechaFin().getMonth() + " / " + (oferta.getFechaFin().getYear() + 1900)+"</p>";
		String vigencia = fechaInicio+" - "+fechaFin;
		String mailContent = "<p><b>Oferta:</b> " + oferta.getNombre() + "</p>";
		mailContent += "<p><b>Vigencia:</b> " + vigencia + "</p>";
		mailContent += "<p><b>Mensaje:</b> " + oferta.getDescripcion() + "</p>";
		mailContent += "<hr><img src='cid:logoImage' width='200'/>";
		
		try {
			helper.setTo(correosCliente);
			helper.setSubject(asunto);
			helper.setText(mailContent, true);
			
			ClassPathResource resource = new ClassPathResource("/static/img/LogoTexto.png");
			helper.addInline("logoImage", resource);
			
			if(!oferta.getFile().isEmpty()) {
				String fileName = StringUtils.cleanPath(oferta.getFile().getOriginalFilename());
				InputStreamSource source = new InputStreamSource() {
					
					@Override
					public InputStream getInputStream() throws IOException {
						return oferta.getFile().getInputStream();
					}
				};
				
				helper.addAttachment(fileName, source);
			}
			
			mailSender.send(message);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("oferta", oferta);
			model.addAttribute("titulo", "Compartir nueva oferta");
			return "/Views/SI/Fidelizacion/Ofertas/nuevaOferta";
		}
		
		oferta.setEnabled(true);
		oferta.setFechaRegistro(new Date());
		
		ofertaService.guardar(oferta);
		return "redirect:/oferta/compartir/success";
	}
	
	@GetMapping("/details/{idOferta}")
	public ResponseEntity<Oferta> details(@PathVariable("idOferta") Long idOferta) {
		try {
			return new ResponseEntity<Oferta>(ofertaService.buscarPorId(idOferta), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Oferta>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/compartir/success")
	public String success() {
		return "/Views/SI/Fidelizacion/Ofertas/successMessage";
	}
	
	@GetMapping("/delete/{idOferta}")
	public String delete(@PathVariable("idOferta") Long idOferta, RedirectAttributes attr) {
		Oferta oferta = null;
		
		if (idOferta > 0) {
			oferta = ofertaService.buscarPorId(idOferta);
			
			if (oferta ==  null) {
				attr.addFlashAttribute("error", "El id de la oferta que intenta eliminar no existe!");
				return "redirect:/oferta/";
			}
		} else {
			attr.addFlashAttribute("error", "El id de la oferta que intenta eliminar no existe!");
			return "redirect:/oferta/";
		}
		
		ofertaService.eliminar(idOferta);
		attr.addFlashAttribute("warning", "Oferta eliminada correctamente!");
		return "redirect:/oferta/";
	}
	
	@SuppressWarnings("deprecation")
	public boolean validarVigencia(Date fechaInicio, Date fechaFin) {
		Date fechaActual = new Date();
		
		fechaActual.setHours(0);
		fechaActual.setMinutes(0);
		fechaActual.setSeconds(0);
		
		fechaInicio.setHours(0);
		fechaInicio.setMinutes(0);
		fechaInicio.setSeconds(0);
		
		fechaFin.setHours(0);
		fechaFin.setMinutes(0);
		fechaFin.setSeconds(0);
		
		if (fechaInicio.after(fechaActual) && fechaInicio.before(fechaFin)) {
			return true;
		}
		
		return false;
	}


	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"ofertas.xlsx\"");
		Sheet hoja = workbook.createSheet("Ofertas");

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
		String[] columnas = { "ID", "Nombre", "Descripción", "Fecha de inicio", "Fecha de fin", "Fecha de registro",
				"Fecha de creación", "Estado"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.ofertasX == null) {
			ofertasX = ofertaService.listar();
		}

		for (Oferta oferta : this.ofertasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(oferta.getIdOferta());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(oferta.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(oferta.getDescripcion());
			hoja.autoSizeColumn(2);
			
			String fechaInicio = oferta.getFechaInicio().getDate()+"-"+(oferta.getFechaInicio().getMonth()+1)+"-"+(oferta.getFechaInicio().getYear()+1900);
			
			filaData.createCell(3).setCellValue(fechaInicio);
			hoja.autoSizeColumn(3);
			
			String fechaFin = oferta.getFechaFin().getDate()+"-"+(oferta.getFechaFin().getMonth()+1)+"-"+(oferta.getFechaFin().getYear()+1900);
			
			filaData.createCell(4).setCellValue(fechaFin);
			hoja.autoSizeColumn(4);
			
			String fechaRegistro = oferta.getFechaRegistro().getDate()+"-"+(oferta.getFechaRegistro().getMonth()+1)+"-"+(oferta.getFechaRegistro().getYear()+1900);
			
			filaData.createCell(5).setCellValue(fechaRegistro);
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(oferta.isEnabled());
			hoja.autoSizeColumn(6);
			numFila++;
		}
		
	}
}
