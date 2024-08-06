package bankManagement.CliServices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bankManagement.Utils;
import bankManagement.DbServices.AccountType;
import bankManagement.DbServices.TransactionHistory;
import bankManagement.DbServices.UserAccount;

/**
 * Class which contains CLI methods to call the DB method for user account table
 * 
 * @author Abhinav
 * @version 06/06/2024
 */
public class UserAccountService {
	static final Logger logger = LogManager.getLogger(UserAccountService.class.getName());

	private static final String PHONE_NUMBER_PATTERN = "^[789][0-9]{9}$";

	private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

	/**
	 * Default constructor for UserAccountService
	 */
	UserAccountService() {
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
	 * Driver method to create an account for a user
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void createAccount(Scanner sc, Connection conn) {
		try {
			System.out.println("Create your account");
			logger.info("Create your account");
			System.out.println("Enter your account number which should be 12 characters long");
			logger.info("Enter your account number which should be 12 characters long");
			String accountNumber = sc.nextLine();
			logger.info("User input - " + accountNumber);
			while (accountNumber.length() != 12 || UserAccount.validAccountNumber(conn, accountNumber)) {
				if (UserAccount.validAccountNumber(conn, accountNumber)) {
					System.out.println("That account number already exists.please reenter the account number");
					logger.error("That account number already exists.please reenter the account number");
				} else {
					System.out.println("Account number should be 12 characters long. Re-enter it");
					logger.error("Account number should be 12 characters long. Re-enter it");
				}
				accountNumber = sc.nextLine();
				logger.info("User input - " + accountNumber);
			}
			System.out.println("Enter account holder's first name");
			StringBuilder firstName = new StringBuilder(sc.nextLine());
			logger.info("User input - " + firstName.toString());
			Utils.emptyStringFix("first name", firstName, sc);
			System.out.println("Enter account holder's last name");
			StringBuilder lastName = new StringBuilder(sc.nextLine());
			logger.info("User input - " + lastName.toString());
			Utils.emptyStringFix("last name", lastName, sc);
			System.out.println("Enter username");
			logger.info("Enter  username");
			StringBuilder username = new StringBuilder(sc.nextLine());
			logger.info("User input - " + username.toString());
			Utils.emptyStringFix("username", username, sc);
			System.out.println("Enter account holder's indian phone number");
			logger.info("Enter account holder's indian phone number");
			String phoneNumber = sc.nextLine();
			logger.info("User input - " + phoneNumber);
			while (!isPhoneNumberValid(phoneNumber)) {
				System.out.println("Sorry that is not a valid phone number! please reenter it");
				logger.info("Sorry that is not a valid phone number! please reenter it");
				phoneNumber = sc.nextLine();
				logger.info("User input - " + phoneNumber);
			}
			System.out.println("Enter account nominee first name");
			logger.info("Enter account nominee first name");
			StringBuilder nomineeFirstName = new StringBuilder(sc.nextLine());
			logger.info("User input - " + nomineeFirstName.toString());
			Utils.emptyStringFix("nominee first name", nomineeFirstName, sc);
			System.out.println("Enter account nominee last name");
			logger.info("Enter account nominee last name");
			StringBuilder nomineeLastName = new StringBuilder(sc.nextLine());
			logger.info("User input - " + nomineeLastName);
			Utils.emptyStringFix("nominee last name", nomineeLastName, sc);
			System.out.println("Enter your account type. Please type any of the type below");
			logger.info("Enter your account type. Please type any of the type below");
			AccountTypeService.displayAccountTypes(conn);
			String accountType = sc.nextLine();
			logger.info("User input - " + accountType);
			boolean isValid = AccountType.isValidAccountType(conn, accountType);
			if (isValid) {
				UserAccount account = new UserAccount(firstName.toString(), lastName.toString(), username.toString(),
						nomineeFirstName.toString(), nomineeLastName.toString(), phoneNumber, accountType,
						accountNumber);
				account.createAccount(conn);
			} else {
				System.out.println("The account type is not valid");
				logger.error("The account type is not valid");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception {}" + e.getMessage(), e);
		}
	}

	/**
	 * Static method to modify account details
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void modifyUserAccount(Scanner sc, Connection conn) {
		try {
			List<String> fields = List.of("first name", "last name", "username", "phone number", "nominee first name",
					"nominee last name");
			System.out.println("The fields that you can edit are");
			logger.info("The fields that you can edit are");
			fields.forEach((field) -> {
				System.out.println("-> " + field);
				logger.info("-> " + field);
			});
			System.out.println("What field do you wish to account? Please type it below");
			logger.info("What field do you wish to account? Please type it below");
			String userField = sc.nextLine();
			logger.info("User input - " + userField);
			if (fields.contains(userField)) {
				System.out.println("Enter account number of the account whose username you wish to change");
				logger.info("Enter account number of the account whose username you wish to change");
				String accountNumber = sc.nextLine();
				logger.info("User input - " + accountNumber);
				// validate account number
				boolean isValidAcNum = UserAccount.validAccountNumber(conn, accountNumber);
				if (isValidAcNum) {
					System.out.println("Enter the new " + userField);
					logger.info("Enter the new " + userField);
					StringBuilder userFieldValue = new StringBuilder(sc.nextLine());
					logger.info("User input - " + userFieldValue.toString());
					Utils.emptyStringFix(userField, userFieldValue, sc);
					// modifying user field to be DB specific
					userField = userField.toLowerCase().trim().replace(" ", "_");
					UserAccount.updateAccountFields(conn, userFieldValue.toString(), accountNumber, userField);
				} else {
					System.out.println("That is not a valid account number");
					logger.error("That is not a valid account number");
				}
			} else {
				System.out.println("There is no such field in user accounts table");
				logger.info("There is no such field in user accounts table");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception {}", e.getMessage(), e);
		}
	}

	/**
	 * Method for a doing an sql like search on the account and then view the
	 * transactions it has done and then linking username, phone number and date at
	 * which the transactions have taken place
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void searchAccount(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter the account number or a pattern of account number");
			logger.info("Enter the account number or a pattern of account number");
			String accountNumberPattern = sc.nextLine();
			logger.info("User input - " + accountNumberPattern);
			ResultSet rs = UserAccount.accountsLikeAccountNumber(conn, accountNumberPattern);
			if (!rs.isBeforeFirst()) {
				System.out.println("Sorry no accounts match that pattern");
				logger.error("Sorry no accounts match the pattern: " + accountNumberPattern);
				return;
			}
			System.out.println("The account numbers with username are : ");
			logger.info("The account numbers with username are : ");
			while (rs.next()) {
				System.out.println("-> account number - " + rs.getString("account_number") + ",  username - "
						+ rs.getString("username"));
				logger.info("-> account number - " + rs.getString("account_number") + ", usernmae "
						+ rs.getString("username"));
			}
			System.out.println("View the details of transaction by entering the account number above ");
			logger.info("View the details of transaction by entering the account number above ");
			String accountNumber = sc.nextLine();
			logger.info("User input - " + accountNumber);
			if (UserAccount.validAccountNumber(conn, accountNumber)) {
				rs = TransactionHistory.getTransactionFromAccount(conn, accountNumber);
				if (!rs.isBeforeFirst()) {
					System.out.println("This account has not made any transactions");
					logger.error("This account has not made any transations: " + accountNumberPattern);
					return;
				}
				System.out.println("The transactions that this account has made: ");
				logger.info("The transactions that this account has made: ");
				while (rs.next()) {
					String transactionDetails = "Transaction ID - " + rs.getInt("txn_id") + ", Amount - "
							+ rs.getFloat("amount") + ", Transaction Type - " + rs.getString("transaction_type")
							+ ", Transaction timestamp - " + rs.getString("txn_timestamp");
					System.out.println(transactionDetails);
					logger.info(transactionDetails);
				}
				System.out.println("Linking name of the user, phone number, date on which transaction is done");
				logger.info("Linking name of the user, phone number, date on which transaction is done");
				rs = TransactionHistory.joinAccountAndTransaction(conn, accountNumber);
				while (rs.next()) {
					System.out.println("txn_id - " + rs.getInt("txn_id") + ", username - " + rs.getString("username")
							+ ", user full name - " + rs.getString("first_name") + " " + rs.getString("last_name")
							+ ", phone number - " + rs.getString("phone_number") + ", transaction timestamp - "
							+ rs.getString("txn_timestamp"));
					logger.info("txn_id - " + rs.getInt("txn_id") + ", username - " + rs.getString("username")
							+ ", user full name - " + rs.getString("first_name") + " " + rs.getString("last_name")
							+ ", phone number - " + rs.getString("phone_number") + ", transaction timestamp - "
							+ rs.getString("txn_timestamp"));
				}

			} else {
				System.out.println("Sorry not valid account number");
				logger.error("Sorry not valid account number");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e.getMessage(), e);
		}
	}

	/**
	 * Method to check avalibility of an account based on an account number
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void checkAccountAvailability(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter the account number to search");
			logger.info("Enter the account number to search");
			String accountNumber = sc.nextLine();
			System.out.println("User input - " + accountNumber);
			logger.info("User input - " + accountNumber);
			ResultSet rs = UserAccount.getAccountByAccountNumber(conn, accountNumber);
			if (!rs.isBeforeFirst()) {
				System.out.println("Sorry there are no account with that account number");
				logger.error("Sorry, no account found with the account number: " + accountNumber);
			} else {
				System.out.println("Here is the account that you want");
				logger.info("Here is the account that you want");
				while (rs.next()) {
					String accountDetails = "Account Number - " + rs.getString("account_number") + ", Username - "
							+ rs.getString("username") + ", Nominee - " + rs.getString("nominee_first_name") + " "
							+ rs.getString("nominee_last_name") + ", Account Type - " + rs.getString("account_type")
							+ ", Phone Number - " + rs.getString("phone_number");
					System.out.println(accountDetails);
					logger.info(accountDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e.getMessage(), e);
		}
	}

	/**
	 * Method to get balance of an account based on an account number
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void getBalance(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter the account number to search");
			logger.info("Enter the account number to search");
			String accountNumber = sc.nextLine();
			logger.info("User input - " + accountNumber);
			if (UserAccount.validAccountNumber(conn, accountNumber)) {
				float balance = UserAccount.getBalanceForAnAccount(conn, accountNumber);
				System.out.println(balance);
				logger.info(balance);

			} else {
				System.out.println("Not valid account number");
				logger.error("Not valid account number");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(e.getMessage(), e);
		}
	}

	/**
	 * Method to deposit money into an account number
	 *
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void depositMoney(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter your account number");
			logger.info("Enter your account number");
			String accountNumber = sc.nextLine();
			while (!UserAccount.validAccountNumber(conn, accountNumber)) {
				System.out.println("Not valid account number. Please enter a valid account number");
				logger.info("Not valid account number. Please enter a valid account number");
				accountNumber = sc.nextLine();
			}
			logger.info("User input - " + accountNumber);

			float amount = 0;
			boolean validAmount = false;
			while (!validAmount) {
				try {
					System.out.println("Enter the amount you want to deposit");
					logger.info("Enter the amount you want to deposit");
					amount = sc.nextFloat();
					validAmount = true;
				} catch (InputMismatchException e) {
					System.out.println("Invalid amount. Please enter a valid number.");
					logger.error("Invalid amount entered: " + sc.next());
					sc.nextLine();
				}
			}
			logger.info("User input - " + amount);

			UserAccount.depositIntoAccount(conn, amount, accountNumber);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
	}

	/**
	 * Method to withdraw money into an account number
	 * 
	 * @param sc   Scanner for user input
	 * @param conn Connection to interact with the database
	 */
	public static void withdrawMoney(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter your account number");
			logger.info("Enter your account number");
			String accountNumber = sc.nextLine();
			while (!UserAccount.validAccountNumber(conn, accountNumber)) {
				System.out.println("Not valid account number. please enter a valid account number");
				logger.info("Not valid account number. please enter a valid account number");
				accountNumber = sc.nextLine();
			}
			logger.info("User input - " + accountNumber);
			float amount = 0;
			boolean validAmount = false;
			while (!validAmount) {
				try {
					System.out.println("Enter the amount you want to withdraw");
					logger.info("Enter the amount you want to withdraw");
					amount = sc.nextFloat();
					validAmount = true;
				} catch (InputMismatchException e) {
					System.out.println("Invalid amount. Please enter a valid number.");
					logger.error("Invalid amount entered: " + sc.next());
					sc.nextLine();
				}
			}
			logger.info("User input - " + amount);

			UserAccount.withdrawFromAccount(conn, amount, accountNumber);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception {}", e.getMessage(), e);
		}
	}

	/**
	 * Method to view all accounts
	 * 
	 * @param conn Connection to interact with the database
	 */
	public static void viewAllAccounts(Connection conn) {
		try {
			ResultSet rs = UserAccount.getAllAccounts(conn);
			if (!rs.isBeforeFirst()) {
				System.out.println("Sorry there are no accounts at the moment");
				logger.error("Sorry, there are no accounts at the moment");
			} else {
				while (rs.next()) {
					String accountDetails = "Account Number - " + rs.getString("account_number")
							+ ", Account Holder's name - " + rs.getString("first_name") + " "
							+ rs.getString("last_name") + ", Username - " + rs.getString("username")
							+ ", Nominee's name - " + rs.getString("nominee_first_name") + " "
							+ rs.getString("nominee_last_name") + ", Account Type - " + rs.getString("account_type")
							+ ", Phone Number - " + rs.getString("phone_number");
					System.out.println(accountDetails);
					logger.info(accountDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception {}", e.getMessage(), e);
		}
	}
}
