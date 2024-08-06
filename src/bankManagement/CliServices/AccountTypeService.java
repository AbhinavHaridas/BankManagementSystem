package bankManagement.CliServices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bankManagement.Utils;
import bankManagement.DbServices.AccountType;

/**
 * Class which contains CLI methods to call the DB methods for account types
 * table
 * 
 * @author Abhinav
 * @version 06/06/2024
 */
public class AccountTypeService {
	static final Logger logger = LogManager.getLogger(AccountTypeService.class.getName());

	/**
	 * Default constructor for AccoutTypeService
	 */
	AccountTypeService() {
	}

	/**
	 * Creates a brand new account account type based on user inputed string
	 * 
	 * @param sc   scanner for user input
	 * @param conn Connection to the database
	 */
	public static void createNewAccountType(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter the name of new account type");
			logger.info("Enter the name of new account type");
			StringBuilder accountType = new StringBuilder(sc.nextLine());
			logger.info("User input - " + accountType.toString());
			// check if the account type already exists or not
			while (AccountType.isValidAccountType(conn, accountType.toString().trim())) {
				System.out.println("Sorry the account type already exists, please enter a new account type");
				logger.info("Sorry the account type already exists, please enter a new account type");
				accountType = new StringBuilder(sc.nextLine());
				logger.info("User input - " + accountType.toString());
			}
			Utils.emptyStringFix("account type", accountType, sc);
			AccountType type = new AccountType(accountType.toString().toLowerCase());
			type.createNewAccountType(conn);
			System.out.println("created the account type");
			logger.info("created the account type");
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
	}

	/**
	 * Displays all the different account types
	 * 
	 * @param conn Connection to the db
	 */
	public static void displayAccountTypes(Connection conn) {
		try {
			ResultSet rs = AccountType.getAllAccountTypes(conn);
			while (rs.next()) {
				String accountType = rs.getString("account_type");
				System.out.println("->" + accountType);
				logger.info("->" + accountType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
	}

}
