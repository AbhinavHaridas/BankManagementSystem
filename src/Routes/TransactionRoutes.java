package Routes;

import java.sql.Connection;

import org.json.simple.JSONObject;

import Controllers.TransactionController;
import io.javalin.Javalin;

/**
 * Contains routes for transaction history table
 * 
 * @author Abhinav
 * @version 19/06/2024
 */
public class TransactionRoutes {
	/**
	 * Registers transaction history routes
	 * 
	 * @param app  javalin application
	 * @param conn connection to the database
	 */
	public static void register(Javalin app, Connection conn) {
		app.get("/api/getEntireTransactionHistory", ctx -> {
			JSONObject json = TransactionController.getEntireTransacationHistory(conn);
			ctx.json(json.toJSONString());
		});

		app.post("/api/searchTransaction", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = TransactionController.searchTransaction(conn, jsonString);
			ctx.json(json.toJSONString());
		});

		app.post("/api/getLatestTransaction", ctx -> {
			String jsonString = ctx.body();
			JSONObject json = TransactionController.getLatestTransaction(conn, jsonString);
			ctx.json(json.toJSONString());
		});
	}
}
