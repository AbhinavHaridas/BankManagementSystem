package Controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bankManagement.Utils;
import bankManagement.DbServices.AccountType;

/**
 * contains controller methods for account type table
 * 
 * @author Abhinav
 * @version 17/06/2024
 */
public class AccountTypeController {

	/**
	 * Gets json for all types of account
	 * 
	 * @param conn connection to the db
	 * @return json which contains the required output
	 */
	public static JSONObject getAllAccountTypes(Connection conn) {
		try {
			ResultSet rs = AccountType.getAllAccountTypes(conn);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();

			metaData.put("table", "account_type");
			responseMap.put("details", metaData);
			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "id,account_type,account_type_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}

			while (rs.next()) {
				LinkedHashMap<String, Object> accountTypesMap = new LinkedHashMap<>();
				accountTypesMap.put("id", rs.getInt("id"));
				accountTypesMap.put("account_type", rs.getString("account_type"));
				accountTypesMap.put("account_type_timestamp", rs.getString("account_type_timestamp"));
				array.add(accountTypesMap);
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
		}
	}

	/**
	 * Creates a new account and sends the success json
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputted json
	 * @return json which contains the required success/failure message
	 */
	public static JSONObject createNewAccountType(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj;
			requestObj = (JSONObject) parser.parse(jsonString);
			if (AccountType.isValidAccountType(conn, (String) requestObj.get("account_type"))) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "That account type already exists");
				return new JSONObject(errorMap);
			}
			AccountType newType = new AccountType((String) requestObj.get("account_type"));
			newType.createNewAccountType(conn);
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully added the new account type");
			return new JSONObject(successMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception " + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an parsing error " + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Updates account type
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputted json
	 * @return json which contains the required success/failure message
	 */
	public static JSONObject updateAccountType(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			// validate account id
			if (!AccountType.validAccountTypeID(conn, Integer.parseInt((String) requestObj.get("id")))) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "there is no account type with that id");
				return new JSONObject(errorMap);
			}
			// check if the account type already exists or not
			if (AccountType.isValidAccountType(conn, (String) requestObj.get("account_type"))) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "That account type already exists");
				return new JSONObject(errorMap);
			}
			AccountType.updateAccountType(conn, Integer.parseInt((String) requestObj.get("id")),
					(String) requestObj.get("account_type"));
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully updated new account type");
			return new JSONObject(successMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception " + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was a parsing error " + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Deletes account type
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputted json
	 * @return json which contains the success/failure message
	 */
	public static JSONObject deleteAccountType(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			if (!AccountType.isValidAccountType(conn, (String) requestObj.get("account_type"))) {
				// validate account type
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "That account type does not exist");
				return new JSONObject(errorMap);
			}
			AccountType.deleteAccountType(conn, (String) requestObj.get("account_type"));
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully deleted the account type");
			return new JSONObject(successMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception " + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was a parsing error " + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Does a like search for account type
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputted json
	 * @return json which contains the success/failure message
	 */
	public static JSONObject searchAccountType(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			ResultSet rs;
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				rs = AccountType.accountLikeAccoutTypeFieldWithDates(conn, (String) requestObj.get("field_name"),
						(String) requestObj.get("field_value"), (String) requestObj.get("start_date"),
						(String) requestObj.get("end_date"));
			} else {
				rs = AccountType.accountsLikeAccountTypeField(conn, (String) requestObj.get("field_name"),
						(String) requestObj.get("field_value"));
			}
			JSONObject json = new JSONObject();
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				metaData.put("start_date", requestObj.get("start_date"));
				metaData.put("end_date", requestObj.get("end_date"));
			}
			metaData.put("table", "account_type");
			metaData.put("field_name", requestObj.get("field_name"));
			metaData.put("field_value", requestObj.get("field_value"));
			responseMap.put("details", metaData);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();

			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "id,account_type,account_type_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}

			while (rs.next()) {
				LinkedHashMap<String, Object> accountTypesMap = new LinkedHashMap<>();
				accountTypesMap.put("id", rs.getInt("id"));
				accountTypesMap.put("account_type", rs.getString("account_type"));
				accountTypesMap.put("account_type_timestamp", rs.getString("account_type_timestamp"));
				array.add(accountTypesMap);
			}

			contentText = Utils.convertToText(array);
			responseMap.put("data", array);
			responseMap.put("text", contentText);
			return new JSONObject(responseMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception " + e.getMessage());
			return new JSONObject(errorMap);
		} catch (ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was a parse exception " + e.getMessage());
			return new JSONObject(errorMap);
		}
	}
}
