package com.KangreSystem.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.KangreSystem.models.entity.Averia;
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

public class AveriaMesActualPDFExporter {
	
	private List<Averia> listAveria;

	public AveriaMesActualPDFExporter(List<Averia> listAveria) {
		super();
		this.listAveria = listAveria;
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
		
		cell.setPhrase(new  Phrase("Numero", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Fecha", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Total", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Administrador", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	@SuppressWarnings("deprecation")
	private void writeTableData(PdfPTable table) {
		for (Averia averia : listAveria) {
			table.addCell(String.valueOf(averia.getIdAveria()));;
			table.addCell(String.valueOf(averia.getNumeroAveria()));
			String fecha = averia.getFecha().getDate()+"-"+(averia.getFecha().getMonth() + 1)+"-"+(averia.getFecha().getYear() + 1900);
			table.addCell(String.valueOf(fecha));
			table.addCell(String.valueOf("$ " + averia.getTotal()));
			table.addCell(String.valueOf(averia.getAdmin().getUser().getNombres() + averia.getAdmin().getUser().getApellidos()));
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
		
		Paragraph title = new Paragraph("Averias Mes Actual", font);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {0.3f, 1.0f, 0.5f, 1.0f, 1.5f});
		
		
		
		writeTableHeader(table);
		writeTableData(table);
		
		document.add(table);
		
		document.close();
	}


}
