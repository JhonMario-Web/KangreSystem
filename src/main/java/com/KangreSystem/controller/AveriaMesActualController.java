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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import com.KangreSystem.models.entity.Averia;
import com.KangreSystem.models.service.IAveriaService;
import com.KangreSystem.util.AveriaMesActualPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario/averia/mes-actual")
@Component("/Views/SI/Inventario/Averias/averiasMesActual")
public class AveriaMesActualController extends AbstractXlsxView {
	
	@Autowired
	private IAveriaService averiaService;
	
	private List<Averia> averias = new ArrayList<>();
	
	private List<Averia> averiasX;
	
	@GetMapping("/")
	public String averiasMesActual(Model model) {
		this.averias = averiaService.averiasMesActual();		
		model.addAttribute("averia", new Averia());
		model.addAttribute("averias", this.averias);
		return "Views/SI/Inventario/Averias/averiasMesActual";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Averia averia, RedirectAttributes attr, Model model) {
		if (averia.getNumeroAveria().isEmpty() && averia.getFecha() == null) {
			this.averiasX = null;
			attr.addFlashAttribute("warning", "No se encontro ningun criterio de busqueda!");
			return "redirect:/inventario/averia/mes-actual/";
		} else if (!averia.getNumeroAveria().isEmpty() && averia.getFecha() != null) {
			this.averiasX = null;
			attr.addFlashAttribute("warning", "No se puede filtrar por ambos criterios de busqueda!");
			return "redirect:/inventario/averia/mes-actual/";
		} else if (!averia.getNumeroAveria().isEmpty() && averia.getFecha() == null) {
			
			if (averiaService.buscarPorNumeroAveria(averia.getNumeroAveria()) == null) {
				this.averiasX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return averiasMesActual(model);
			}
			this.averias = new ArrayList<>();
			this.averias.add(averiaService.buscarPorNumeroAveria(averia.getNumeroAveria()));
			this.averiasX = this.averias;
			
		} else if (averia.getNumeroAveria().isEmpty() && averia.getFecha() != null) {
			this.averias = averiaService.buscarPorFecha(averia.getFecha());
			this.averiasX = this.averias;
			
			if (this.averias.isEmpty()) {
				this.averiasX = null;
				model.addAttribute("warning", "No se encontraron resultados!");
				return averiasMesActual(model);
			}
		}
		
		model.addAttribute("averia", new Averia());
		model.addAttribute("averias", this.averias);
		return "Views/SI/Inventario/Averias/averiasMesActual";
	}
	
	@GetMapping("/limpiar")
	public String limpiarFiltro() {
		this.averiasX = null;
		return "redirect:/inventario/averia/mes-actual/";
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"averias_mes_actual.xlsx\"");
		Sheet hoja = workbook.createSheet("averias_mes_actual");

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
		String[] columnas = { "ID", "Numero averia", "Fecha", "Total", "Administrador"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.averiasX == null) {
			this.averiasX = averiaService.listar();
		}

		for (Averia averia : this.averiasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(averia.getIdAveria());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(averia.getNumeroAveria());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(averia.getFecha().toString());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue("$"+averia.getTotal());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(averia.getAdmin().getUser().getNombres()+" "+averia.getAdmin().getUser().getApellidos());
			hoja.autoSizeColumn(4);
			numFila++;
		}
	}

	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=AveriasMes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (averiasX == null) {
			AveriaMesActualPDFExporter exporter = new AveriaMesActualPDFExporter(averiaService.listar());
			exporter.export(response);
		}
		else {
			AveriaMesActualPDFExporter exporter = new AveriaMesActualPDFExporter(averiasX);
			exporter.export(response);
		}
		
	}
	
}
