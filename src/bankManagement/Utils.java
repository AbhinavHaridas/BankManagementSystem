package bankManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bankManagement.CliServices.TransactionHistoryService;

/**
 * Enum that shows different type of transactions
 * 
 * @author Abhinav
 * @version 06/06/2024
 */
enum TransactionType {
	Deposit, Withdrawl
}

/**
 * Class that contains some utility methods
 * 
 * @author Abhinav
 * @version 06/06/2024
 */
public class Utils {
	static final Logger logger = LogManager.getLogger(TransactionHistoryService.class.getName());
	private static StringBuilder builder = new StringBuilder();

	/**
	 * Default constructor for Utils
	 */
	Utils() {
	}

	/**
	 * Generates a MySQL database connection
	 * 
	 * @return connection object which is used to interact with the database
	 */
	static Connection generateDBConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("DB_USERNAME"),
					System.getenv("DB_PASSWORD"));
			System.out.println("Connected to the db");
			logger.info("Connected to the db");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Exception: {}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Validates whether the transaction type that the user entered is valid or not
	 * 
	 * @param transactionType transaction type string which is to be validated
	 * @return TransactionType its a string or it gives error
	 */
	public static TransactionType validateTransactionType(String transactionType) {
		try {
			return TransactionType.valueOf(transactionType);
		} catch (IllegalArgumentException e) {
			logger.fatal("This is not a valid type of transaction");
			throw new IllegalArgumentException("This is not a valid type of transaction");
		}
	}

	/**
	 * Asks the user to reenter the field value until it is not an empty string
	 * 
	 * @param fieldName  name of the field
	 * @param fieldValue value of the field which the user has to define
	 * @param sc         scanner for taking user input
	 */
	public static void emptyStringFix(String fieldName, StringBuilder fieldValue, Scanner sc) {
		while (fieldValue.toString().trim().length() == 0) {
			System.out.println("Null strings are not allowed please reenter it");
			logger.error("Null strings are not allowed please reenter it");
			fieldValue.append(sc.nextLine());
			logger.info("User input - " + fieldValue.toString());
		}
	}

	/**
	 * Converts List of records to text
	 * 
	 * @param dataMap Data map which is contains json of records
	 * @return String string which cotains all the records comma seperated
	 */
	public static String convertToText(List<LinkedHashMap<String, Object>> dataMap) {
		List<String> textArray = new ArrayList<>();
		LinkedHashMap<String, Object> headers = dataMap.get(0);
		// format the object properly
		textArray.add(String.join(",", headers.keySet()));
		// clear builder
		builder.delete(0, builder.length());
		for (int i = 0; i < dataMap.size(); i++) {
			LinkedHashMap<String, Object> entry = dataMap.get(i);
			BiConsumer<String, Object> bc = new CSVBiConsumer();
			entry.forEach(bc);
			textArray.add(builder.toString());
			// clear builder
			builder.delete(0, builder.length());
		}

		String text = String.join("\n", textArray);
		return text;
	}

	/**
	 * Required for forEach loop
	 */
	static class CSVBiConsumer implements BiConsumer<String, Object> {
		@Override
		public void accept(String key, Object value) {
			builder.append(value).append(",");
		}
	}
}