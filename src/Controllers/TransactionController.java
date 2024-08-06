package Controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bankManagement.Utils;
import bankManagement.DbServices.TransactionHistory;
import bankManagement.DbServices.UserAccount;

/**
 * Contains controller methods form transaction history table
 * 
 * @author Abhinav
 * @version 17/06/2024
 */
public class TransactionController {
	/**
	 * Gets json for entire transaction history
	 * 
	 * @param conn connection to the db
	 * @return json which contains the required output
	 */
	public static JSONObject getEntireTransacationHistory(Connection conn) {
		try {
			ResultSet rs = TransactionHistory.getEntireTransacationHistory(conn);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
			metaData.put("table", "transactions");
			responseMap.put("details", metaData);
			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "txn_id,account_number,amount,transaction_type,txn_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}
			while (rs.next()) {
				LinkedHashMap<String, Object> transactionMap = new LinkedHashMap<>();
				transactionMap.put("txn_id", rs.getInt("txn_id"));
				transactionMap.put("account_number", rs.getString("account_number"));
				transactionMap.put("amount", rs.getString("amount"));
				transactionMap.put("transaction_type", rs.getString("transaction_type"));
				transactionMap.put("txn_timestamp", rs.getString("txn_timestamp"));
				array.add(transactionMap);
			}

			contentText = Utils.convertToText(array);
			responseMap.put("data", array);
			responseMap.put("text", contentText);
			return new JSONObject(responseMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
		}
		return null;
	}

	/**
	 * Gets json for entire transaction history
	 * 
	 * @param conn       connection to the db
	 * @param jsonString jsonString which is used to parse the requestObj
	 * @return json which contains the required output
	 */
	public static JSONObject searchTransaction(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			ResultSet rs;
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				rs = TransactionHistory.transactionsLikeAccountFieldWithDates(conn,
						(String) requestObj.get("field_name"), (String) requestObj.get("field_value"),
						(String) requestObj.get("start_date"), (String) requestObj.get("end_date"));
			} else {
				rs = TransactionHistory.transactionsLikeAccountField(conn, (String) requestObj.get("field_name"),
						(String) requestObj.get("field_value"));
			}
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				metaData.put("start_date", requestObj.get("start_date"));
				metaData.put("end_date", requestObj.get("end_date"));
			}
			metaData.put("table", "transactions");
			metaData.put("field_name", requestObj.get("field_name"));
			metaData.put("field_value", requestObj.get("field_value"));
			responseMap.put("details", metaData);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();

			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "txn_id,account_number,amount,transaction_type,txn_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}

			while (rs.next()) {
				LinkedHashMap<String, Object> transactionMap = new LinkedHashMap<>();
				transactionMap.put("txn_id", rs.getInt("txn_id"));
				transactionMap.put("account_number", rs.getString("account_number"));
				transactionMap.put("amount", rs.getString("amount"));
				transactionMap.put("transaction_type", rs.getString("transaction_type"));
				transactionMap.put("txn_timestamp", rs.getString("txn_timestamp"));
				array.add(transactionMap);
			}

			contentText = Utils.convertToText(array);
			responseMap.put("data", array);
			responseMap.put("text", contentText);
			return new JSONObject(responseMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an parsing exception" + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Gets latest transaction as json
	 * 
	 * @param conn       connection to the database
	 * @param jsonString user inputted json
	 * @return json which contains the latest transaction
	 */
	public static JSONObject getLatestTransaction(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			if (!UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				// validate account number
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Account number is not valid");
				return new JSONObject(errorMap);
			}
			ResultSet rs = TransactionHistory.getLatestTransaction(conn, (String) requestObj.get("account_number"));
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();

			while (rs.next()) {
				LinkedHashMap<String, Object> transactionMap = new LinkedHashMap<>();
				transactionMap.put("txn_id", rs.getInt("txn_id"));
				transactionMap.put("account_number", rs.getString("account_number"));
				transactionMap.put("amount", rs.getString("amount"));
				transactionMap.put("transaction_type", rs.getString("transaction_type"));
				transactionMap.put("txn_timestamp", rs.getString("txn_timestamp"));
				array.add(transactionMap);
			}

			json.put("data", array);
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		} catch (ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an parsing exception" + e.getMessage());
			return new JSONObject(errorMap);
		}
	}
}
