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
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.Rol;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IEmpleadoService;
import com.KangreSystem.models.service.IRolService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.RolPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/rol")
@Component("/Views/SI/Rol/roles.xlsx")
public class RolController extends AbstractXlsxView{
	
	@Autowired
	private IRolService rolService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEmpleadoService empleadoService;
	
	private List<Rol> roles;
	
	private List<Rol> rolesX;
	
	@GetMapping("/")
	public String listar(Model model) {
		model.addAttribute("listaRoles", getRolesForFilter());
		model.addAttribute("roles", new ArrayList<>());
		model.addAttribute("rol", new Rol());
		model.addAttribute("titulo", "Roles y permisos");
		model.addAttribute("tabla", "d-none");
		return "/Views/SI/Rol/roles";
	}
	
	@PostMapping("/")
	public String buscar(@Param("username") String username, @Param("rol") String rol, Model model) {
		User user = null;
		
		if (!username.isEmpty() && !rol.isEmpty()) {
			rolesX = null;
			model.addAttribute("warning", "No se puede filtrar por ambos campos!");
			return listar(model);
		} else if (username.isEmpty() && rol.isEmpty()) {
			rolesX = null;
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			return listar(model);
		}
		
		if (!username.isEmpty() && rol.isEmpty()) {
			user = userService.buscarPorNumeroDoc(username);
			roles = rolService.buscarRolesPorUser(user);
			rolesX = roles;
			
			if (roles.isEmpty()) {
				rolesX = null;
				model.addAttribute("warning", "No se encontraron resultados, puede que el username ingresado no esta escrito correctamente!, Recuerda escribirlo completo.");
				return listar(model);
			}
			
			model.addAttribute("titulo", "Roles y permisos");
			model.addAttribute("roles", roles);
			model.addAttribute("tabla", "show");
			model.addAttribute("listaRoles", getRolesForFilter());
			return "/Views/SI/Rol/roles";
		}
		System.out.println("ROLES: "+roles);
		roles = rolService.buscarPorRol(rol);
		rolesX = roles;
		
		if (roles.isEmpty()) {
			rolesX = null;
			model.addAttribute("warning", "No se encontraron resultados!");
			return listar(model);
		}
		
		model.addAttribute("titulo", "Roles y permisos");
		model.addAttribute("roles", roles);
		model.addAttribute("tabla", "show");
		model.addAttribute("listaRoles", getRolesForFilter());
		return "/Views/SI/Rol/roles";
	}

	@SuppressWarnings("null")
	@GetMapping("/agregar/{id}")
	public String agregarRol(@PathVariable("id") Long idUser, RedirectAttributes attr, Model model) {
		User user =  null;
		Empleado empleado = null;
		
		if (idUser > 0) {
			user = userService.buscarPorId(idUser);
			
			if (user == null) {
				System.out.println("El id del usuario no existe");
				attr.addFlashAttribute("error", "El id del usuario a agregar no existe!");
				return "redirect:/user/";
			}
		} else {
			System.out.println("El id del producto no existe");
			attr.addFlashAttribute("error", "El id del usuario a agregar no existe!");
			return "redirect:/user/";
		}
		
	    
		Rol rol = userToRol(user);
		
		if (getRoles(user) == null) {
			attr.addFlashAttribute("info", "El usuario ya tiene todos permisos");
			return "redirect:/user/"; 
		}
		
		empleado = empleadoService.buscarPorUser(user);
		
		if (empleadoService.existePorUser(user)) {
			model.addAttribute("sueldo", empleado.getSueldo());
		} else {
			model.addAttribute("sueldo", "0");
		}
		
		model.addAttribute("rol", rol);
		model.addAttribute("titulo", "Agregar rol");
		model.addAttribute("roles", getRoles(user));
		idUser = null;
		return "/Views/SI/Rol/asignarRol";
	}

	@PostMapping("/asignar-rol")
	public String asignarRol(@ModelAttribute Rol rol, RedirectAttributes attribute,
			@Param("sueldo") String sueldo) {
		
		rolService.guardar(rol, sueldo);
		attribute.addFlashAttribute("success", "Rol agregado correctamente!");
		return "redirect:/user/";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idRol, RedirectAttributes attribute) {
		rolService.eliminar(idRol);
		attribute.addFlashAttribute("success", "Rol eliminado correctamente!");
		return "redirect:/rol/";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Roles_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (rolesX == null) {
			RolPDFExporter exporter = new RolPDFExporter(rolService.listar());
			exporter.export(response);
		}
		else {
			RolPDFExporter exporter = new RolPDFExporter(rolesX);
			exporter.export(response);
		}
	}
	
