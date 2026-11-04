package edu.psu.se411.lab12_threads.sim;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import edu.psu.se411.lab12_threads.model.CryptoAccount;
import edu.psu.se411.lab12_threads.model.Transaction;

/**
 * Runs one benchmark: N transactions over M accounts on a given executor.
 *
 * Shared by both main programs so that the fixed-pool and cached-pool runs
 * differ in exactly one thing - the executor - and the comparison means
 * something.
 */
public final class TransferBenchmark {

	/** What one run produced. */
    public static final class Result {
		public final String poolName;
		public final int accounts;
		public final int transactions;
		public final double initialTotal;
		public final double finalTotal;
		public final long millis;
		/** How many live threads the JVM peaked at during this run. */
		public final int peakThreads;

		Result(String poolName, int accounts, int transactions,
				double initialTotal, double finalTotal, long millis, int peakThreads) {
			this.poolName = poolName;
			this.accounts = accounts;
			this.transactions = transactions;
			this.initialTotal = initialTotal;
			this.finalTotal = finalTotal;
			this.millis = millis;
			this.peakThreads = peakThreads;
		}

		public boolean isConserved() {
			return Math.abs(finalTotal - initialTotal) < 0.000001;
		}

		@Override
		public String toString() {
			return String.format("%-14s %5d accounts %9d tx  %7d ms  peak %6d threads  total %s",
					poolName, accounts, transactions, millis, peakThreads,
					isConserved() ? "conserved" : "CORRUPTED");
		}
	}

	private TransferBenchmark() {
	}

	public static CryptoAccount[] createAccounts(int nbAccounts, double initialBalance) {
		CryptoAccount[] accounts = new CryptoAccount[nbAccounts];
		for (int i = 0; i < nbAccounts; i++) {
			accounts[i] = new CryptoAccount("acc_" + pad(i, nbAccounts), initialBalance);
		}
		return accounts;
	}

	/**
	 * Zero-pads the index so that the string ids sort in numeric order.
	 *
	 * Without this, "acc_10" sorts before "acc_9" - harmless for correctness
	 * (any total order works for resource ordering) but confusing to read.
	 */
	private static String pad(int i, int total) {
		int width = String.valueOf(total - 1).length();
		return String.format("%0" + width + "d", i);
	}

	public static double sumTotalMoneyInAccounts(CryptoAccount[] accounts) {
		double total = 0.0;
		for (CryptoAccount c : accounts) {
			total += c.getBalance();
		}
		return total;
	}

	/**
	 * Submits {@code nbTransactions} random transfers to a fresh executor and
	 * waits for every one of them to finish.
	 *
	 * @param poolFactory builds the executor under test
	 * @param poolName    label for the report
	 * @param mode        which locking strategy the transactions use
	 */
	public static Result run(String poolName, Supplier<ExecutorService> poolFactory,
			int nbAccounts, int nbTransactions, double initialBalance, Transaction.Mode mode) {

		CryptoAccount[] accounts = createAccounts(nbAccounts, initialBalance);
		double initialTotal = sumTotalMoneyInAccounts(accounts);

		// Peak thread count is the cheapest way to see what a pool actually did.
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		threadBean.resetPeakThreadCount();

		ExecutorService executor = poolFactory.get();
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < nbTransactions; i++) {
			int senderIndex = (int) (Math.random() * nbAccounts);
			int receiverIndex = (int) (Math.random() * nbAccounts);
			if (senderIndex == receiverIndex) {
				receiverIndex = (receiverIndex + 1) % nbAccounts;
			}

			Double amount = Math.random() * 100;
			executor.execute(new Transaction("tx_" + i,
					accounts[senderIndex], accounts[receiverIndex], amount, mode));
		}

		executor.shutdown();
		try {
			if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
				executor.shutdownNow();
				throw new IllegalStateException(
						poolName + " did not finish within 10 minutes - likely deadlocked");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			executor.shutdownNow();
		}

		long millis = System.currentTimeMillis() - startTime;
		int peakThreads = threadBean.getPeakThreadCount();
		double finalTotal = sumTotalMoneyInAccounts(accounts);

		return new Result(poolName, nbAccounts, nbTransactions,
				initialTotal, finalTotal, millis, peakThreads);
	}
}
