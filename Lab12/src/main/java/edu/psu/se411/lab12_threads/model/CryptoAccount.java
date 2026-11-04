package edu.psu.se411.lab12_threads.model;

import java.util.concurrent.locks.ReentrantLock;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;

/**
 * One account in the crypto-currency system.
 *
 * <p>Carries two different protection mechanisms on purpose, so the lab can
 * compare them:
 * <ul>
 *   <li>{@code synchronized} on {@link #addToBalance(Double)} and
 *       {@link #retrieveFromBalance(Double)} - the Lab 11 solution, kept from
 *       the starter code;</li>
 *   <li>a per-account {@link ReentrantLock}, used by the lock-based transaction
 *       strategies added in Lab 12.</li>
 * </ul>
 */
public class CryptoAccount {

	private String id;
	private Double balance;

	/**
	 * This account's own lock, for the Lab 12 strategies.
	 *
	 * Reentrant, so taking it twice on the same thread is harmless.
	 */
	private final ReentrantLock lock = new ReentrantLock();

	public CryptoAccount(String id, Double balance) {
		if (balance < 0) {
			throw new IllegalArgumentException("Account balance cannot be negative");
		}
		this.id = id;
		this.balance = balance;
	}

	public synchronized void addToBalance(Double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount added to balance cannot be negative");
		}
		balance += amount;
	}

	public synchronized void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
		if (amount > balance) {
			throw new InsufficientBalanceException(id);
		}
		balance -= amount;
	}

	/**
	 * The same two operations with no {@code synchronized} at all.
	 *
	 * The lock-based strategies already hold both account locks for the whole
	 * transfer, so going through the synchronized methods would take a second,
	 * redundant monitor on every call. These are what they use instead.
	 */
	public void addToBalanceUnguarded(Double amount) {
		balance += amount;
	}

	public void retrieveFromBalanceUnguarded(Double amount) throws InsufficientBalanceException {
		if (amount > balance) {
			throw new InsufficientBalanceException(id);
		}
		balance -= amount;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	/**
	 * The key that defines the global order of accounts.
	 *
	 * Resource ordering only works if every thread ranks accounts the same way,
	 * and the ranking has to be <b>total</b>: for any two distinct accounts, one
	 * is strictly before the other. The id does that.
	 */
	public String getOrderKey() {
		return id;
	}

	public String getId() {
		return id;
	}

	public Double getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return String.format("%s = %.2f", id, balance);
	}
}
