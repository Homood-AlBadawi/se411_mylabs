package edu.psu.se411.model;

/**
 * Exercise 2 - the destination of a wallet withdrawal.
 */
public class BankAccount {

	private final String accountNumber;
	private double balance;

	public BankAccount(String accountNumber, double openingBalance) {
		if (accountNumber == null || accountNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("Account number must not be empty");
		}
		if (openingBalance < 0) {
			throw new IllegalArgumentException("Opening balance must not be negative");
		}
		this.accountNumber = accountNumber;
		this.balance = openingBalance;
	}

	public void deposit(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Deposit amount must be positive");
		}
		balance += amount;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return String.format("BankAccount[%s] balance = %.2f", accountNumber, balance);
	}
}
