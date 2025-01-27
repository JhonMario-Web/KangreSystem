package com.KangreSystem.util;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.KangreSystem.models.entity.Salida;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SalidasMesActualPDFExporter {
	
	private List<Salida> listSalida;

	public SalidasMesActualPDFExporter(List<Salida> listSalida) {
		super();
		this.listSalida = listSalida;
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
		
		cell.setPhrase(new  Phrase("Tipo", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Fecha", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell.setPhrase(new  Phrase("Cantidad", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	@SuppressWarnings("deprecation")
	private void writeTableData(PdfPTable table) {
		for (Salida salida : listSalida) {
			table.addCell(String.valueOf(salida.getInsumo().getIdInsumo()));
			table.addCell(String.valueOf(salida.getInsumo()));
			table.addCell(String.valueOf(salida.getTipo()));
			String fecha = salida.getFecha().getDate()+"-"+(salida.getFecha().getMonth() + 1)+"-"+(salida.getFecha().getYear() + 1900);
			table.addCell(String.valueOf(fecha));
			table.addCell(String.valueOf(salida.getCantidad()));
		}
		
	}
	
	public void export(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		 HtmlWriter.getInstance(document, new FileOutputStream("images_wrong.html"));
         HtmlWriter writer = HtmlWriter.getInstance(document, new FileOutputStream("images_right.html"));
         writer.setImagepath("./");
		
		document.setPageSize(PageSize.LETTER.rotate());
		document.open();
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setColor(Color.BLACK);
		font.setSize(18);
		
		Paragraph title = new Paragraph("Salidas Mes Actual", font);

		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(15);
		table.setWidths(new float[] {0.3f, 1.0f, 1.0f, 0.5f, 0.3f});
		
		writeTableHeader(table);
		writeTableData(table);
		
		document.add(table);
		
		document.close();
	}

}
