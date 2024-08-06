package Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bankManagement.Mail;
import bankManagement.AttachmentType.AttachmentType;
import bankManagement.AttachmentType.Excel;
import bankManagement.AttachmentType.PDF;
import bankManagement.AttachmentType.Word;
import io.javalin.http.Context;

/**
 * Contains controller methods for mail and download functionality
 * 
 * @author Abhinav
 * @version 25-06-2024
 */
public class MailAndDownloadController {
	/**
	 * Sends the mail
	 * 
	 * @param jsonString the request body
	 * @return json json which contains success key or error key
	 */
	public static JSONObject sendMail(String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			String contentsText = (String) requestObj.get("text");
			HashMap<String, Object> detailsMap = (HashMap<String, Object>) requestObj.get("details");
			Mail mail = new Mail("The contents are attached below", detailsMap.get("table") + "_details",
					(String) detailsMap.get("attachment_type"), contentsText, detailsMap);
			mail.mailRunner();
			HashMap<String, Object> successMap = new HashMap<>();
			successMap.put("success", "Send the mail");
			return new JSONObject(successMap);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			HashMap<String, Object> errorMap = new HashMap<>();
			errorMap.put("error", "There was a parse error");
			e.printStackTrace();
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Sends attachment as response
	 * 
	 * @param ctx        context to the javalin app
	 * @param jsonString the request body
	 */
	public static void downloadAttachment(Context ctx, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			String contentsText = (String) requestObj.get("text");
			HashMap<String, Object> detailsMap = (HashMap<String, Object>) requestObj.get("details");
			AttachmentType attachment = null;
			System.out.println((String) detailsMap.get("attachment_type"));
			if (((String) detailsMap.get("attachment_type")).equals("csv")) {
				System.out.println("Went here");
				attachment = new Excel(detailsMap.get("table") + "_details", contentsText, detailsMap);
			} else if (((String) detailsMap.get("attachment_type")).equals("pdf")) {
				System.out.println("Went here");
				attachment = new PDF(detailsMap.get("table") + "_details", contentsText, detailsMap);
			} else if (((String) detailsMap.get("attachment_type")).equals("docx")) {
				System.out.println("Went here");
				attachment = new Word(detailsMap.get("table") + "_details", contentsText, detailsMap);
			}
			if (attachment != null && attachment.sendAttachment() != null) {
				File file = attachment.sendAttachment();
				ctx.header("Content-Disposition", "attachment; filename=" + file.getName());
				ctx.header("Access-Control-Expose-Headers", "Content-Disposition");
				ctx.contentType(Files.probeContentType(file.toPath()));
				ctx.result(Files.readAllBytes(file.toPath()));
				// delete file (for clean up)
				if (file.delete()) {
					System.out.println("Deleted the file");
				} else {
					System.out.println("Could not delete the file");
				}
			} else {
				ctx.result("file was null");
			}
		} catch (ParseException e) {
			ctx.result(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ctx.result(e.getMessage());
			e.printStackTrace();
		}
	}
}
