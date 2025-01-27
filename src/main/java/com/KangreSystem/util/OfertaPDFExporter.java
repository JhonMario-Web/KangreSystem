package com.KangreSystem.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.KangreSystem.models.entity.Oferta;
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

public class OfertaPDFExporter {
	
	private List<Oferta> listOferta;

	public OfertaPDFExporter(List<Oferta> listOferta) {
		super();
		this.listOferta = listOferta;
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
		
		cell.setPhrase(new  Phrase("Fecha creación", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Fecha inicio", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Fecha fin", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	private void writeTableData(PdfPTable table) {
		for (Oferta oferta : listOferta) {
			table.addCell(String.valueOf(oferta.getIdOferta()));
			table.addCell(oferta.getNombre());
			table.addCell(String.valueOf(oferta.getFechaRegistro()));
			table.addCell(String.valueOf(oferta.getFechaInicio()));
			table.addCell(String.valueOf(oferta.getFechaFin()));
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
		
		Paragraph title = new Paragraph("Lista de ofertas", font);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {0.3f, 2.0f, 1.0f, 1.0f, 1.0f});
		
		writeTableHeader(table);
		writeTableData(table);
		
		document.add(table);
		
		document.close();
	}

}
