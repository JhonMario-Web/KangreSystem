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
import com.KangreSystem.models.entity.Empleado;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IEmpleadoService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.EmpleadoPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/empleado")
@Component("/Views/SI/Empleado/empleados.xlsx")
public class EmpleadoController extends AbstractXlsxView {

	@Autowired
	private IEmpleadoService empleadoService;

	@Autowired
	private IUserService userService;
	
	private List<Empleado> empleados;
	
	private List<Empleado> empleadosX;

	@GetMapping("/")
	public String listar(Model model) {
		empleados = empleadoService.listar();
		model.addAttribute("empleados", empleados);
		model.addAttribute("titulo", "Empleados");
		return "Views/SI/Empleado/empleados";
	}

	@PostMapping("/")
	public String buscar(@RequestParam(value = "username", required = false) String username, Model model) {
		List<User> users = null;

		if (username.isEmpty()) {
			empleadosX = null;
			model.addAttribute("warning", "No hubo criterio de busqueda");
			return listar(model);
		}

		users = userService.buscarUsuariosPorNumeroDoc(username);
		
		System.out.println("Usuarios: "+users);

		if (users.isEmpty()) {
			empleadosX = null;
			model.addAttribute("warning", "No se encontraron resultados");
			return listar(model);
		}
		
		for (User user : users) {
			if (empleadoService.existePorUser(user)) {
				empleados = new ArrayList<>();
				Empleado empleado = empleadoService.buscarPorUser(user);
				empleados.add(empleado);
			} else {
				empleadosX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return listar(model);
			}
		}
		
		System.out.println("Empleados: "+empleados);
		
		empleadosX = empleados;
		model.addAttribute("empleados", empleados);
		model.addAttribute("titulo", "Empleados");
		return "Views/SI/Empleado/empleados";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Empleados_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (empleadosX == null) {
			EmpleadoPDFExporter exporter = new EmpleadoPDFExporter(empleadoService.listar());
			exporter.export(response);
		}
		else {
			EmpleadoPDFExporter exporter = new EmpleadoPDFExporter(this.empleadosX);
			exporter.export(response);
		}
		
		
	}

	@GetMapping("/limpiar")
	public String limpiar() {
		empleadosX = null;
		return "redirect:/empleado/";
	}

	@GetMapping("/asignar-sueldo")
	public String crear(Model model) {
		model.addAttribute("empleado", new Empleado());
		model.addAttribute("titulo", "Asignar sueldo");
		return "Views/SI/Empleado/asignarSueldo";
	}

	@PostMapping("/asignar-sueldo")
	public String crear(@ModelAttribute Empleado empleado) {
		empleadoService.guardar(empleado);
		return "redirect:/empleado/";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long idEmpleado, Model model) {
		model.addAttribute("empleado", empleadoService.buscarPorId(idEmpleado));
		return "Views/SI/Empleado/asignarSueldo";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idEmpleado, RedirectAttributes attr) {
		empleadoService.eliminar(idEmpleado);
		attr.addFlashAttribute("success", "Empleado eliminado correctamente!");
		return "redirect:/empleado/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"empleados.xlsx\"");
		Sheet hoja = workbook.createSheet("Empleados");

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
		String[] columnas = { "ID", "Username", "Nombres", "Apellidos", "Email", "Sueldo", "Fecha de registro" };

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (empleadosX == null) {
			empleadosX = empleadoService.listar();
		}

		for (Empleado empleado : empleadosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(empleado.getIdEmpleado());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(empleado.getUser().getUsername());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(empleado.getUser().getNombres());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(empleado.getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(empleado.getUser().getEmail());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(empleado.getSueldo());
			hoja.autoSizeColumn(5);
			
			String fecha = empleado.getFechaRegistro().getDate()+"-"+(empleado.getFechaRegistro().getMonth() + 1)+"-"+(empleado.getFechaRegistro().getYear() + 1900);
			
			filaData.createCell(6).setCellValue(fecha);
			hoja.autoSizeColumn(6);
			numFila++;
		}
	}
}
