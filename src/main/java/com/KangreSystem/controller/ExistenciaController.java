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
import com.KangreSystem.models.entity.Insumo;
import com.KangreSystem.models.service.ICategoriaInsumoService;
import com.KangreSystem.models.service.IInsumoService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.util.ExistenciasPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/inventario/existencias")
@Component("/Views/SI/Inventario/Existencias/existencias.xlsx")
public class ExistenciaController extends AbstractXlsxView {
	
	@Autowired
	private IInsumoService insumoService;
	
	@Autowired
	private ICategoriaInsumoService categoriaInsumoServ;
	
	@Autowired
	private IProveedorService proveedorService;
	
	private List<Insumo> existencias = new ArrayList<>();
	
	private List<Insumo> existenciasX;
	
	@GetMapping("/")
	public String existencias(Model model) {
		this.existencias = insumoService.listar();
		model.addAttribute("insumo", new Insumo());
		model.addAttribute("insumos", this.existencias);
		model.addAttribute("proveedores", proveedorService.listar());
		model.addAttribute("categorias", categoriaInsumoServ.listar());
		return "Views/SI/Inventario/Existencias/existencias";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Insumo insumo, Model model, RedirectAttributes attr) {
		
		if (!validFilter(insumo)) {
			this.existenciasX = null;
			attr.addFlashAttribute("error", "No se puede filtrar por los criterios seleccionados");
			return "redirect:/inventario/existencias/";
		} else if (!insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() != null && insumo.getProveedor().getIdProveedor() != null && !insumo.getEstado().isEmpty()) {
			this.existenciasX = null;
			attr.addFlashAttribute("error", "No se puede filtrar por todos los criterios");
			return "redirect:/inventario/existencias/";
		} else if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() == null && insumo.getProveedor().getIdProveedor() == null && insumo.getEstado().isEmpty()) {
			this.existenciasX = null;
			attr.addFlashAttribute("error", "No se encontraron criterios de busqueda!");
			return "redirect:/inventario/existencias/";
		}
		
		if (!insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() == null && insumo.getProveedor().getIdProveedor() == null && insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorNombreList(insumo.getNombre());
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() != null && insumo.getProveedor().getIdProveedor() == null && insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorCategoria(categoriaInsumoServ.buscarPorId(insumo.getCategoria().getIdCategoria()));
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() == null && insumo.getProveedor().getIdProveedor() != null && insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorProveedor(proveedorService.buscarPorId(insumo.getProveedor().getIdProveedor()));
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() == null && insumo.getProveedor().getIdProveedor() == null && !insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorEstado(insumo.getEstado());
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() != null && insumo.getProveedor().getIdProveedor() != null && insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorCategoriaProveedor(categoriaInsumoServ.buscarPorId(insumo.getCategoria().getIdCategoria()), proveedorService.buscarPorId(insumo.getProveedor().getIdProveedor()));
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() != null && insumo.getProveedor().getIdProveedor() == null && !insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorCategoriaEstado(categoriaInsumoServ.buscarPorId(insumo.getCategoria().getIdCategoria()), insumo.getEstado());
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() == null && insumo.getProveedor().getIdProveedor() != null && !insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorProveedorEstado(proveedorService.buscarPorId(insumo.getProveedor().getIdProveedor()), insumo.getEstado());
		}
		
		if (insumo.getNombre().isEmpty() && insumo.getCategoria().getIdCategoria() != null && insumo.getProveedor().getIdProveedor() != null && !insumo.getEstado().isEmpty()) {
			this.existencias = insumoService.buscarPorCategoriaProveedorEstado(categoriaInsumoServ.buscarPorId(insumo.getCategoria().getIdCategoria()), proveedorService.buscarPorId(insumo.getProveedor().getIdProveedor()), insumo.getEstado());
		}
		
		if (this.existencias.isEmpty()) {
			this.existenciasX = null;
			attr.addFlashAttribute("warning", "No se encontraron resultados de busqueda!");
			return "redirect:/inventario/existencias/";
		}
		
		this.existenciasX = this.existencias;
		
		model.addAttribute("insumo", new Insumo());
		model.addAttribute("insumos", this.existencias);
		model.addAttribute("proveedores", proveedorService.listar());
		model.addAttribute("categorias", categoriaInsumoServ.listar());
		return "Views/SI/Inventario/Existencias/existencias";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.existenciasX = null;
		return "redirect:/inventario/existencias/";
	}
	
	@GetMapping("/pdf")	
	public void exportPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Existencias_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (existenciasX == null) {
			ExistenciasPDFExporter exporter = new ExistenciasPDFExporter(insumoService.listar());
			exporter.export(response);
		}
		else {
			ExistenciasPDFExporter exporter = new ExistenciasPDFExporter(this.existenciasX);
			exporter.export(response);
		}
		
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=\"Existencias.xlsx\"");
		Sheet hoja = workbook.createSheet("Existencias");

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
		String[] columnas = { "ID", "Nombre", "Stock", "Stock Max", "Estado"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.existenciasX == null) {
			this.existenciasX = insumoService.listar();
		}

		for (Insumo insumo : this.existenciasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(insumo.getIdInsumo());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(insumo.getNombre());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(insumo.getStockActual());
			hoja.autoSizeColumn(2);
			filaData.createCell(3).setCellValue(insumo.getStockMax());
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(insumo.getEstado());
			hoja.autoSizeColumn(4);
			numFila++;
		}
	}
	
	public boolean validFilter(Insumo insumo) {
		if (!insumo.getNombre().isEmpty() && (insumo.getCategoria().getIdCategoria() != null || insumo.getProveedor().getIdProveedor() != null || !insumo.getEstado().isEmpty())) {
			return false; 
		}
		return true;
	}

}
