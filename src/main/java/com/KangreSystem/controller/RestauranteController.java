package com.KangreSystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.KangreSystem.models.entity.Restaurante;
import com.KangreSystem.models.service.IRestauranteService;
import com.KangreSystem.util.RestaurantesPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/restaurante")
@Component("/Views/Restaurante/restaurantes.xlsx")
public class RestauranteController extends AbstractXlsxView{
	
	private List<Restaurante> restaurantes;
	
	private List<Restaurante> restaurantesX;
	
	@Autowired
	private IRestauranteService restauranteServ;
	
	@GetMapping("/")
	public String listar(Model model) {
		this.restaurantes = restauranteServ.listar();
		model.addAttribute("restaurantes", this.restaurantes);
		model.addAttribute("titulo", "Restaurantes");
		return "/Views/Restaurante/restaurantes";
	}
	
	@PostMapping("/")
	public String filtrar(@Param("nombre") String nombre, @Param("ciudad") String ciudad, Model model) {
		
		if (nombre.isEmpty() && ciudad.isEmpty()) {
			this.restaurantesX = null; 
			model.addAttribute("warning", "No se encontro ningun criterio de busqueda!");
			return listar(model);
		} 
		
		if (!nombre.isEmpty() && !ciudad.isEmpty()){
			this.restaurantesX = null;
			model.addAttribute("error", "No se puede filtrar por ambos criterios de busqueda!");
			return listar(model);
		}
		
		if (!nombre.isEmpty() && ciudad.isEmpty()) {
			this.restaurantes = restauranteServ.buscarRestaurantesPorNombre(nombre);
			this.restaurantesX = this.restaurantes; 
			
			if (this.restaurantes.isEmpty()) {
				this.restaurantesX = null;
				model.addAttribute("error", "No se encontraron resultados!");
				return listar(model);
			}
			
			model.addAttribute("restaurantes", this.restaurantes);
			model.addAttribute("titulo", "Restaurantes");
			return "/Views/Restaurante/restaurantes";
		}
		
		
		this.restaurantes = restauranteServ.buscarPorCiudad(ciudad);
		this.restaurantesX = this.restaurantes;
		
		if (this.restaurantes.isEmpty()) {
			this.restaurantesX = null;
			model.addAttribute("error", "No se encontraron resultados!");
			return listar(model);
		}
		
		model.addAttribute("restaurantes", this.restaurantes);
		model.addAttribute("titulo", "Restaurantes");
		return "/Views/Restaurante/restaurantes";
	}
	
	@GetMapping("/limpiar")
	public String limpiar() {
		this.restaurantesX = null;
		return "redirect:/restaurante/";
	}
	
	@GetMapping("/agregar")
	public String agregar(Model model) {
		model.addAttribute("restaurante", new Restaurante());
		model.addAttribute("titulo", "Agregar restaurante");
		return "/Views/Restaurante/crearRestaurante";
	}
	
	@PostMapping("/agregar")
	public String agregar(@ModelAttribute Restaurante restaurante, RedirectAttributes attr,
			Model model) {
		Restaurante restauranteAux1; 
		
		if (restauranteServ.existePorNombre(restaurante.getNombre())) {
			restauranteAux1 = restauranteServ.buscarPorId(restaurante.getIdRestaurante());
			if (!restauranteAux1.getNombre().toUpperCase().equals(restaurante.getNombre().toUpperCase())) {
				model.addAttribute("error", "El restaurante ya se encuentra guardado!");
				model.addAttribute("titulo", "Agregar restaurante");
				model.addAttribute("restaurante", restaurante);
				return "/Views/Restaurante/crearRestaurante";
			}
		}
		
		restaurante.setFoto("restaurante.png");
		
		restauranteServ.guardar(restaurante);
		attr.addFlashAttribute("success", "Restaurante guardado correctamente!");
		return "redirect:/restaurante/";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long idRestaurante, RedirectAttributes attr, Model model) {
		Restaurante restaurante = null;
		
		if (idRestaurante > 0) {
			restaurante = restauranteServ.buscarPorId(idRestaurante);
			
			if (restaurante == null) {
				attr.addFlashAttribute("error", "El ID del producto a editar no existe!");
				return "redirect:/restaurante/";
			}
			
		} else {
			attr.addFlashAttribute("error", "El ID del producto a editar no existe!");
			return "redirect:/restaurante/";
		}
		
		model.addAttribute("restaurante", restaurante);
		model.addAttribute("titulo", "Editar restaurante");
		return "/Views/Restaurante/crearRestaurante";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long idRestaurante, RedirectAttributes attr) {
		
		if (idRestaurante > 0) {
			Restaurante restaurante = restauranteServ.buscarPorId(idRestaurante);
			
			if (restaurante == null) {
				attr.addFlashAttribute("error", "El ID del producto a eliminar no existe!");
				return "redirect:/restaurante/";
			}
		} else {
			attr.addFlashAttribute("error", "El ID del producto a eliminar no existe!");
			return "redirect:/restaurante/";
		}
		
		restauranteServ.eliminar(idRestaurante);
		attr.addFlashAttribute("warning", "Restaurante eliminado correctamente!");
		return "redirect:/restaurante/";
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Restaurantes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		List<Restaurante> listRestaurante = restauranteServ.listar();
		
		RestaurantesPDFExporter exporter = new RestaurantesPDFExporter(listRestaurante);
		exporter.export(response);
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"restaurantes.xlsx\"");
		Sheet hoja = workbook.createSheet("Restaurantes");

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
		String[] columnas = { "ID", "Nombre", "Dirección", "Telefono", "Celular", "Ciudad", "Horario"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.restaurantesX == null) {
			this.restaurantesX = restauranteServ.listar();
		}

		for (Restaurante restaurante : this.restaurantesX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(restaurante.getIdRestaurante());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(restaurante.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(restaurante.getDireccion());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(restaurante.getTelefono());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(restaurante.getCelular());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(restaurante.getCiudad());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(restaurante.getHorario());
			hoja.autoSizeColumn(6);
			numFila++;
		}
		
	}

}
