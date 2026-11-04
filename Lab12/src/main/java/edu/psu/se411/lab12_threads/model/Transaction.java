package edu.psu.se411.lab12_threads.model;

import java.util.concurrent.locks.ReentrantLock;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;

/**
 * A payment from one account to another, runnable three ways.
 *
 * <table border="1">
 *   <caption>Strategies</caption>
 *   <tr><th>Mode</th><th>Locking</th><th>Correct?</th></tr>
 *   <tr><td>{@code SYNCHRONIZED}</td><td>each account's monitor, one at a time</td>
 *       <td>total is conserved, but a transfer is not atomic</td></tr>
 *   <tr><td>{@code NAIVE_LOCKS}</td><td>both account locks, sender first</td>
 *       <td><b>no - deadlocks</b></td></tr>
 *   <tr><td>{@code ORDERED_LOCKS}</td><td>both account locks, lowest id first</td>
 *       <td>yes, atomic <i>and</i> concurrent</td></tr>
 * </table>
 */
public class Transaction implements Runnable {

	/** How this transaction protects itself. */
	public enum Mode {
		SYNCHRONIZED, NAIVE_LOCKS, ORDERED_LOCKS
	}

	/**
	 * Demo aid only. A pause between taking the first and the second lock makes
	 * the deadlock in {@code NAIVE_LOCKS} happen every time instead of once in
	 * a thousand runs. Left at 0 for the benchmarks.
	 */
	public static volatile long deadlockWindowMillis = 0;

	private String id;
	private CryptoAccount sender;
	private CryptoAccount receiver;
	private Double amount;
	private Mode mode = Mode.SYNCHRONIZED;

	public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
		super();
		if (amount < 0) {
			throw new IllegalArgumentException("Amount used in transaction cannot be negative");
		}
		if (id == null || sender == null || receiver == null || amount < 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount,
			Mode mode) {
		this(id, sender, receiver, amount);
		this.mode = mode;
	}

	@Override
	public void run() {
		try {
			switch (mode) {
				case NAIVE_LOCKS:
					executeWithNaiveLocks();
					break;
				case ORDERED_LOCKS:
					executeWithOrderedLocks();
					break;
				default:
					execute();
					break;
			}
		} catch (InsufficientBalanceException e) {
			// Sender could not pay, so nothing was credited and the system
			// total is unaffected. Skipping is the correct response.
		}
	}

	// ==================================================================
	// Lab 11 solution - per-account synchronized methods
	// ==================================================================

	/**
	 * The starter's version.
	 *
	 * Each half is atomic on its own account, which is enough to conserve the
	 * total: a debit that fails throws before any credit happens, and a debit
	 * that succeeds is always followed by a credit of the same amount.
	 *
	 * It is <b>not</b> enough to make the transfer atomic. Between the two
	 * calls the money is in neither account, so a reader can see a total that
	 * is short. No money is lost, but no snapshot is trustworthy either.
	 */
	public void execute() throws InsufficientBalanceException {
		if (sender == receiver) {
			throw new IllegalArgumentException(
					"Cannot make a transaction where the sender and the receiver are the same account");
		}

		sender.retrieveFromBalance(amount);
		receiver.addToBalance(amount);
	}

	// ==================================================================
	// Lab 12 part 2 - replace synchronized with locks. What could go wrong?
	// ==================================================================

	/**
	 * Both account locks, taken sender-first, held across the whole transfer.
	 *
	 * <p>That makes the transfer genuinely atomic - and <b>deadlocks</b>.
	 *
	 * <p>Thread 1 runs A&nbsp;&rarr;&nbsp;B: takes A's lock, then wants B's.
	 * Thread 2 runs B&nbsp;&rarr;&nbsp;A at the same moment: takes B's lock,
	 * then wants A's. Neither releases what it holds, so both wait forever.
	 * Two threads, two locks, opposite order: a circular wait.
	 *
	 * <p>Nothing here looks wrong, which is exactly the danger. Swapping
	 * {@code synchronized} for {@code Lock} buys real concurrency and silently
	 * introduces a failure mode that hangs the program instead of corrupting a
	 * number.
	 */
	public void executeWithNaiveLocks() throws InsufficientBalanceException {
		if (sender == receiver) {
			return;
		}

		ReentrantLock first = sender.getLock();
		ReentrantLock second = receiver.getLock();

		first.lock();
		try {
			pauseForDemo();

			second.lock();
			try {
				sender.retrieveFromBalanceUnguarded(amount);
				receiver.addToBalanceUnguarded(amount);
			} finally {
				second.unlock();
			}
		} finally {
			first.unlock();
		}
	}

	// ==================================================================
	// Lab 12 part 3 - resource ordering
	// ==================================================================

	/**
	 * Both account locks, always taken in ascending account order.
	 *
	 * <p>The fix is one decision: every thread acquires locks in the same
	 * global order. A circular wait then becomes impossible, because a cycle
	 * needs at least one thread travelling "down" the order while another
	 * travels "up". No cycle, no deadlock - guaranteed, not merely unlikely.
	 *
	 * <p>And unlike one global monitor, transfers touching different accounts
	 * still run in parallel.
	 */
	public void executeWithOrderedLocks() throws InsufficientBalanceException {
		if (sender == receiver) {
			return;
		}

		ReentrantLock firstLock;
		ReentrantLock secondLock;

		// THE ordering decision: the lower account id is always locked first,
		// whichever end of the transfer it happens to be on.
		if (sender.getOrderKey().compareTo(receiver.getOrderKey()) < 0) {
			firstLock = sender.getLock();
			secondLock = receiver.getLock();
		} else {
			firstLock = receiver.getLock();
			secondLock = sender.getLock();
		}

		firstLock.lock();
		try {
			pauseForDemo();

			secondLock.lock();
			try {
				sender.retrieveFromBalanceUnguarded(amount);
				receiver.addToBalanceUnguarded(amount);
			} finally {
				secondLock.unlock();
			}
		} finally {
			firstLock.unlock();
		}
	}

	// ==================================================================

	private static void pauseForDemo() {
		long pause = deadlockWindowMillis;
		if (pause > 0) {
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public CryptoAccount getSender() {
		return sender;
	}

	public void setSender(CryptoAccount sender) {
		this.sender = sender;
	}

	public CryptoAccount getReceiver() {
		return receiver;
	}

	public void setReceiver(CryptoAccount receiver) {
		this.receiver = receiver;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}
}
