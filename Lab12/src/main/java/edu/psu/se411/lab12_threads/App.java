package edu.psu.se411.lab12_threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab12_threads.model.Transaction;
import edu.psu.se411.lab12_threads.sim.TransferBenchmark;

/**
 * Lab 12, part 1 - the FIXED thread pool program.
 *
 * Build and run using Maven goal: clean package exec:java
 *
 * <p>Runs the two scenarios the handout asks about:
 * <ul>
 *   <li>1000 accounts, one million transactions</li>
 *   <li>10 accounts, one million transactions</li>
 * </ul>
 *
 * <p>Compare the numbers with {@link AppCachedPool}.
 */
public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static Double initialBalance = 10000.0;
	public static Integer nbTransactions = 1000000;

	/**
	 * How many threads the fixed pool gets.
	 *
	 * <p>The starter used {@code newFixedThreadPool(nbTransactions)} - one
	 * thread per task. At 100,000 that is merely wasteful; at one million it
	 * cannot be done at all, because every thread reserves its own stack and
	 * the JVM runs out of memory long before the millionth. A fixed pool is
	 * supposed to be sized to the <b>hardware</b>, not to the workload, so this
	 * uses the core count.
	 */
	public static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {
		logger.info("Application is starting...");

		System.out.println("=== FIXED thread pool (" + POOL_SIZE + " threads) ===");
		System.out.println("transactions per scenario: " + nbTransactions);
		System.out.println("strategy: per-account synchronized (the Lab 11 solution)\n");

		TransferBenchmark.Result many = TransferBenchmark.run(
				"fixed(" + POOL_SIZE + ")",
				() -> Executors.newFixedThreadPool(POOL_SIZE),
				1000, nbTransactions, initialBalance, Transaction.Mode.SYNCHRONIZED);
		print(many);

		TransferBenchmark.Result few = TransferBenchmark.run(
				"fixed(" + POOL_SIZE + ")",
				() -> Executors.newFixedThreadPool(POOL_SIZE),
				10, nbTransactions, initialBalance, Transaction.Mode.SYNCHRONIZED);
		print(few);

		System.out.printf("%nSame pool, same workload: %d accounts took %d ms, "
				+ "10 accounts took %d ms.%n", many.accounts, many.millis, few.millis);
		System.out.println("The only difference is how many accounts the threads"
				+ " are fighting over.");

		logger.info("Application is closing...");
	}

	private static void print(TransferBenchmark.Result r) {
		System.out.printf("%d accounts%n", r.accounts);
		System.out.printf("   initial money volume: %.2f%n", r.initialTotal);
		System.out.printf("   final money volume  : %.2f   (%s)%n",
				r.finalTotal, r.isConserved() ? "conserved" : "CORRUPTED");
		System.out.printf("   elapsed             : %d ms%n", r.millis);
		System.out.printf("   peak live threads   : %d%n%n", r.peakThreads);
	}

	/** Kept so the starter's single-pool shape is still available. */
	public static ExecutorService newPool() {
		return Executors.newFixedThreadPool(POOL_SIZE);
	}
}
