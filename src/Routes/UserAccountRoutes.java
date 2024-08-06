package Routes;

import java.sql.Connection;

import org.json.simple.JSONObject;

import Controllers.UserAccountController;
import io.javalin.Javalin;

/**
 * Contains routes for user account table
 * 
 * @author Abhinav
 * @version 19/06/2024
 */
public class UserAccountRoutes {
	/**
	 * Registers user accounts routes
	 * 
	 * @param app  javalin application
	 * @param conn connection to the database
	 */
	public static void register(Javalin app, Connection conn) {
		app.get("/api/getAllAccounts", ctx -> {
			JSONObject json = UserAccountController.getAllAccounts(conn);
			ctx.json(json.toJSONString());
		});

		app.post("/api/getBalance", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.getBalance(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/createNewAccount", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.createAccount(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/depositIntoAccount", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.depositIntoAccount(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/withdrawFromAccount", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.withdrawFromAccount(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.put("/api/updateAccountFields", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.updateAccountFields(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.delete("/api/deleteAccount", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.deleteAccount(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/searchAccount", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = UserAccountController.searchAccount(conn, jsonString);
			ctx.json(json.toJSONString());
		});

	}
}
