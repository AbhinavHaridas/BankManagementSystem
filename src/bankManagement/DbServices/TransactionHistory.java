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
 * Class which contains DB methods to interact with transaction history table
 * 
 * @author Abhinav
 * @version 05/06/2024
 */
public class TransactionHistory {
	String accountNumber;
	String transactionType;
	float amount;

	static final Logger logger = LogManager.getLogger(BankManagement.class.getName());

	/**
	 * Constructor for Transaction History
	 * 
	 * @param accountNumber   the account number (unique) of the account
	 * @param transactionType type of transaction
	 * @param amount          the amount that was transacted
	 */
	TransactionHistory(String accountNumber, String transactionType, float amount) {
		this.accountNumber = accountNumber;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	/**
	 * Creates a brand new transaction based on object parameters
	 * 
	 * @param conn Connection to the db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public void createTransaction(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO Transaction_history (account_number, transaction_type, amount) VALUES (?, ?, ?) ");
		ps.setString(1, this.accountNumber);
		ps.setString(2, this.transactionType);
		ps.setFloat(3, this.amount);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
		System.out.println("Created a transaction to the database");
		logger.info("Created a transaction to the database");
	}

	/**
	 * Gets the latest transaction of any account based on account number
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber account number is used find that account
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs contains only one row which is the latest transaction made by that
	 *         account
	 */
	public static ResultSet getLatestTransaction(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"SELECT * FROM transaction_history WHERE account_number = ? ORDER BY txn_timestamp DESC LIMIT 1");
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Gets all the transaction from an account based on account number
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber account number is used to get the account
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs contains all the rows of transactions made by that account
	 */
	public static ResultSet getTransactionFromAccount(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM Transaction_history WHERE account_number = ?");
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Joins the account table and transactions table to get txn_id, username,
	 * phone_number, txn_timestamp and amount for an account
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber account number is used to get the account
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs which contains txn_id, username, phone_number, txn_timestamp and
	 *         amount for an account
	 */
	public static ResultSet joinAccountAndTransaction(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement ps = conn
				.prepareStatement("SELECT *" + " FROM transaction_history t INNER JOIN useraccount u "
						+ "ON t.account_number = u.account_number WHERE u.account_number = ?");
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Gets entire transaction history of every transaction made in the bank
	 * 
	 * @param conn Connection to the db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs which contains the entire transaction history
	 */
	public static ResultSet getEntireTransacationHistory(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		System.out.println(st.toString());
		logger.info(st.toString());
		ResultSet rs = st.executeQuery("SELECT * FROM transaction_history");
		return rs;
	}

	/**
	 * Deletes transactions made by that user account
	 * 
	 * @param conn          Connection to the db
	 * @param accountNumber method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @throws SQLException
	 */
	public static void deleteTransactions(Connection conn, String accountNumber) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM transaction_history WHERE account_number = ?");
		ps.setString(1, accountNumber);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
	}

	/**
	 * Finds transactions which have the transaction field matching to the pattern
	 * user inputed
	 * 
	 * @param conn              Connection to the db
	 * @param fieldName         the name of the field
	 * @param fieldValuePattern the pattern of field value which matches the user
	 *                          search
	 * @return rs Represents transactions which match the pattern
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static ResultSet transactionsLikeAccountField(Connection conn, String fieldName, String fieldValuePattern)
			throws SQLException {
		String query = "SELECT * FROM transaction_history WHERE " + fieldName + " LIKE ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, "%" + fieldValuePattern + "%");
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Finds transactions which have the transaction field matching to the pattern
	 * user inputed with dates
	 * 
	 * @param conn              Connection to the db
	 * @param fieldName         the name of the field
	 * @param fieldValuePattern the pattern of field value which matches the user
	 *                          search
	 * @param startDate         the start date
	 * @param endDate           the end date
	 * @return rs Represents transactions which match the pattern
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static ResultSet transactionsLikeAccountFieldWithDates(Connection conn, String fieldName,
			String fieldValuePattern, String startDate, String endDate) throws SQLException {
		String query = "SELECT * FROM transaction_history WHERE " + fieldName
				+ " LIKE ? AND txn_timestamp BETWEEN ? AND ?";
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
