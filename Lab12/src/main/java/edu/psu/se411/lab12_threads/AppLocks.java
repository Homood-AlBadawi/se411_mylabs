package edu.psu.se411.lab12_threads;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab12_threads.model.CryptoAccount;
import edu.psu.se411.lab12_threads.model.Transaction;
import edu.psu.se411.lab12_threads.sim.TransferBenchmark;

/**
 * Lab 12, parts 2 and 3 - replacing {@code synchronized} with locks, the
 * deadlock that causes, and resource ordering as the fix.
 *
 * Run with: clean package exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.AppLocks
 */
public class AppLocks {

	static Logger logger = LoggerFactory.getLogger(AppLocks.class);

	private static final double INITIAL_BALANCE = 10000.0;
	private static final int BENCHMARK_TRANSACTIONS = 1000000;
	private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {
		logger.info("Application is starting...");

		demonstrateDeadlock();
		demonstrateOrderedLocks();
		compareThroughput();

		logger.info("Application is closing...");
	}

	// ==================================================================
	// Part 2 - what could go wrong
	// ==================================================================

	private static void demonstrateDeadlock() {
		System.out.println("=== Part 2: locks taken sender-first (NAIVE_LOCKS) ===");
		System.out.println("Two accounts, two threads, opposite directions, at the same moment.\n");

		CryptoAccount a = new CryptoAccount("acc_A", INITIAL_BALANCE);
		CryptoAccount b = new CryptoAccount("acc_B", INITIAL_BALANCE);

		// Widen the window between taking the first and the second lock so the
		// race becomes a certainty instead of a coin flip.
		Transaction.deadlockWindowMillis = 100;

		Transaction aToB = new Transaction("tx_A_to_B", a, b, 100.0, Transaction.Mode.NAIVE_LOCKS);
		Transaction bToA = new Transaction("tx_B_to_A", b, a, 100.0, Transaction.Mode.NAIVE_LOCKS);

		// Daemon threads: if they deadlock they will never finish, and the JVM
		// must still be able to exit.
		Thread t1 = new Thread(aToB, "transfer-A-to-B");
		Thread t2 = new Thread(bToA, "transfer-B-to-A");
		t1.setDaemon(true);
		t2.setDaemon(true);

		t1.start();
		t2.start();

		boolean finished = joinWithin(t1, 3000) & joinWithin(t2, 3000);

		if (finished) {
			System.out.println("Both transfers completed - the race did not trigger this time.");
			System.out.println("Run it again; nothing about the code has been fixed.");
			logger.warn("Naive lock ordering completed without deadlocking this run");
		} else {
			System.out.println("NEITHER transfer completed after 3 seconds. Deadlocked.");
			reportDeadlockedThreads();
			System.out.println("\n  thread A-to-B holds acc_A's lock and is waiting for acc_B's");
			System.out.println("  thread B-to-A holds acc_B's lock and is waiting for acc_A's");
			System.out.println("  Neither will release what it holds. Circular wait.");
			logger.error("Deadlock reproduced with sender-first lock ordering");
		}

		Transaction.deadlockWindowMillis = 0;
		System.out.println();
	}

	/** Asks the JVM itself whether any threads are deadlocked. */
	private static void reportDeadlockedThreads() {
		ThreadMXBean threads = ManagementFactory.getThreadMXBean();
		long[] deadlocked = threads.findDeadlockedThreads();

		if (deadlocked == null) {
			System.out.println("  (the JVM did not report a deadlock cycle)");
			return;
		}

		System.out.println("\n  The JVM confirms it. ThreadMXBean.findDeadlockedThreads() names:");
		for (long id : deadlocked) {
			System.out.println("    - " + threads.getThreadInfo(id).getThreadName());
		}
	}

	// ==================================================================
	// Part 3 - resource ordering
	// ==================================================================

	private static void demonstrateOrderedLocks() {
		System.out.println("=== Part 3: locks taken in ascending account order (ORDERED_LOCKS) ===");
		System.out.println("Exactly the same scenario, with the same 100 ms window.\n");

		CryptoAccount a = new CryptoAccount("acc_A", INITIAL_BALANCE);
		CryptoAccount b = new CryptoAccount("acc_B", INITIAL_BALANCE);

		Transaction.deadlockWindowMillis = 100;

		Transaction aToB = new Transaction("tx_A_to_B", a, b, 100.0, Transaction.Mode.ORDERED_LOCKS);
		Transaction bToA = new Transaction("tx_B_to_A", b, a, 100.0, Transaction.Mode.ORDERED_LOCKS);

		Thread t1 = new Thread(aToB, "transfer-A-to-B");
		Thread t2 = new Thread(bToA, "transfer-B-to-A");
		t1.setDaemon(true);
		t2.setDaemon(true);

		t1.start();
		t2.start();

		boolean finished = joinWithin(t1, 3000) & joinWithin(t2, 3000);

		if (finished) {
			System.out.println("Both transfers completed.");
			System.out.println("  Both threads wanted acc_A and acc_B. Both took acc_A first,");
			System.out.println("  because acc_A < acc_B. So one waited at the door instead of");
			System.out.println("  inside the room, and there was no cycle to form.");
			System.out.printf("%n  %s%n  %s%n", a, b);
			System.out.printf("  total: %.2f (started at %.2f)%n",
					a.getBalance() + b.getBalance(), 2 * INITIAL_BALANCE);
		} else {
			System.out.println("Still stuck - that would be a bug in the ordering.");
			logger.error("Ordered locking deadlocked, which should be impossible");
		}

		Transaction.deadlockWindowMillis = 0;
		System.out.println();
	}

	// ==================================================================
	// What ordering buys over one global monitor
	// ==================================================================

	private static void compareThroughput() {
		System.out.println("=== Ordered locks vs per-account synchronized ===");
		System.out.println(BENCHMARK_TRANSACTIONS + " transactions on a fixed pool of "
				+ POOL_SIZE + " threads\n");

		for (int accounts : new int[] { 1000, 10 }) {
			TransferBenchmark.Result sync = TransferBenchmark.run(
					"synchronized",
					() -> Executors.newFixedThreadPool(POOL_SIZE),
					accounts, BENCHMARK_TRANSACTIONS, INITIAL_BALANCE,
					Transaction.Mode.SYNCHRONIZED);

			TransferBenchmark.Result ordered = TransferBenchmark.run(
					"ordered locks",
					() -> Executors.newFixedThreadPool(POOL_SIZE),
					accounts, BENCHMARK_TRANSACTIONS, INITIAL_BALANCE,
					Transaction.Mode.ORDERED_LOCKS);

			System.out.println(sync);
			System.out.println(ordered);
			System.out.println();
		}
	}

	// ==================================================================

	/** @return true if the thread finished inside the timeout */
	private static boolean joinWithin(Thread thread, long millis) {
		try {
			thread.join(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return !thread.isAlive();
	}
}
