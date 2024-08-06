package bankManagement.AttachmentType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

//import java.io.FileOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * Contains methods for Word type of attachment
 * 
 * @version 26/06/2025
 */
public class Word extends AttachmentType {
	/**
	 * Constructs the Word object
	 * 
	 * @param name       name of the file
	 * @param content    contents of the file
	 * @param detailsMap contains the metadata for the file
	 */
	public Word(String name, String content, HashMap<String, Object> detailsMap) {
		// create the document
		super(name, content, detailsMap);
		this.file = new File(
				"C:\\Users\\Asus\\eclipse-workspace\\bankManagement\\resources\\Word\\" + this.name + ".docx");
		this.createTable();
	}

	/**
	 * sets the color of the table
	 * 
	 * @param table table where the color is being set
	 */
	public void setTableColor(XWPFTable table) {
		table.setInsideHBorder(XWPFBorderType.THICK, 5, 5, "000000");
		table.setInsideVBorder(XWPFBorderType.THICK, 5, 5, "000000");
		table.setLeftBorder(XWPFBorderType.THICK, 5, 5, "000000");
		table.setRightBorder(XWPFBorderType.THICK, 5, 5, "000000");
		table.setBottomBorder(XWPFBorderType.THICK, 5, 5, "000000");
		table.setTopBorder(XWPFBorderType.THICK, 5, 5, "000000");
	}

	/**
	 * Creates the table
	 */
	public void createTable() {
		try {
			FileOutputStream out = new FileOutputStream(this.file);
			String[] textArray = content.split("\n");
			XWPFDocument document = new XWPFDocument();
			XWPFParagraph headerParagraph = document.createParagraph();
			XWPFRun headerRun = headerParagraph.createRun();
			headerRun.setText("table - " + this.detailsMap.get("table"));
			headerRun.addCarriageReturn();
			if (this.detailsMap.containsKey("start_date") && this.detailsMap.containsKey("end_date")) {
				headerRun.setText("Start date - " + this.detailsMap.get("start_date"));
				headerRun.addCarriageReturn();
				headerRun.setText("End date - " + this.detailsMap.get("end_date"));
				headerRun.addCarriageReturn();
			}

			if (this.detailsMap.containsKey("field_name") && this.detailsMap.containsKey("field_value")) {
				headerRun.setText("Field name - " + this.detailsMap.get("field_name"));
				headerRun.addCarriageReturn();
				headerRun.setText("Field value - " + this.detailsMap.get("field_value"));
				headerRun.addCarriageReturn();
			}

			headerRun.addCarriageReturn();

			XWPFTable table = document.createTable();
			setTableColor(table);
			// filling header row
			XWPFTableRow headerRow = table.getRow(0);
			String[] textArrayRow0 = textArray[0].split(",");
			headerRow.getCell(0).setText(textArrayRow0[0]);
			for (int i = 1; i < textArrayRow0.length; i++) {
				headerRow.addNewTableCell().setText(textArrayRow0[i]);
			}

			// filling rest of rows
			if (textArray.length > 1) {
				for (int i = 1; i < textArray.length; i++) {
					XWPFTableRow row = table.createRow();
					String[] textArrayRow = textArray[i].split(",");
					row.getCell(0).setText(textArrayRow[0]);
					if (textArrayRow.length > 1) {
						for (int j = 1; j < textArrayRow.length; j++) {
							if (row.getCell(j) == null) {
								row.addNewTableCell();
							}
							row.getCell(j).setText(textArrayRow[j]);
						}
					}
				}
			}

			document.write(out);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
