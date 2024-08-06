package bankManagement.DbServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bankManagement.BankManagement;

/**
 * Class which contains DB methods to interact with user account table
 * 
 * @author Abhinav
 * @version 03/06/2024
 * 
 */
public class UserAccount {
	String firstName;
	String lastName;
	String username;
	private String nomineeFirstName;
	private String nomineeLastName;
	private String accountType;
	private String phoneNumber;
	String accountNumber;
	float balance = 0;

	static final Logger logger = LogManager.getLogger(BankManagement.class.getName());

	/**
	 * Constructor for user account
	 * 
	 * @param firstName        first name of account holder
	 * @param lastName         last name of account holder
	 * @param username         username which the account holder wants
	 * @param nomineeFirstName first name of the nominee of account holder
	 * @param nomineeLastName  last name of the nominee of account holder
	 * @param phoneNumber      phone number of the account holder
	 * @param accountType      type of account created
	 * @param accountNumber    12 digit unique identifier of an account
	 */
	public UserAccount(String firstName, String lastName, String username, String nomineeFirstName,
			String nomineeLastName, String phoneNumber, String accountType, String accountNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.nomineeFirstName = nomineeFirstName;
		this.nomineeLastName = nomineeLastName;
		this.phoneNumber = phoneNumber;
		this.accountType = accountType;
		this.accountNumber = accountNumber;
	}

