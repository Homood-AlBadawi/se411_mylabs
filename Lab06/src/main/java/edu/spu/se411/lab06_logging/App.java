package edu.spu.se411.lab06_logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.spu.se411.lab06_logging.exceptions.InsufficientFundsException;
import edu.spu.se411.lab06_logging.model.WalletAccount;

/**
 * SE411 - Lab 06: Logging with SLF4J.
 *
 * Run with: clean package exec:java
 */
public class App {

	/** One logger per class, named after the class, as SLF4J intends. */
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {

		logger.info("Application is starting...");

		WalletAccount account = new WalletAccount(1000);

		try {
			account.withdraw(1500);
		} catch (InsufficientFundsException e) {
			// The handler is where we know the exception actually escaped, so
			// this is where it is logged at ERROR - once, with the throwable
			// itself so the stack trace is preserved.
			logger.error("Withdrawal failed for amount {}", 1500.0, e);
		}

		try {
			account.deposit(-100);
		} catch (IllegalArgumentException e) {
			logger.error("Deposit failed for amount {}", -100.0, e);
		}

		// A successful pair, so the log shows the happy path too.
		try {
			account.deposit(250);
			account.withdraw(400);
		} catch (InsufficientFundsException e) {
			logger.error("Unexpected failure on the valid transactions", e);
		}

		logger.info("Application is ending...");
	}
}
