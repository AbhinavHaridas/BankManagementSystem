package bankManagement;

import java.sql.Connection;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Routes.AccountTypeRoutes;
import Routes.MailAndDownloadRoutes;
import Routes.TransactionRoutes;
import Routes.UserAccountRoutes;
import bankManagement.CliServices.AccountTypeService;
import bankManagement.CliServices.TransactionHistoryService;
import bankManagement.CliServices.UserAccountService;
import io.javalin.Javalin;

/**
 * Main class
 * 
 * @author Abhinav
 * @version 03/06/2024
 */
public class BankManagement {
	static final Logger logger = LogManager.getLogger(BankManagement.class.getName());

	/**
	 * Default constructor of BankManagement
	 */
	BankManagement() {
	}

	/**
	 * Main method of class which uses switch statement for routing or calls API
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Application started");
		Connection conn = Utils.generateDBConnection();
		Scanner sc = new Scanner(System.in);
		boolean userUseSystem = true;

		boolean doAPI = true;

		if (!doAPI) {
			while (userUseSystem) {
				try {
					System.out.println("---------------------------------");
					logger.info("---------------------------------");
					System.out.println("This is a bank management system");
					logger.info("This is a bank management system");

					System.out
							.println("It provides the following options to work with:\n" + "1. Create a user account\n"
									+ "2. Display different types of accounts\n" + "3. Modify user account\n"
									+ "4. Viewing latest transactions\n" + "5. Search for an account\n"
									+ "6. Availability of an account\n" + "7. Get balance of an account\n"
									+ "8. Deposit money into an account\n" + "9. Withdraw money from an account\n"
									+ "10. View all accounts\n" + "11. View entire transaction history\n"
									+ "12. Create a new account type\n" + "Any other number. Exit");

					logger.info("It provides the following options to work with:\n" + "1. Create a user account\n"
							+ "2. Display different types of accounts\n" + "3. Modify user account\n"
							+ "4. Viewing latest transactions\n" + "5. Search for an account\n"
							+ "6. Availability of an account\n" + "7. Get balance of an account\n"
							+ "8. Deposit money into an account\n" + "9. Withdraw money from an account\n"
							+ "10. View all accounts\n" + "11. View entire transaction history\n"
							+ "12. Create a new account type\n" + "Any other number. Exit");

					System.out.println("Choose an option by selecting the appropriate number");
					logger.info("Choose an option by selecting the appropriate number");
					int option = sc.nextInt();
					logger.info("User input - " + option);
					sc.nextLine();

					switch (option) {
					case 1:
						UserAccountService.createAccount(sc, conn);
						break;
					case 2:
						AccountTypeService.displayAccountTypes(conn);
						break;
					case 3:
						UserAccountService.modifyUserAccount(sc, conn);
						break;
					case 4:
						TransactionHistoryService.viewLatestTransaction(sc, conn);
						break;
					case 5:
						UserAccountService.searchAccount(sc, conn);
						break;
					case 6:
						UserAccountService.checkAccountAvailability(sc, conn);
						break;
					case 7:
						UserAccountService.getBalance(sc, conn);
						break;
					case 8:
						UserAccountService.depositMoney(sc, conn);
						break;
					case 9:
						UserAccountService.withdrawMoney(sc, conn);
						break;
					case 10:
						UserAccountService.viewAllAccounts(conn);
						break;
					case 11:
						TransactionHistoryService.viewAllTransactions(conn);
						break;
					case 12:
						AccountTypeService.createNewAccountType(sc, conn);
						break;
					default:
						userUseSystem = false;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.fatal("Exception {}", e.getMessage(), e);
					sc.nextLine();
				}

			}

			logger.info("application closed");
		} else {
			// API part here
			Javalin app = Javalin.create(config -> {
				config.bundledPlugins.enableCors(cors -> {
					cors.addRule(it -> {
						it.anyHost();
					});
				});
			}).start(7000);
			app.get("/", ctx -> {
				ctx.result("Hello world");
			});

			AccountTypeRoutes.register(app, conn);
			TransactionRoutes.register(app, conn);
			UserAccountRoutes.register(app, conn);
			MailAndDownloadRoutes.register(app);
		}
	}
}