package bankManagement.AttachmentType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Contains methods for Excel attachment
 * 
 * @author Abhinav
 * @version 26/06/2024
 */
public class Excel extends AttachmentType {
	/**
	 * Constructs the excel object
	 * 
	 * @param name       name of the file
	 * @param content    content of the file
	 * @param detailsMap contains the metadata of the file
	 */
	public Excel(String name, String content, HashMap<String, Object> detailsMap) {
		super(name, content, detailsMap);
		this.createFile();
		this.writeToFile();
	}

	/**
	 * Creates the file
	 */
	public void createFile() {
		try {
			this.file = new File(
					"C:\\Users\\Asus\\eclipse-workspace\\bankManagement\\resources\\Word\\" + this.name + ".csv");
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Writes to the file
	 */
	public void writeToFile() {
		FileWriter filewriter;
		try {
			filewriter = new FileWriter(
					"C:\\Users\\Asus\\eclipse-workspace\\bankManagement\\resources\\Word\\" + this.name + ".csv");
			filewriter.write("table - " + (String) detailsMap.get("table") + "\n");
			if (detailsMap.containsKey("start_date") && detailsMap.containsKey("end_date")) {
				filewriter.write("start date - " + (String) detailsMap.get("start_date") + "\n");
				filewriter.write("end date - " + (String) detailsMap.get("end_date") + "\n");
			}
			if (detailsMap.containsKey("field_name") && detailsMap.containsKey("field_value")) {
				filewriter.write("field name - " + detailsMap.get("field_name") + "\n");
				filewriter.write("field value - " + detailsMap.get("field_value") + "\n");
			}
			filewriter.write("\n");
			filewriter.write(this.content);
			filewriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
