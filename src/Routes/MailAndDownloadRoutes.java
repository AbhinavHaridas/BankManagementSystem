package Routes;

import org.json.simple.JSONObject;

import Controllers.MailAndDownloadController;
import io.javalin.Javalin;

/**
 * Contains routes for mail and download functionality
 * 
 * @author Abhinav
 * @version 25/06/2024
 */
public class MailAndDownloadRoutes {
	/**
	 * Registers mail and download routes
	 * 
	 * @param app javalin application
	 */
	public static void register(Javalin app) {
		app.post("/api/sendMail", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = MailAndDownloadController.sendMail(jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/download", ctx -> {
			String jsonString = ctx.body();
			MailAndDownloadController.downloadAttachment(ctx, jsonString);
		});
	}
}
