package Controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bankManagement.Utils;
import bankManagement.DbServices.AccountType;
import bankManagement.DbServices.TransactionHistory;
import bankManagement.DbServices.UserAccount;

/**
 * Contains controller methods for user account table
 * 
 * @author Abhinav
 * @version 17/06/2024
 */
public class UserAccountController {
	private static final String PHONE_NUMBER_PATTERN = "^[789][0-9]{9}$";

	private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

	/**
	 * Gets json for all the accounts available
	 * 
	 * @param conn connection to the db
	 * @return json which contains the required output
	 */
	public static JSONObject getAllAccounts(Connection conn) {
		try {
			ResultSet rs = UserAccount.getAllAccounts(conn);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
			metaData.put("table", "user_accounts");
			responseMap.put("details", metaData);
			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "account_number,first_name,last_name,username,phone_number,nominee_first_name,nominee_last_namee,account_type,balance,account_created_by,account_creation_timestamp,account_updation_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}

			while (rs.next()) {
				LinkedHashMap<String, Object> userAccountMap = new LinkedHashMap<>();
				userAccountMap.put("account_number", rs.getString("account_number"));
				userAccountMap.put("first_name", rs.getString("first_name"));
				userAccountMap.put("last_name", rs.getString("last_name"));
				userAccountMap.put("username", rs.getString("username"));
				userAccountMap.put("phone_number", rs.getString("phone_number"));
				userAccountMap.put("nominee_first_name", rs.getString("nominee_first_name"));
				userAccountMap.put("nominee_last_name", rs.getString("nominee_last_name"));
				userAccountMap.put("account_type", rs.getString("account_type"));
				userAccountMap.put("balance", rs.getString("balance"));
				userAccountMap.put("account_created_by", rs.getString("account_created_by"));
				userAccountMap.put("account_creation_timestamp", rs.getString("account_creation_timestamp"));
				userAccountMap.put("account_updation_timestamp", rs.getString("account_updation_timestamp"));
				array.add(userAccountMap);
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
	 * Checks whether the specified phone number is valid or not
	 * 
	 * @param phoneNumber phone number of the user
	 * @return matcher.matches A boolean (true | false) value
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		if (phoneNumber == null)
			return false;
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches();
	}

	/**
	 * Creates a new account
	 * 
	 * @param conn       Connection to the database
	 * @param jsonString user inputed json
	 * @return json which contains success/failure message
	 */
	public static JSONObject createAccount(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj;
			requestObj = (JSONObject) parser.parse(jsonString);

			String accountNumber = (String) requestObj.get("account_number");
			String phoneNumber = (String) requestObj.get("phone_number");
			String accountType = (String) requestObj.get("account_type");

			// validate account number
			if (accountNumber.length() != 12) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Account number should be 12 characters long. Re-enter it");
				return new JSONObject(errorMap);
			}
			// validate phone number
			else if (!isPhoneNumberValid(phoneNumber)) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Phone number is not valid it. Re-enter it");
				return new JSONObject(errorMap);
			}
			// validate account type
			else if (!AccountType.isValidAccountType(conn, accountType)) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "The account type is not valid");
				return new JSONObject(errorMap);
			} else {
				UserAccount account = new UserAccount((String) requestObj.get("first_name"),
						(String) requestObj.get("last_name"), (String) requestObj.get("username"),
						(String) requestObj.get("nominee_first_name"), (String) requestObj.get("nominee_last_name"),
						(String) requestObj.get("phone_number"), (String) requestObj.get("account_type"),
						(String) requestObj.get("account_number"));
				boolean didItSucceed = account.createAccount(conn);
				if (didItSucceed) {
					LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
					successMap.put("success", "Successfully added the new account");
					return new JSONObject(successMap);
				} else {
					LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
					errorMap.put("error",
							"Sorry! an account with that account number already exists. choose another 12 digit account number");
					return new JSONObject(errorMap);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Deposits into an account
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains success/failure message
	 */
	public static JSONObject depositIntoAccount(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj;
			requestObj = (JSONObject) parser.parse(jsonString);
			if (!UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				// validate account number
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Account number is not valid");
				return new JSONObject(errorMap);
			}
			UserAccount.depositIntoAccount(conn, Float.parseFloat((String) requestObj.get("amount")),
					(String) requestObj.get("account_number"));
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully created a deposition");
			return new JSONObject(successMap);
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception " + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Withdraws require amount from an account
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains success/failure message
	 */
	public static JSONObject withdrawFromAccount(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj;
			requestObj = (JSONObject) parser.parse(jsonString);
			if (!UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				// validate account number
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Account number is not valid");
				return new JSONObject(errorMap);
			}
			String error = UserAccount.withdrawFromAccount(conn, Float.parseFloat((String) requestObj.get("amount")),
					(String) requestObj.get("account_number"));
			System.out.println("error " + error);
			if (error.isEmpty()) {
				LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
				successMap.put("success", "Successfully created a withdrawl");
				return new JSONObject(successMap);
			} else {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("failure", error);
				return new JSONObject(errorMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("error", "There was an sql exception" + e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * Updates the account fields
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains success/failure message
	 */
	public static JSONObject updateAccountFields(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			boolean accountTypeSuccess = true;
			boolean phoneNumberSuccess = true;
			// validate account number
			if (!UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				System.out.println("Went here in phone number");
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "The account number does not exist");
				return new JSONObject(errorMap);
			}
			Set<Map.Entry<String, Object>> entries = requestObj.entrySet();
			for (Map.Entry<String, Object> entry : entries) {
				if (entry.getKey().equals("account_number"))
					continue;
				if (entry.getKey().equals("phone_number") && !isPhoneNumberValid((String) entry.getValue())) {
					// validate phone number
					System.out.println("Went here in phone number");
					LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
					errorMap.put("error", "Not valid phone number");
					return new JSONObject(errorMap);
				}
				accountTypeSuccess = UserAccount.updateAccountFields(conn, (String) entry.getValue(),
						(String) requestObj.get("account_number"), entry.getKey());
				if (!accountTypeSuccess)
					break;
			}
			if (!accountTypeSuccess) {
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Not valid account type");
				return new JSONObject(errorMap);
			}
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully updated the fields of account");
			return new JSONObject(successMap);
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

	/**
	 * Deletes an account
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains success/failure message
	 */
	public static JSONObject deleteAccount(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			if (!UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				// validate account number
				LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("error", "Account number is not valid");
				return new JSONObject(errorMap);
			}
			TransactionHistory.deleteTransactions(conn, (String) requestObj.get("account_number"));
			UserAccount.deleteUserAccount(conn, (String) requestObj.get("account_number"));
			LinkedHashMap<String, Object> successMap = new LinkedHashMap<>();
			successMap.put("success", "Successfully deleted the account");
			return new JSONObject(successMap);
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

	/**
	 * Gets balance for an account
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains balance for an account
	 */
	public static JSONObject getBalance(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			JSONObject json = new JSONObject();
			if (UserAccount.validAccountNumber(conn, (String) requestObj.get("account_number"))) {
				float balance = UserAccount.getBalanceForAnAccount(conn, (String) requestObj.get("account_number"));
				HashMap<String, Object> balanceMap = new HashMap<>();
				balanceMap.put("balance", balance);
				json.put("data", balanceMap);
				return json;
			} else {
				HashMap<String, Object> errorMap = new HashMap<>();
				errorMap.put("error", "The account number is not valid");
				return new JSONObject(errorMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			HashMap<String, Object> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return new JSONObject(errorMap);
		}
	}

	/**
	 * like search for an account
	 * 
	 * @param conn       connection to the db
	 * @param jsonString user inputed json
	 * @return json which contains accounts which match the search
	 */
	public static JSONObject searchAccount(Connection conn, String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonString);
			ResultSet rs;
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				rs = UserAccount.accountLikeAccountFieldWithDates(conn, (String) requestObj.get("field_name"),
						(String) requestObj.get("field_value"), (String) requestObj.get("start_date"),
						(String) requestObj.get("end_date"));
			} else {
				rs = UserAccount.accountsLikeAccountField(conn, (String) requestObj.get("field_name"),
						(String) requestObj.get("field_value"));
			}
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
			LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
			if (requestObj.containsKey("start_date") && requestObj.containsKey("end_date")) {
				metaData.put("start_date", requestObj.get("start_date"));
				metaData.put("end_date", requestObj.get("end_date"));
			}
			metaData.put("table", "user_accounts");
			metaData.put("field_name", requestObj.get("field_name"));
			metaData.put("field_value", requestObj.get("field_value"));
			responseMap.put("details", metaData);
			List<LinkedHashMap<String, Object>> array = new ArrayList<>();

			String contentText;

			if (!rs.isBeforeFirst()) {
				contentText = "account_number,first_name,last_name,username,phone_number,nominee_first_name,nominee_last_namee,account_type,balance,account_created_by,account_creation_timestamp,account_updation_timestamp";
				responseMap.put("data", array);
				responseMap.put("text", contentText);
				return new JSONObject(responseMap);
			}

			while (rs.next()) {
				System.out.println(rs.getString("account_number"));
				System.out.println(rs.getString("first_name"));
				System.out.println(rs.getString("last_name"));
				System.out.println(rs.getString("username"));
				System.out.println(rs.getString("phone_number"));
				System.out.println(rs.getString("nominee_first_name"));

				LinkedHashMap<String, Object> userAccountMap = new LinkedHashMap<>();
				userAccountMap.put("account_number", rs.getString("account_number"));
				userAccountMap.put("first_name", rs.getString("first_name"));
				userAccountMap.put("last_name", rs.getString("last_name"));
				userAccountMap.put("username", rs.getString("username"));
				userAccountMap.put("phone_number", rs.getString("phone_number"));
				userAccountMap.put("nominee_first_name", rs.getString("nominee_first_name"));
				userAccountMap.put("nominee_last_name", rs.getString("nominee_last_name"));
				userAccountMap.put("account_type", rs.getString("account_type"));
				userAccountMap.put("balance", rs.getString("balance"));
				userAccountMap.put("account_created_by", rs.getString("account_created_by"));
				userAccountMap.put("account_creation_timestamp", rs.getString("account_creation_timestamp"));
				userAccountMap.put("account_updation_timestamp", rs.getString("account_updation_timestamp"));
				array.add(userAccountMap);
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
