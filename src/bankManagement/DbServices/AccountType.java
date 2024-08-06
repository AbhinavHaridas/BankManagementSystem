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
 * Class which contains DB methods to interact with account type table
 * 
 * @author Abhinav
 * @version 05/06/2024
 */
public class AccountType {
	public String accountType;

	static final Logger logger = LogManager.getLogger(BankManagement.class.getName());

	/**
	 * Constructor for account type
	 * 
	 * @param accountType represents the type of account
	 */
	public AccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * creates a new account using initialized parameters
	 * 
	 * @param conn Connection to the db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public void createNewAccountType(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO AccountTypes (account_type) VALUES (?)");
		ps.setString(1, this.accountType);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
	}

	/**
	 * Gets all different type of accounts
	 * 
	 * @param conn Connection to the db
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs which represents all the different type of accounts
	 */
	public static ResultSet getAllAccountTypes(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		String query = "SELECT * FROM accounttypes";
		System.out.println(query);
		logger.info(query);
		ResultSet rs = st.executeQuery(query);
		return rs;
	}

	/**
	 * Checks whether the account type passed as parameter is valid or not by
	 * accessing the db
	 * 
	 * @param conn        Connection to the db
	 * @param accountType represents the user inputed accountType
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return boolean (true for valid | false for invalid)
	 */
	public static boolean isValidAccountType(Connection conn, String accountType) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM accounttypes WHERE account_type = ?");
			ps.setString(1, accountType);
			rs = ps.executeQuery();
			System.out.println(ps.toString());
			logger.info(ps.toString());

			if (!rs.isBeforeFirst()) {
				return false;
			}
			return true;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * Deletes the account type from the database
	 * 
	 * @param conn         Connection to the db
	 * @param acccountType represents the user inputed accountType
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * 
	 */
	public static void deleteAccountType(Connection conn, String acccountType) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM AccountTypes WHERE account_type = ?");
		ps.setString(1, acccountType);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
		System.out.println("deleted the account type from the  database");
		logger.info("deleted the account type from the database");
		ps.close();
	}

	/**
	 * Updates the account type
	 * 
	 * @param conn        Connection to the db
	 * @param id          id for the account type
	 * @param accountType represents the user inputed accountType
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * 
	 */
	public static void updateAccountType(Connection conn, int id, String accountType) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE accounttypes SET account_type = ? WHERE id = ?");
		ps.setString(1, accountType);
		ps.setInt(2, id);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ps.executeUpdate();
		System.out.println("Updated the record");
		logger.info("Updated the record");
		ps.close();
	}

	/**
	 * Checks if account type is valid or not
	 * 
	 * @param conn connection to the db
	 * @param id   account type id
	 * @return boolean which says valid or not
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 */
	public static boolean validAccountTypeID(Connection conn, int id) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounttypes WHERE id = ?");
		ps.setInt(1, id);
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		if (!rs.isBeforeFirst()) {
			System.out.println("Not valid account id");
			logger.error("Not valid account id");
			return false;
		} else {
			System.out.println("Valid account id");
			logger.info("Valid account id");
			return true;
		}

	}

	/**
	 * Finds account type field matching to the pattern user inputed
	 * 
	 * @param conn              Connection to the db
	 * @param fieldName         the name of field that the user wants to search
	 * @param fieldValuePattern the pattern of field value based on which the user
	 *                          wants to do the like search
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this method
	 * @return rs Represents the accounts which match the pattern
	 */
	public static ResultSet accountsLikeAccountTypeField(Connection conn, String fieldName, String fieldValuePattern)
			throws SQLException {
		String query = "SELECT * FROM accounttypes WHERE " + fieldName + " LIKE ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, "%" + fieldValuePattern + "%");
		System.out.println(ps.toString());
		logger.info(ps.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	/**
	 * Finds account type field matching to the pattern user inputed and filters it
	 * based on start and end date
	 * 
	 * @param conn              connection to the db
	 * @param fieldName         the name of field that the user wants to search
	 * @param fieldValuePattern the pattern of field value based on which the user
	 *                          wants to do the like search
	 * @param startDate         the beginning date range
	 * @param endDate           the ending date range
	 * @return rs Represents the account which matches the pattern
	 * @throws SQLException method could contain wrong SQL query which should stop
	 *                      the execution of caller of this metho
	 */
	public static ResultSet accountLikeAccoutTypeFieldWithDates(Connection conn, String fieldName,
			String fieldValuePattern, String startDate, String endDate) throws SQLException {
		String query = "SELECT * FROM accounttypes WHERE " + fieldName
				+ " LIKE ? AND account_type_timestamp BETWEEN ? AND ?";
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
