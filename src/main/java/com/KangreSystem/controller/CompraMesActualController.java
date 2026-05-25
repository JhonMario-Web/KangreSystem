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
import com.KangreSystem.models.entity.Compra;
import com.KangreSystem.models.entity.Proveedor;
import com.KangreSystem.models.service.ICompraService;
import com.KangreSystem.models.service.IProveedorService;
import com.KangreSystem.util.ComprasMesActualPDFExporter;
import com.lowagie.text.DocumentException;

@Controller
@Secured("ROLE_ADMIN")
@Component("/Views/SI/Compra/comprasMesActual")
@RequestMapping("/proveedor/servicios/compra/mes-actual")
public class CompraMesActualController extends AbstractXlsxView{
	
	@Autowired
	private ICompraService compraService;
	
	@Autowired
	private IProveedorService proveedorService;
	
	private List<Compra> compras = new ArrayList<>();
	
	private List<Compra> comprasX;
	
	@GetMapping("/")
	public String comprasMesActual(Model model) {
		this.compras = compraService.comprasMesActual();
		model.addAttribute("compras", this.compras);
		model.addAttribute("compra", new Compra());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/comprasMesActual";
	}
	
	@PostMapping("/")
	public String filtrar(@ModelAttribute Compra compra, RedirectAttributes attr, Model model) {
		Proveedor proveedor = null;
		if (compra.getProveedor().getIdProveedor() != null) {
			proveedor = proveedorService.buscarPorId(compra.getProveedor().getIdProveedor());
		}
		compra.setProveedor(proveedor);
		
		if (compra.getIdCompra() != null && compra.getFecha() != null && compra.getProveedor() != null && !compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() != null && compra.getProveedor() == null && compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() != null && compra.getEstado().isEmpty()
				|| compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() == null && !compra.getEstado().isEmpty()) {
			this.comprasX = null;
			attr.addFlashAttribute("error", "No se puede filtrar por los filtros seleccionados!");
			return "redirect:/proveedor/servicios/compra/mes-actual/";
		} else if (compra.getIdCompra() == null && compra.getFecha() == null && compra.getProveedor() == null && compra.getEstado().isEmpty()) {
			this.comprasX = null;
			attr.addFlashAttribute("error", "No se encontraron criterios de busqueda!");
			return "redirect:/proveedor/servicios/compra/mes-actual/";
		} else if (compra.getIdCompra() != null && compra.getFecha() == null && compra.getProveedor() == null && compra.getEstado().isEmpty()) {
			this.compras = new ArrayList<Compra>();
			Compra c = compraService.buscarPorId(compra.getIdCompra());
			if (c == null) {
				this.comprasX = null;
				attr.addFlashAttribute("error", "No se encontraron resultados!");
				return "redirect:/proveedor/servicios/compra/mes-actual/";
			}
			this.compras.add(c);
			
			this.comprasX = this.compras;
			
			model.addAttribute("compras", this.compras);
			model.addAttribute("compra", new Compra());
			model.addAttribute("proveedores", proveedorService.listar());
			return "Views/SI/Compra/comprasMesActual";
		}
		
		this.compras = compraService.filtrar(compra.getFecha(), compra.getProveedor(), compra.getEstado());
		
		if (this.compras.isEmpty()) {
			this.comprasX = null;
			attr.addFlashAttribute("error", "No se encontraron resultados!");
			return "redirect:/proveedor/servicios/compra/mes-actual/";
		}
		
		this.comprasX = this.compras;
		
		model.addAttribute("compras", this.compras);
		model.addAttribute("compra", new Compra());
		model.addAttribute("proveedores", proveedorService.listar());
		return "Views/SI/Compra/compras";
	}
	
	@GetMapping("/limpiar-filtro")
	public String limpiarFiltro() {
		this.comprasX = null;
		return "redirect:/proveedor/servicios/compra/mes-actual/";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "attachment; filename=\"compras_mes_actual.xlsx\"");
		Sheet hoja = workbook.createSheet("compras_mes_actual");

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
		String[] columnas = { "ID", "Numero", "Proveedor", "Fecha", "Estado", "Total", "Administrador"};

		for (int i = 0; i < columnas.length; i++) {
			celda = filaData.createCell(i);
			celda.setCellValue(columnas[i]);
			celda.setCellStyle(style);
		}

		int numFila = 1;

		if (this.comprasX == null) {
			this.comprasX = compraService.listar();
		}

		for (Compra compra : this.comprasX) {
			filaData = hoja.createRow(numFila);

			filaData.createCell(0).setCellValue(compra.getIdCompra());
			hoja.autoSizeColumn(0);
			filaData.createCell(1).setCellValue(compra.getNumeroCompra());
			hoja.autoSizeColumn(1);
			filaData.createCell(2).setCellValue(compra.getProveedor().getNombre());
			hoja.autoSizeColumn(2);
			
			String fecha = compra.getFecha().getDate()+"-"+(compra.getFecha().getMonth() + 1)+"-"+(compra.getFecha().getYear() + 1900);
			
			filaData.createCell(3).setCellValue(fecha);
			hoja.autoSizeColumn(3);
			filaData.createCell(4).setCellValue(compra.getEstado());
			hoja.autoSizeColumn(4);
			filaData.createCell(5).setCellValue(compra.getTotal());
			hoja.autoSizeColumn(5);
			filaData.createCell(6).setCellValue(compra.getAdmin().getUser().getNombres()+" "+compra.getAdmin().getUser().getApellidos());
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
		String headerValue = "attachment; filename=ComprasMes_" + currentDateTime +".pdf";
		
		response.setHeader(headerKey, headerValue);
		
		if (comprasX == null) {
			ComprasMesActualPDFExporter exporter = new ComprasMesActualPDFExporter(compraService.listar());
			exporter.export(response);
		}
		else {
			ComprasMesActualPDFExporter exporter = new ComprasMesActualPDFExporter(comprasX);
			exporter.export(response);
		}
		
	}

}