	@GetMapping("/limpiar")
	public String limpiar(Model model) {
		rolesX = null;
		return listar(model);
	}
	
	protected Rol userToRol(User user) {
		Rol rol = new Rol();
		rol.setUser(user);
		return rol;
	}
	
	protected List<String> getRoles(User user){
		List<String> roles = new ArrayList<>();
		
		boolean adminExists = false;
		boolean cajeroExists = false;
		boolean cocineroExists = false;
		boolean meseroExists = false;
		
		if (rolService.existePorUserRol(user, "ROLE_ADMIN")) {
			adminExists = true;
		}
		if (rolService.existePorUserRol(user, "ROLE_CAJERO")) {
			cajeroExists = true;
		}
		if (rolService.existePorUserRol(user, "ROLE_COCINERO")) {
			cocineroExists = true;
		}
		if (rolService.existePorUserRol(user, "ROLE_MESERO")) {
			meseroExists = true;
		}
		
		if (adminExists && cajeroExists && cocineroExists && meseroExists) {
			return null;
		}
		if (!adminExists && cajeroExists && cocineroExists && meseroExists) {
			roles.add("ROLE_ADMIN");
			return roles;
		}
		if (adminExists && !cajeroExists && cocineroExists && meseroExists) {
			roles.add("ROLE_CAJERO");
			return roles;
		}
		if (adminExists && cajeroExists && !cocineroExists && meseroExists) {
			roles.add("ROLE_COCINERO");
			return roles;
		}
		if (adminExists && cajeroExists && cocineroExists && !meseroExists) {
			roles.add("ROLE_MESERO");
			return roles;
		}
		if (!adminExists && !cajeroExists && cocineroExists && meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_CAJERO");
			return roles;
		}
		if (adminExists && cajeroExists && !cocineroExists && !meseroExists) {
			roles.add("ROLE_MESERO");
			roles.add("ROLE_COCINERO");
			return roles;
		}
		if (!adminExists && cajeroExists && cocineroExists && !meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_MESERO");
			return roles;
		}
		if (adminExists && !cajeroExists && !cocineroExists && meseroExists) {
			roles.add("ROLE_COCINERO");
			roles.add("ROLE_CAJERO");
			return roles;
		}
		if (!adminExists && cajeroExists && !cocineroExists && meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_COCINERO");
			return roles;
		}
		if (adminExists && !cajeroExists && cocineroExists && !meseroExists) {
			roles.add("ROLE_CAJERO");
			roles.add("ROLE_MESERO");
			return roles;
		}
		if (!adminExists && cajeroExists && !cocineroExists && !meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_COCINERO");
			roles.add("ROLE_MESERO");
			return roles;
		}
		if (adminExists && !cajeroExists && !cocineroExists && !meseroExists) {
			roles.add("ROLE_CAJERO");
			roles.add("ROLE_COCINERO");
			roles.add("ROLE_MESERO");
			return roles;
		}
		if (!adminExists && !cajeroExists && !cocineroExists && meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_CAJERO");
			roles.add("ROLE_COCINERO");
			return roles;
		}
		if (!adminExists && !cajeroExists && cocineroExists && !meseroExists) {
			roles.add("ROLE_ADMIN");
			roles.add("ROLE_CAJERO");
			roles.add("ROLE_MESERO");
			return roles;
		}
		
		roles.add("ROLE_ADMIN");
		roles.add("ROLE_CAJERO");
		roles.add("ROLE_COCINERO");
		roles.add("ROLE_MESERO");
		return roles;
	}
	
	public List<String> getRolesForFilter(){
		List<String> listaRoles = new ArrayList<>();
		listaRoles.add("ROLE_ADMIN");
		listaRoles.add("ROLE_CAJERO");
		listaRoles.add("ROLE_COCINERO");
		listaRoles.add("ROLE_MESERO");
		return listaRoles;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"roles.xlsx\"");
		Sheet hoja = workbook.createSheet("Roles");
		
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
		String[] columnas = { "ID", "Username", "Nombres", "Apellidos", "Email", "Rol", "Fecha de registro"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (rolesX == null) {
			rolesX = rolService.listar();
		}

		for (Rol rol : rolesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(rol.getIdRol());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(rol.getUser().getUsername());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(rol.getUser().getNombres());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(rol.getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(rol.getUser().getEmail());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(rol.getRol());
			hoja.autoSizeColumn(5);
			
			String fechaRegistro = rol.getFechaRegistro().getDate()+"-"+(rol.getFechaRegistro().getMonth() + 1)+"-"+(rol.getFechaRegistro().getYear() + 1900);
			
			filaData.createCell(6).setCellValue(fechaRegistro);
			hoja.autoSizeColumn(6);
			numFila++;
		}
	}
	
	
}
