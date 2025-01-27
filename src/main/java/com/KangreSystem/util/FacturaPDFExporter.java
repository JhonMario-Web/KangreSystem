package com.KangreSystem.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.KangreSystem.models.entity.DetallePedido;
import com.KangreSystem.models.entity.Pedido;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FacturaPDFExporter {
	
	
	private List<DetallePedido> detalles;
	
	private Pedido pedido;
	
	private Long iva;

	
	
	public FacturaPDFExporter(List<DetallePedido> detalles, Pedido pedido, Long iva) {
		super();
		this.detalles = detalles;
		this.pedido = pedido;
		this.iva = iva;
	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.BLACK);
		
		
		cell.setPhrase(new Phrase("ID", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Producto", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Precio", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Sub total", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	private void writeTableData(PdfPTable table) {
		for (DetallePedido detalle : detalles) {
			table.addCell(String.valueOf(detalle.getProducto().getIdProducto()));
			table.addCell(String.valueOf(detalle.getProducto().getNombre()));
			table.addCell(String.valueOf(detalle.getProducto().getPrecio()));
			table.addCell(String.valueOf(detalle.getSubtotal()));
		}
		
	}
	
	public void export(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setColor(Color.BLACK);
		font.setSize(18);
		
		Paragraph title = new Paragraph("LA HAMBURGUESERIA | KANGRESYSTEM", font);
		Image jpg = Image.getInstance("C:\\Users\\Public\\KangreSystem\\KangreSystem\\KangreSystem-springboot\\src\\main\\resources\\static\\img\\LogoSimboloNegro.png");
		document.add(jpg);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {1.0f, 1.0f, 1.0f, 1.0f});
		
		writeTableHeader(table);
		writeTableData(table);
		
		document.add(table);
		
		document.close();
	}

	public List<DetallePedido> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetallePedido> detalles) {
		this.detalles = detalles;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Long getIva() {
		return iva;
	}

	public void setIva(Long iva) {
		this.iva = iva;
	}

	
	
}
