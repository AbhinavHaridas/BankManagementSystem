package bankManagement.CliServices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bankManagement.DbServices.TransactionHistory;

/**
 * Class which contains CLI methods to call the DB methods for transaction
 * history table
 * 
 * @author Abhinav
 * @version 06/06/2024
 */
public class TransactionHistoryService {
	static final Logger logger = LogManager.getLogger(TransactionHistoryService.class.getName());

	/**
	 * Default constructor for TransactionHistoryService
	 */
	TransactionHistoryService() {
	}

	/**
	 * Views the latest transaction by taking user input and passing that to the db
	 * service method
	 * 
	 * @param sc   scanner which is used to take user input
	 * @param conn Connection to the db
	 */
	public static void viewLatestTransaction(Scanner sc, Connection conn) {
		try {
			System.out.println("Enter your account number to search");
			logger.info("Enter your account number to search");
			String accountNumber = sc.nextLine();
			System.out.println("User input - " + accountNumber);
			logger.info("User input - " + accountNumber);
			ResultSet rs = TransactionHistory.getLatestTransaction(conn, accountNumber);
			if (!rs.isBeforeFirst()) {
				System.out.println("Sorry no transactions for this account at the moment");
				logger.error("Sorry, no transactions for the account: " + accountNumber + " at the moment");
			} else {
				while (rs.next()) {
					String transactionDetails = "Transaction ID - " + rs.getInt("txn_id") + ", Amount - "
							+ rs.getFloat("amount") + ", Transaction Type - " + rs.getString("transaction_type")
							+ ", Transaction timestamp - " + rs.getString("txn_timestamp");
					System.out.println(transactionDetails);
					logger.info(transactionDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
	}

	/**
	 * Views all the transacitons made on that bank account by passing it to the db
	 * method for transaction history and displaying results for it
	 * 
	 * @param conn Connection to the db
	 */
	public static void viewAllTransactions(Connection conn) {
		try {
			ResultSet rs = TransactionHistory.getEntireTransacationHistory(conn);
			if (!rs.isBeforeFirst()) {
				System.out.println("No transactions available at the moment");
				logger.error("No transactions available at the moment");
			} else {
				while (rs.next()) {
					String transactionDetails = "Transaction ID - " + rs.getInt("txn_id") + ", Account Number - "
							+ rs.getString("account_number") + ", Amount - " + rs.getFloat("amount")
							+ ", Transaction Type - " + rs.getString("transaction_type") + ", Transaction timestamp - "
							+ rs.getString("txn_timestamp");
					System.out.println(transactionDetails);
					logger.info(transactionDetails);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
	}
}
