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

import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.repository.InsumoRepository;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.util.MovimientosPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario")
@Component("/Views/SI/Inventario/Movimientos/movimientos.xlsx")
public class MovimientosController extends AbstractXlsxView {
	
	@Autowired
	private IInsumoService insumoService; 
	
	@Autowired
	private InsumoRepository insumoRepo;
	
	private List<Insumo> movimientosX;
	
	
	@GetMapping("/movimientos")
	public String movimientos(Model model) {
		model.addAttribute("insumos", insumoService.listar());
		return "/Views/SI/Inventario/Movimientos/movimientos";
	}
	
	@GetMapping("/movimientos/ajustar/{id}")
	public String ajustarMovimientos(@PathVariable("id") Long idInsumo, RedirectAttributes attr, Model model) {
		Insumo insumo = null;
		
	if (idInsumo > 0) {
		insumo = insumoService.buscarPorId(idInsumo);
		
		if (insumo == null) {
			attr.addFlashAttribute("error", "El ID del insumo a editar no existe!");
			return "redirect:/inventario/existencias";
		}
		
	} else {
		attr.addFlashAttribute("error", "El ID del insumo a editar no existe!");
		return "redirect:/inventario/existencias";
	}
		
		model.addAttribute("insumo", insumo);
		return "/Views/SI/Inventario/Movimientos/ajustarMovimientos";
	}
	
	@PostMapping("/movimientos/ajustar")
	public String ajustarMovimientos(@ModelAttribute Insumo insumo, RedirectAttributes attr) {
		insumoRepo.save(insumo);
		attr.addFlashAttribute("success", "Insumo ajustado correctamente!");
		return "redirect:/inventario/movimientos";
	}
	
	@GetMapping("/movimientos/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Movimientos_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		List<Insumo> listMovimientos = insumoService.listar();
		
		MovimientosPDFExporter exporter = new MovimientosPDFExporter(listMovimientos);
		exporter.export(response);
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=\"Movimientos.xlsx\"");
		Sheet hoja = workbook.createSheet("Movimientos");

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
		String[] columnas = { "ID", "Nombre", "Stock", "Stock Max"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (movimientosX == null) {
			movimientosX = insumoService.listar();
		}

		for (Insumo insumo : movimientosX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(insumo.getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(insumo.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(insumo.getStockActual());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(insumo.getStockMax());
			hoja.autoSizeColumn(3);
			numFila++;
		}
	}

}
