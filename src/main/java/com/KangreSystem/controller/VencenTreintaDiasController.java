package com.KangreSystem.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.KangreSystem.models.entity.VencimientoLote;
import com.KangreSystem.models.service.IVencimientoLoteServ;
import com.KangreSystem.util.VenceTreintaDiasPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario/vencimiento/treinta-dias")
@Component("/Views/SI/Inventario/Vencimiento/vencenEnTreintaDias")
public class VencenTreintaDiasController extends AbstractXlsxView {
	
	@Autowired
	private IVencimientoLoteServ vencimientoService;
	
	private List<VencimientoLote> vencimientos = new ArrayList<>();
	
	private List<VencimientoLote> vencimientosX;
	
	@GetMapping("/")
	public String vencenEnTreintaDias(Model model) {
		model.addAttribute("vencimientos", vencimientoService.vencenEnTreintaDias());
		model.addAttribute("vencimiento", new VencimientoLote());
		return "/Views/SI/Inventario/Vencimiento/vencenEnTreintaDias";
	}
	
	@PostMapping("/")
	private String filtrar(@ModelAttribute VencimientoLote vencimiento, Model model, RedirectAttributes attr) {
		
		if (vencimiento.getInsumo().getIdInsumo() == null && vencimiento.getLote().isEmpty() && vencimiento.getFecha() == null) {
			this.vencimientosX = null;
			attr.addFlashAttribute("warning", "No se ha seleccionado ningun criterio de busqueda!");
			return "redirect:/inventario/vencimiento/treinta-dias/";
		} 
		
		this.vencimientos = vencimientoService.filtrar(vencimiento);
		
		if (this.vencimientos.isEmpty()) {
			this.vencimientosX = null;
			attr.addFlashAttribute("error", "No se encontraron resultados!");
			return "redirect:/inventario/vencimiento/treinta-dias/";
		}
		
		this.vencimientos = vencimientoService.vencimientosDisponibles(this.vencimientos);
		this.vencimientosX = this.vencimientos;
		
		model.addAttribute("vencimiento", new VencimientoLote());
		model.addAttribute("vencimientos", this.vencimientos);
		return "/Views/SI/Inventario/Vencimiento/vencenEnTreintaDias";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.vencimientosX = null;
		return "redirect:/inventario/vencimiento/treinta-dias/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"vencen_en_30_dias.xlsx\"");
		Sheet hoja = workbook.createSheet("vencen_en_30_dias");
		
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
		String[] columnas = { "ID Insumo", "Insumo", "Llegada", "Fecha vencimiento", "Lote", "Cantidad"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.vencimientosX == null) {
			this.vencimientosX = vencimientoService.listar();
		}

		for (VencimientoLote vencimiento : this.vencimientosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(vencimiento.getInsumo().getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(vencimiento.getInsumo().getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(vencimiento.getLlegada().getIdLlegada());
			hoja.autoSizeColumn(2);
			
			String fecha = vencimiento.getFecha().getDate()+"-"+(vencimiento.getFecha().getMonth()+1)+"-"+(vencimiento.getFecha().getYear()+1900);
			
			filaData.createCell(3).setCellValue(fecha);
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(vencimiento.getLote());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(vencimiento.getCantidad());
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
		String headerValue = "attachment; filename=Vencimientos_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (vencimientosX == null) {
			VenceTreintaDiasPDFExporter exporter = new VenceTreintaDiasPDFExporter(vencimientoService.listar());
			exporter.export(response);
		}
		else {
			VenceTreintaDiasPDFExporter exporter = new VenceTreintaDiasPDFExporter(vencimientosX);
			exporter.export(response);
		}
		
	}


}
