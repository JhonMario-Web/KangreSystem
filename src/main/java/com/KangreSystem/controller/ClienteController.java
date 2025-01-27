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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Cliente;
import com.KangreSystem.models.entity.User;
import com.KangreSystem.models.service.IClienteService;
import com.KangreSystem.models.service.IUserService;
import com.KangreSystem.util.ClientePDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/cliente")
@Secured("ROLE_ADMIN")
@Component("/Views/SI/Cliente/clientes.xlsx")
public class ClienteController extends AbstractXlsxView{
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUserService userService;
	
	private List<Cliente> clientes;
	
	private List<Cliente> clientesX;
	
	
	@Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
	@GetMapping("/")
	public String listar(Model model) {
		clientes = clienteService.listar();
		model.addAttribute("titulo", "Clientes");
		model.addAttribute("clientes", clientes);
		return "Views/SI/Cliente/clientes";
	}
	
	@PostMapping("/")
	public String buscar(@Param("username") String username, Model model) {
		List<User> users = null;
		
		if (username.isEmpty()) {
			model.addAttribute("error", "No se encontraron criterios de busqueda!");
			clientesX = null;
			return listar(model);
		}
		
		users = userService.buscarUsuariosPorNumeroDoc(username);
		System.out.println("USERS: "+users);
		System.out.println("USERS_SIZE: "+users.size());
		
		if (!users.isEmpty()) {
			for (User user : users) {
				clientes = new ArrayList<>();
				Cliente cliente = clienteService.buscarPorUser(user);
				clientes.add(cliente);
			}
		}
		
		if (clientes.isEmpty()) {
			model.addAttribute("error", "No se encontraron resultados!");
			clientesX = null;
			return listar(model);
		}
		System.out.println("CLIENTES: "+clientes);
		clientesX = clientes;
		model.addAttribute("clientes", clientes);
		model.addAttribute("titulo", "Clientes");
		return "Views/SI/Cliente/clientes";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Clientes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (clientesX == null) {
			ClientePDFExporter exporter = new ClientePDFExporter(clienteService.listar());
			exporter.export(response);
		}
		else {
			ClientePDFExporter exporter = new ClientePDFExporter(clientesX);
			exporter.export(response);
		}
		
		
	}
	
	@GetMapping("/limpiar")
	public String limpiar() {
		clientesX = null;
		return "redirect:/cliente/";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/delete/{id}")
	public String delete() {
		return "";
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"clientes.xlsx\"");
		Sheet hoja = workbook.createSheet("Clientes");
		
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
		String[] columnas = { "ID", "Username", "Nombres", "Apellidos", "Email", "Kangrepuntos"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (clientesX == null) {
			clientesX = clienteService.listar();
		}

		for (Cliente cliente : clientesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(cliente.getIdCliente());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(cliente.getUser().getUsername());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(cliente.getUser().getNombres());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(cliente.getUser().getApellidos());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(cliente.getUser().getEmail());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(cliente.getKangrepuntos());
			hoja.autoSizeColumn(5);
			numFila++;
		}
	}
}