	/**
	 * Creates a new user account
	 * 
	 * @param conn connection of db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return boolean boolean which tells whether the acount is created or not
	 */
	public boolean createAccount(Connection conn) throws SQLException {
		// checking if account exists or not
		ResultSet rs = getAccountByAccountNumber(conn, this.accountNumber);

		if (rs.isBeforeFirst()) {
			String errorMessage = "Sorry! an account with that account number already exists. choose another 12 digit account number";
			System.out.println(errorMessage);
			logger.error(errorMessage);
			return false;
		} else {
			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO useraccount (account_number, first_name, last_name, username, nominee_first_name, nominee_last_name, phone_number, account_type) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, this.accountNumber);
			ps.setString(2, this.firstName);
			ps.setString(3, this.lastName);
			ps.setString(4, this.username);
			ps.setString(5, this.nomineeFirstName);
			ps.setString(6, this.nomineeLastName);
			ps.setString(7, this.phoneNumber);
			ps.setString(8, this.accountType);
			System.out.println(ps.toString());
			logger.info(ps.toString());
			ps.executeUpdate();
			ps.close();
			String successMessage = "Inserted the account into database";
			System.out.println(successMessage);
			logger.info(successMessage);

			return true;
		}
	}

	/**
	 * updates account details
	 * 
	 * @param conn           Connection to the db
	 * @param userField      the field that the user user wants to update
	 * @param userFieldValue the value of the field that the user user wants to
	 *                       update
	 * @param accountNumber  account number where the field is to be updated
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return boolean boolean which tells the account field is updated or not
	 */
	public static boolean updateAccountFields(Connection conn, String userFieldValue, String accountNumber,
			String userField) throws SQLException {
		if (userField.equals("account_type") && !AccountType.isValidAccountType(conn, userFieldValue)) {
			// validate account type
			return false;
		}
		PreparedStatement ps = conn.prepareStatement("UPDATE useraccount\r\n" + "SET " + userField + " = ?,\r\n"
				+ " account_updation_timestamp = NOW()\r\n" + "WHERE account_number = ?");
		ps.setString(1, userFieldValue);
		ps.setString(2, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
		ps.close();
		System.out.println("Updated the required field in the database");
		logger.info("Updated the required field in the database");
		return true;
	}

	/**
	 * Retrieves all user accounts
	 * 
	 * @param conn connection of db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller ;of this method
	 * @return rs the effective result set which will contain all the fields
	 *         required
	 */
	public static ResultSet getAllAccounts(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		String query = "SELECT * FROM useraccount";
		System.out.println(query);
		logger.info(query);
		ResultSet rs = st.executeQuery(query);
		return rs;
	}

	/**
	 * Retrieves account by account number
	 * 
	 * @param conn          connection of db
	 * @param accountNumber account number of the account
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs the effective result set which will contain all the fields
	 *         required
	 */
	public static ResultSet getAccountByAccountNumber(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM useraccount WHERE account_number = ?");
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Displays all the different type of accounts that can be created
	 * 
	 * @param conn connection of db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs the effective result set which will contain all the fields
	 *         required
	 */
	public static ResultSet displayAllAccountTypes(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		String query = "SELECT DISTINCT account_type FROM useraccount";
		System.out.println(query);
		logger.info(query);
		ResultSet rs = st.executeQuery(query);
		return rs;
	}

	/**
	 * Deposits some amount into an account and creates a transaction
	 * 
	 * @param conn          connection of db
	 * @param amount        the amount which is deposited to the account
	 * @param accountNumber account number where the amount is to be deposited
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static void depositIntoAccount(Connection conn, float amount, String accountNumber) throws SQLException {
		PreparedStatement st = conn
				.prepareStatement("UPDATE useraccount SET balance = balance + ? WHERE account_number = ?");
		st.setDouble(1, amount);
		st.setString(2, accountNumber);
		System.out.println(st.toString());
		logger.info(st.toString());
		st.executeUpdate();
		TransactionHistory th = new TransactionHistory(accountNumber, "Deposit", amount);
		th.createTransaction(conn);
		System.out.println("Deposited amount into account");
		logger.info("Deposited amount into account");
	}

	/**
	 * Returns balance for an account
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber user inputed account number
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return balance balance which is present in user's bank account
	 */
	public static float getBalanceForAnAccount(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement st1 = conn.prepareStatement("SELECT balance FROM useraccount WHERE account_number = ?");
		st1.setString(1, accountNumber);
		System.out.println(st1.toString());
		logger.info(st1.toString());
		ResultSet rs = st1.executeQuery();
		rs.next();
		float balance = rs.getFloat("balance");
		return balance;
	}

	/**
	 * Withdraws some amount from an account and creates a transaction which is
	 * stored in transaction history table
	 * 
	 * @param conn          connection of db
	 * @param amount        the amount which is withdrawn to the account
	 * @param accountNumber account number of the account where the amount has to be
	 *                      withdrawn from
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return String string which tells the amount is withdrawn or not
	 */
	public static String withdrawFromAccount(Connection conn, float amount, String accountNumber) throws SQLException {
		float balance = getBalanceForAnAccount(conn, accountNumber);
		if (balance < amount) {
			System.out.println("You don't have that much amount in your bank account");
			logger.info("You don't have that much amount in your bank account");
			return "You don't have that much amount in your bank account";
		} else {
			PreparedStatement st = conn
					.prepareStatement("UPDATE useraccount SET balance = balance - ? WHERE account_number = ?");
			st.setDouble(1, amount);
			st.setString(2, accountNumber);
			System.out.println(st.toString());
			logger.info(st.toString());
			st.executeUpdate();
			System.out.println("Withdrew the required amount from the account");
			logger.info("Withdrew the required amount from the account");
			TransactionHistory th = new TransactionHistory(accountNumber, "Withdrawl", amount);
			th.createTransaction(conn);
			return "";
		}
	}

	/**
	 * Finds accounts which have the account number matching to the pattern user
	 * inputed
	 * 
	 * @param conn                 Connection to the db
	 * @param accountNumberPattern its a string pattern which will be matched to the
	 *                             different accountNumbers using an SQL "LIKE"
	 *                             query
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs Represents the accounts which match the pattern
	 */
	public static ResultSet accountsLikeAccountNumber(Connection conn, String accountNumberPattern)
			throws SQLException {
		String query = "SELECT * FROM useraccount WHERE account_number LIKE ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, "%" + accountNumberPattern + "%");
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Checks whether the account number is valid or not by doing retrieving
	 * accounts based on account number. If no accounts are retrieved then the
	 * account is not valid
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber user inputed account number
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return boolean true for valid and false for invalid
	 */
	public static boolean validAccountNumber(Connection conn, String accountNumber) throws SQLException {
		String query = "SELECT * FROM useraccount where account_number = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		if (!rs.isBeforeFirst())
			return false;
		return true;
	}

	/**
	 * Deletes the user account based on an account number
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber user inputed account number
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static void deleteUserAccount(Connection conn, String accountNumber) throws SQLException {
		String query = "DELETE FROM useraccount WHERE account_number = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
		System.out.println("deleted the account from the  database");
		logger.info("deleted the account from the database");
	}

	/**
	 * Finds accounts which have the account field matching to the pattern
	 * 
	 * @param conn              Connection to the db
	 * @param accountField      The account field that user wants to input
	 * @param fieldValuePattern the field value pattern that the search matches from
	 * @return rs Represents the accounts which match the pattern
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static ResultSet accountsLikeAccountField(Connection conn, String accountField, String fieldValuePattern)
			throws SQLException {
		String query = "SELECT * FROM useraccount WHERE " + accountField + " LIKE ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, "%" + fieldValuePattern + "%");
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Finds accounts which have the account field matching to the pattern user
	 * inputed with dates
	 * 
	 * @param conn              Connection to the db
	 * @param accountField      The account field that user wants to input
	 * @param fieldValuePattern the field value pattern that the search matches from
	 * @param startDate         the start date of the search
	 * @param endDate           the end date of the search
	 * @return rs Represents the accounts which match the pattern
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static ResultSet accountLikeAccountFieldWithDates(Connection conn, String accountField,
			String fieldValuePattern, String startDate, String endDate) throws SQLException {
		String query = "SELECT * FROM useraccount WHERE " + accountField
				+ " LIKE ? AND account_creation_timestamp BETWEEN ? AND ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, "%" + fieldValuePattern + "%");
		ps.setString(2, startDate);
		ps.setString(3, endDate);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}
}
