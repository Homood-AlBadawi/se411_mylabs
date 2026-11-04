package edu.psu.se411.lab12_threads;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab12_threads.model.Transaction;
import edu.psu.se411.lab12_threads.sim.TransferBenchmark;

/**
 * Lab 12, part 1 - the second main program, using a CACHED thread pool.
 *
 * Run with: clean package exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.AppCachedPool
 *
 * <p>A cached pool has no size limit. It reuses an idle thread when one is
 * available and creates a new one when none is, keeping each for 60 seconds.
 * That is ideal for many short, <b>independent</b> tasks - and a trap for tasks
 * that contend for a lock, because a blocked thread is not idle, so the pool
 * keeps manufacturing more of them.
 */
public class AppCachedPool {

	static Logger logger = LoggerFactory.getLogger(AppCachedPool.class);

	public static Double initialBalance = 10000.0;
	public static Integer nbTransactions = 1000000;

	public static void main(String[] args) {
		logger.info("Application is starting...");

		System.out.println("=== CACHED thread pool (unbounded) ===");
		System.out.println("transactions per scenario: " + nbTransactions);
		System.out.println("strategy: per-account synchronized (the Lab 11 solution)\n");

		TransferBenchmark.Result many = TransferBenchmark.run(
				"cached",
				() -> Executors.newCachedThreadPool(),
				1000, nbTransactions, initialBalance, Transaction.Mode.SYNCHRONIZED);
		print(many);

		TransferBenchmark.Result few = TransferBenchmark.run(
				"cached",
				() -> Executors.newCachedThreadPool(),
				10, nbTransactions, initialBalance, Transaction.Mode.SYNCHRONIZED);
		print(few);

		System.out.printf("%nCached pool: %d accounts took %d ms, 10 accounts took %d ms.%n",
				many.accounts, many.millis, few.millis);

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
}
