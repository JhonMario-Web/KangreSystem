package com.KangreSystem.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.KangreSystem.models.entity.Entrada;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class EntradasPDFExporter {
	
	private List<Entrada> listEntrada;

	public EntradasPDFExporter(List<Entrada> listEntrada) {
		super();
		this.listEntrada = listEntrada;
	}
	
	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.RED);
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);
		
		
		cell.setPhrase(new Phrase("ID", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Nombre", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Compra", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Factura", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	private void writeTableData(PdfPTable table) {
		for (Entrada entrada : listEntrada) {
			table.addCell(String.valueOf(entrada.getInsumo().getIdInsumo()));
			table.addCell(String.valueOf(entrada.getInsumo().getNombre()));
			table.addCell(String.valueOf(entrada.getIdEntrada()));
			table.addCell(String.valueOf(entrada.getLlegada().getNumeroFactura()));
			table.addCell(String.valueOf(entrada.getFecha()));
			table.addCell(String.valueOf(entrada.getCantidad()));
		}
		
	}
	
	public void export(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.setPageSize(PageSize.LETTER.rotate());
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setColor(Color.BLACK);
		font.setSize(18);
		
		Paragraph title = new Paragraph("Lista de proveedores", font);

		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {1.5f, 3.0f, 3.0f, 1.5f});
		
		writeTableHeader(table);
		writeTableData(table);
		
		document.add(table);
		
		document.close();
	}

}
