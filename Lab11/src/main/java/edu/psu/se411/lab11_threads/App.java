package edu.psu.se411.lab11_threads;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab11_threads.model.CryptoAccount;
import edu.psu.se411.lab11_threads.model.Transaction;

/**
 * SE411 - Lab 11: the double spending problem.
 *
 * Build and run using Maven goal: clean package exec:java
 */
public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static Integer nbAccounts = 10;
	public static Double initialBalance = 1000.0;
	public static Integer nbTransactions = 10000;

	public static void main(String[] args) {
		logger.info("Application is starting...");

		double expected = nbAccounts * initialBalance;

		System.out.println("Accounts: " + nbAccounts
				+ ", each starting with " + initialBalance
				+ "  ->  system total should always be " + expected);
		System.out.println("Transactions per run: " + nbTransactions
				+ ", one thread each\n");

		double unsafeTotal = runSimulation(false);
		double safeTotal = runSimulation(true);

		printVerdict(expected, unsafeTotal, safeTotal);

		logger.info("Application is closing...");
	}

	// ==================================================================
	// One full simulation
	// ==================================================================

	/**
	 * Builds a fresh set of accounts, fires one thread per transaction, waits
	 * for all of them, and reports the system total.
	 *
	 * @param useSynchronized false for part 1 (broken), true for part 2 (fixed)
	 * @return the total money in the system once every thread has finished
	 */
	private static double runSimulation(boolean useSynchronized) {
		String label = useSynchronized ? "WITH synchronized" : "WITHOUT synchronization";
		System.out.println("=== " + label + " ===");

		List<CryptoAccount> accounts = createAccounts();
		System.out.printf("total before transactions: %.2f%n", getTotalBalance(accounts));

		List<Thread> threads = new ArrayList<Thread>(nbTransactions);

		for (int i = 0; i < nbTransactions; i++) {
			// Exactly the code the handout asks us to make possible.
			CryptoAccount randomA = accounts.get((int) (Math.random() * nbAccounts));
			CryptoAccount randomB = accounts.get((int) (Math.random() * nbAccounts));
			Double randomAmount = (double) (Math.random() * 10 + 1);

			Transaction t = new Transaction(randomA, randomB, randomAmount);

			Runnable job = useSynchronized
					? () -> Transaction.executeSynchronized(t)
					: () -> t.execute();

			threads.add(new Thread(job, "tx-" + i));
		}

		long start = System.currentTimeMillis();
		for (Thread thread : threads) {
			thread.start();
		}
		// Every thread must finish before the total means anything.
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.error("Interrupted while waiting for transaction threads", e);
			}
		}
		long elapsed = System.currentTimeMillis() - start;

		double total = getTotalBalance(accounts);
		System.out.printf("total after  transactions: %.2f   (%d ms)%n", total, elapsed);
		System.out.println();

		return total;
	}

	private static List<CryptoAccount> createAccounts() {
		List<CryptoAccount> accounts = new ArrayList<CryptoAccount>(nbAccounts);
		for (Integer i = 0; i < nbAccounts; i++) {
			accounts.add(new CryptoAccount("acc_" + i.toString(), initialBalance));
		}
		return accounts;
	}

	/**
	 * The total amount of money in the crypto-currency system.
	 *
	 * Money is only ever moved between accounts, never created or destroyed, so
	 * this number must be identical before and after any number of
	 * transactions. When it is not, the system has lost money to a race.
	 */
	public static double getTotalBalance(List<CryptoAccount> accounts) {
		double total = 0.0;
		for (CryptoAccount account : accounts) {
			total += account.getBalance();
		}
		return total;
	}

	// ==================================================================
	// Verdict
	// ==================================================================

	private static void printVerdict(double expected, double unsafeTotal, double safeTotal) {
		System.out.println("=== Verdict ===");
		System.out.printf("expected total        : %.2f%n", expected);
		System.out.printf("without synchronized  : %.2f   (difference %+.2f)%n",
				unsafeTotal, unsafeTotal - expected);
		System.out.printf("with synchronized     : %.2f   (difference %+.2f)%n",
				safeTotal, safeTotal - expected);

		if (!isConserved(expected, unsafeTotal)) {
			System.out.println("\nThe unsynchronized run corrupted the total. Two threads read the"
					+ " same balance and one update overwrote the other, so money was created or"
					+ " destroyed out of nothing.");
		} else {
			System.out.println("\nThe unsynchronized run happened to come out right this time."
					+ " That is luck, not correctness - run it again.");
			logger.warn("Unsynchronized run produced the correct total by chance");
		}

		if (isConserved(expected, safeTotal)) {
			System.out.println("The synchronized run conserved every riyal.");
		} else {
			System.out.println("The synchronized run lost money - that would be a bug.");
			logger.error("Synchronized run lost money: expected {} but got {}", expected, safeTotal);
		}
	}

	/**
	 * True when the total still matches, allowing for floating-point drift.
	 *
	 * Balances are {@code double}, and adding the same set of numbers in a
	 * different order can differ in the last bit or two - so a correct run can
	 * land on 10000.000000000005 rather than exactly 10000.0. That residue is
	 * arithmetic, not a race. A race moves the total by whole riyals, which is
	 * far outside this tolerance.
	 *
	 * (Real money would use {@code BigDecimal} or integer minor units. The
	 * starter code uses {@code Double}, so this comparison has to tolerate it.)
	 */
	private static boolean isConserved(double expected, double actual) {
		return Math.abs(actual - expected) < 0.000001;
	}
}
