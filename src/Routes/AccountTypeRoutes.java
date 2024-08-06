package Routes;

import java.sql.Connection;

import org.json.simple.JSONObject;

import Controllers.AccountTypeController;
import io.javalin.Javalin;

/**
 * Contains routes for account type table
 * 
 * @author Abhinav
 * @version 19/06/2024
 */
public class AccountTypeRoutes {
	/**
	 * Registers account type routes
	 * 
	 * @param app  javalin application
	 * @param conn connection to the database
	 */
	public static void register(Javalin app, Connection conn) {
		app.get("/api/getAllAccountTypes", ctx -> {
			JSONObject json = AccountTypeController.getAllAccountTypes(conn);
			ctx.json(json.toJSONString());
		});

		app.post("/api/createNewAccountType", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = AccountTypeController.createNewAccountType(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.put("/api/updateAccountType", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = AccountTypeController.updateAccountType(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.delete("/api/deleteAccountType", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = AccountTypeController.deleteAccountType(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/searchAccountType", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = AccountTypeController.searchAccountType(conn, jsonString);
			ctx.json(json.toJSONString());
		});
	}
}
