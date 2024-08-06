package bankManagement.AttachmentType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Contains PDF methods
 * 
 * @author Abhinav
 * @version 26/06/2024
 */
public class PDF extends AttachmentType {
	/**
	 * Constructs the pdf object
	 * 
	 * @param name       the name of the file
	 * @param content    the content of the file
	 * @param detailsMap contains metadata of the file
	 */
	public PDF(String name, String content, HashMap<String, Object> detailsMap) {
		super(name, content, detailsMap);
		this.file = new File(
				"C:\\Users\\Asus\\eclipse-workspace\\bankManagement\\resources\\Word\\" + this.name + ".pdf");
		this.createPDF(content);
	}

	/**
	 * Creates the pdf
	 * 
	 * @param content contents of the file
	 */
	public void createPDF(String content) {
		try {
			String[] contentArray = content.split("\n");
			Document document = new Document();
			FileOutputStream fout = new FileOutputStream(this.file);
			PdfWriter.getInstance(document, fout);
			String[] headers = contentArray[0].split(",");
			int columnLength = headers.length;
			document.open();
			document.add(new Paragraph("Table - " + this.detailsMap.get("table") + "\n"));
			if (this.detailsMap.containsKey("start_date") && this.detailsMap.containsKey("end_date")) {
				document.add(new Paragraph("Start date - " + this.detailsMap.get("start_date")));
				document.add(new Paragraph("End date - " + this.detailsMap.get("end_date")));
			}
			if (this.detailsMap.containsKey("field_name") && this.detailsMap.containsKey("field_value")) {
				document.add(new Paragraph("Field name - " + this.detailsMap.get("field_name")));
				document.add(new Paragraph("Field value - " + this.detailsMap.get("field_value")));
			}
			document.add(new Paragraph("\n"));
			PdfPTable table = new PdfPTable(columnLength);
			table.setWidthPercentage(100);
			this.addTable(table, headers, contentArray, document);
			document.add(table);
			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds the table to the pdf file
	 * 
	 * @param table        table where data is inserted
	 * @param headers      headers of the table
	 * @param contentArray the array which contains the content that is to be
	 *                     inserted in the table
	 * @param document     document which contains the table
	 */
	public void addTable(PdfPTable table, String[] headers, String[] contentArray, Document document) {
		// add header
		for (String headerName : headers) {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(headerName));
			table.addCell(header);
		}
		// rest of the cells
		if (contentArray.length > 1) {
			for (int i = 1; i < contentArray.length; i++) {
				String[] contentArrayCells = contentArray[i].split(",");
				for (String contentCell : contentArrayCells) {
					PdfPCell cell = new PdfPCell();
					cell.setBorderWidth(2);
					cell.setPhrase(new Phrase(contentCell));
					table.addCell(cell);
				}
			}
		}
	}
}
