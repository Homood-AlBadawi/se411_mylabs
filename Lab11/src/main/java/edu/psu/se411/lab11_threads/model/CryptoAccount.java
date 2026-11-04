package edu.psu.se411.lab11_threads.model;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;

/**
 * One account in the crypto-currency system.
 *
 * <p><b>Deliberately not thread-safe.</b> Part 1 of the lab needs the race to
 * be visible, and {@code balance = balance + amount} is the classic
 * read-modify-write that produces it: a thread reads the balance, is paused by
 * the scheduler before it writes, and a second thread reads the same stale
 * value. One of the two updates is then lost.
 *
 * <p>The field is a {@code Double} (the wrapper, as in the starter code), so
 * every update also unboxes and re-boxes - widening the window still further.
 */
public class CryptoAccount {

	private String id;
	private Double balance;

	public CryptoAccount(String id, Double balance) {
		this.id = id;
		this.balance = balance;
	}

	/**
	 * Credits the account.
	 *
	 * Not atomic: read, add, write are three steps and a thread can be
	 * interrupted between any of them.
	 */
	public void addToBalance(Double amount) {
		balance = balance + amount;
	}

	/**
	 * Debits the account.
	 *
	 * @throws InsufficientBalanceException if the account does not hold enough
	 */
	public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
		if (amount > balance) {
			throw new InsufficientBalanceException(
					"Account " + id + " holds " + balance + " but " + amount + " was requested");
		}
		balance = balance - amount;
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
