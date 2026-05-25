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
import com.KangreSystem.models.entity.Entrada;
import com.KangreSystem.models.entity.LlegadaCompra;
import com.KangreSystem.models.service.IEntradaService;
import com.KangreSystem.util.EntradasMesActualPDFExporter;
import com.lowagie.text.DocumentException;
import com.KangreSystem.models.service.ILlegadaCompraServ;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario/movimientos/entradas/mes-actual")
@Component("/Views/SI/Inventario/Movimientos/Entrada/entradasMesActual")
public class EntradaMesActualController extends AbstractXlsxView{
	
	@Autowired
	private IEntradaService entradaService;
	
	@Autowired
	private ILlegadaCompraServ llegadaService;
	
	private List<Entrada> entradas = new ArrayList<>();
	
	private List<Entrada> entradasX;
	
	@GetMapping("/")
	public String entradasMesActual(Model model) {
		this.entradas = entradaService.entradasMesActual();
		model.addAttribute("entradas", this.entradas);
		model.addAttribute("entrada", new Entrada());
		return "Views/SI/Inventario/Movimientos/Entrada/entradasMesActual";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Entrada entrada, Model model, RedirectAttributes attr) {
		LlegadaCompra llegada = null;
		
		if (entrada.getLlegada().getNumeroFactura().isEmpty() && entrada.getFecha() == null) {
			this.entradasX = null;
			attr.addFlashAttribute("warning", "No se encontro ningun criterio de busqueda!");
			return "redirect:/inventario/movimientos/entradas/";
		} else if (!entrada.getLlegada().getNumeroFactura().isEmpty() && entrada.getFecha() != null) {
			this.entradasX = null;
			attr.addFlashAttribute("error", "No se filtrar por ambos criterios de busqueda!");
			return "redirect:/inventario/movimientos/entradas/";
		} else if (!entrada.getLlegada().getNumeroFactura().isEmpty() && entrada.getFecha() == null) {
			llegada = llegadaService.buscarPorNumeroFactura(entrada.getLlegada().getNumeroFactura());
			
			if (llegada == null) {
				this.entradasX = null;
				attr.addFlashAttribute("error", "No se ha encontrado ninguna llegada con el numero de factura ingresado!");
				return "redirect:/inventario/movimientos/entradas/";
			}
			
			this.entradas = entradaService.buscarPorLlegada(llegada);
			this.entradasX = this.entradas;
			
			if (this.entradas.isEmpty()) {
				this.entradasX = null;
				attr.addFlashAttribute("error", "No se han encontrado resultados!");
				return "redirect:/inventario/movimientos/entradas/";
			}
			
			model.addAttribute("entradas", this.entradas);
			model.addAttribute("entrada", new Entrada());
			return "Views/SI/Inventario/Movimientos/Entrada/entradas";
		}
		
		this.entradas = entradaService.buscarPorFecha(entrada.getFecha());	
		this.entradasX = this.entradas;
		
		if (this.entradas.isEmpty()) {
			this.entradasX = null;
			attr.addFlashAttribute("error", "No se han encontrado resultados!");
			return "redirect:/inventario/movimientos/entradas/";
		}
		
		model.addAttribute("entradas", this.entradas);
		model.addAttribute("entrada", new Entrada());
		return "Views/SI/Inventario/Movimientos/Entrada/entradas";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.entradasX = null;
		return "redirect:/inventario/movimientos/entradas/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"entradas_mes_actual.xlsx\"");
		Sheet hoja = workbook.createSheet("entradas_mes_actual");
		
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
		String[] columnas = { "ID Insumo", "Insumo", "Compra", "Factura", "Fecha", "Hora", "Cantidad"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.entradasX == null) {
			this.entradasX = entradaService.listar();
		}

		for (Entrada entrada : this.entradasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(entrada.getInsumo().getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(entrada.getInsumo().getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(entrada.getLlegada().getCompra().getNumeroCompra());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(entrada.getLlegada().getNumeroFactura());
			hoja.autoSizeColumn(3);
			
			String fecha = entrada.getFecha().getDate()+"-"+(entrada.getFecha().getMonth()+1)+"-"+(entrada.getFecha().getYear()+1900);
			
			filaData.createCell(4).setCellValue(fecha);
			hoja.autoSizeColumn(4);
			
			String hora = entrada.getHora().getHours()+":"+entrada.getHora().getMinutes();
			
			filaData.createCell(5).setCellValue(hora);
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(entrada.getCantidad());
			hoja.autoSizeColumn(6);
			numFila++;
		}
		
	}
	
	@GetMapping("/pdf")	
	public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Entradas-Mes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (entradasX == null) {
			EntradasMesActualPDFExporter exporter = new EntradasMesActualPDFExporter(entradaService.listar());
			exporter.export(response);
		}
		
		else {
			EntradasMesActualPDFExporter exporter = new EntradasMesActualPDFExporter(entradasX);
			exporter.export(response);
		}
		
	}

}
