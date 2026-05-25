package com.KangreSystem.controller;

import java.util.ArrayList;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Administrador;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IAdminService;
import com.KangreSystem.models.service.IUserService;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
@Component("/Views/SI/Admin/administradores.xlsx")
public class AdminController extends AbstractXlsxView{
	
	@Autowired
	private IAdminService adminService;
	
	@Autowired
	private IUserService userService;
	
	private List<Administrador> admins;
	
	private List<Administrador> adminsX;
	
	@GetMapping("/")
	public String listar(Model model) {
		admins = adminService.listar();
		model.addAttribute("titulo", "Administradores");
		model.addAttribute("admins", admins);
		return "Views/SI/Admin/administradores";
	}
	
	@PostMapping("/")
	public String buscar(@RequestParam(value = "username", required = false) String username, Model model) {
		List<User> users = null;
		
		if (username.isEmpty()) {
			adminsX = null;
			model.addAttribute("warning", "No se encontraron criterios de busqueda!");
			return listar(model);
		}
		
		users = userService.buscarUsuariosPorNumeroDoc(username);
		
		if (users.isEmpty()) {
			adminsX = null;
			model.addAttribute("error", "No se encontraron resultados!");
			return listar(model);
		}
		
		for (User user : users) {
			if (adminService.existePorUser(user)) {
				admins = new ArrayList<>();
				Administrador admin = adminService.buscarPorUser(user);
				admins.add(admin);
			} else {
				adminsX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
		}
		
		adminsX = admins; 
		model.addAttribute("titulo", "Administradores");
		model.addAttribute("admins", admins);
		return "Views/SI/Admin/administradores";
	}
	
	@GetMapping("/limpiar")
	public String limpiar() {
		adminsX = null;
		return "redirect:/admin/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"administradores.xlsx\"");
		Sheet hoja = workbook.createSheet("Administradores");

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
		String[] columnas = { "ID", "Username", "Nombres", "Apellidos", "Email", "Fecha de registro" };

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (adminsX == null) {
			adminsX = adminService.listar();
		}

		for (Administrador admin : adminsX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(admin.getIdAdmin());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(admin.getUser().getUsername());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(admin.getUser().getNombres());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(admin.getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(admin.getUser().getEmail());
			hoja.autoSizeColumn(4);
			
			String fechaRegistro = admin.getFechaRegistro().getDate()+"-"+(admin.getFechaRegistro().getMonth() + 1)+"-"+(admin.getFechaRegistro().getYear() + 1900);
			
			filaData.createCell(5).setCellValue(fechaRegistro);
			hoja.autoSizeColumn(5);
			numFila++;
		}
	}
}
