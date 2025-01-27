package com.KangreSystem.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import com.KangreSystem.models.entity.Salida;
import com.KangreSystem.models.service.ISalidaService;
import com.KangreSystem.util.SalidasMesActualPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario/movimientos/salidas/mes-actual")
@Component("/Views/SI/Inventario/Movimientos/Salida/salidasMesActual")
public class SalidaMesActualController extends AbstractXlsxView {
	
	@Autowired
	private ISalidaService salidaService;
	
	private List<Salida> salidas = new ArrayList<>();
	
	private List<Salida> salidasX;
	
	@GetMapping("/")
	public String salidasMesActual(Model model) {
		this.salidas = salidaService.salidasMesActual();
		model.addAttribute("salidas", this.salidas);
		model.addAttribute("salida", new Salida());
		return "/Views/SI/Inventario/Movimientos/Salida/salidasMesActual";
	}
	
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Salida salida, RedirectAttributes attr, Model model) {
		
		if (salida.getTipo().isEmpty() && salida.getFecha() == null) {
			this.salidasX = null;
			attr.addFlashAttribute("warning", "No se ha selecciona ningun criterio de busqueda!");
			return "redirect:/inventario/movimientos/salidas/mes-actual/";
		} else if (!salida.getTipo().isEmpty() && salida.getFecha() != null) {
			this.salidas = salidaService.buscarPorTipoFecha(salida.getTipo(), salida.getFecha());
		} else if (!salida.getTipo().isEmpty() && salida.getFecha() == null) {
			this.salidas = salidaService.buscarPorTipo(salida.getTipo());
		} else if (salida.getTipo().isEmpty() && salida.getFecha() != null) {
			this.salidas = salidaService.buscarPorFecha(salida.getFecha());
		}
		
		if (this.salidas.isEmpty()) {
			this.salidasX = null;
			attr.addFlashAttribute("warning", "No se han encontrado resultados de busqueda!");
			return "redirect:/inventario/movimientos/salidas/mes-actual/";
		}
		
		this.salidasX = this.salidas;
		model.addAttribute("salidas", this.salidas);
		model.addAttribute("salida", new Salida());
		return "/Views/SI/Inventario/Movimientos/Salida/salidasMesActual";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.salidasX = null;
		return "redirect:/inventario/movimientos/salidas/mes-actual/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"salidas_mes_actual.xlsx\"");
		Sheet hoja = workbook.createSheet("salidas_mes_actual");
		
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
		String[] columnas = { "ID Insumo", "Insumo", "Tipo", "Fecha", "Hora", "Cantidad"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.salidasX == null) {
			this.salidasX = salidaService.listar();
		}

		for (Salida salida : this.salidasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(salida.getInsumo().getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(salida.getInsumo().getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(salida.getTipo());
			hoja.autoSizeColumn(2);
			
			String fecha = salida.getFecha().getDate()+"-"+(salida.getFecha().getMonth()+1)+"-"+(salida.getFecha().getYear()+1900);
			
			filaData.createCell(3).setCellValue(fecha);
			hoja.autoSizeColumn(3);
			
			String hora = salida.getHora().getHours()+":"+salida.getHora().getMinutes();
			
			filaData.createCell(4).setCellValue(hora);
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(salida.getCantidad());
			hoja.autoSizeColumn(5);
			numFila++;
		}
		
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Salidas-Mes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (salidasX == null) {
			SalidasMesActualPDFExporter exporter = new SalidasMesActualPDFExporter(salidaService.listar());
			exporter.export(response);
		}
		else {
			SalidasMesActualPDFExporter exporter = new SalidasMesActualPDFExporter(salidasX);
			exporter.export(response);
		}
		
	} 

}
