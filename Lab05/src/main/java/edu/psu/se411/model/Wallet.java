package edu.psu.se411.model;

import edu.psu.se411.exceptions.InsufficientFundsException;

/**
 * Exercise 2 - an online wallet the user can withdraw from into a bank account.
 */
public class Wallet {

	private final String owner;
	private double balance;

	public Wallet(String owner, double openingBalance) {
		if (owner == null || owner.trim().isEmpty()) {
			throw new IllegalArgumentException("Owner must not be empty");
		}
		if (openingBalance < 0) {
			throw new IllegalArgumentException("Opening balance must not be negative");
		}
		this.owner = owner;
		this.balance = openingBalance;
	}

	/**
	 * Moves money out of this wallet and into a bank account.
	 *
	 * The wallet is only debited after every check has passed, so a failed
	 * withdrawal leaves both the wallet and the account exactly as they were.
	 *
	 * @param account the account to transfer into
	 * @param amount  how much to withdraw, must be positive
	 * @throws InsufficientFundsException if the wallet balance is smaller than
	 *                                    the requested amount
	 */
	public void withdrawTo(BankAccount account, double amount) throws InsufficientFundsException {
		if (account == null) {
			throw new IllegalArgumentException("Destination account must not be null");
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Withdrawal amount must be positive");
		}
		if (amount > balance) {
			throw new InsufficientFundsException(amount, balance);
		}

		balance -= amount;
		account.deposit(amount);
	}

	public String getOwner() {
		return owner;
	}

	public double getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return String.format("Wallet[%s] balance = %.2f", owner, balance);
	}
}
