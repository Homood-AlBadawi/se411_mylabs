package edu.psu.se411;

import edu.psu.se411.exceptions.InsufficientFundsException;
import edu.psu.se411.exceptions.InvalidAgeException;
import edu.psu.se411.model.BankAccount;
import edu.psu.se411.model.Wallet;

/**
 * SE411 - Lab 05: Exception handling.
 *
 * Run with: clean -&gt; package -&gt; exec:java
 */
public class App {

	private static final int MINIMUM_AGE = 18;

	public static void main(String[] args) {
		exercise1();
		exercise2();
	}

	// ==================================================================
	// Exercise 1 - custom checked exception
	// ==================================================================

	/**
	 * Prints a message if the age is acceptable, otherwise refuses it.
	 *
	 * @param age the age to check
	 * @throws InvalidAgeException if age is below 18
	 */
	public static void validateAge(int age) throws InvalidAgeException {
		if (age < MINIMUM_AGE) {
			throw new InvalidAgeException(age);
		}
		System.out.println("Age valid message. (age = " + age + ")");
	}

	private static void exercise1() {
		System.out.println("=== Exercise 1: validateAge ===");

		int[] agesToTest = { 25, 18, 17, 0 };

		for (int age : agesToTest) {
			try {
				validateAge(age);
			} catch (InvalidAgeException e) {
				// The exception is handled here, so the loop carries on with
				// the next age instead of the program stopping.
				System.out.println("Rejected -> " + e.getMessage());
			}
		}

		System.out.println();
	}

	// ==================================================================
	// Exercise 2 - the online wallet
	// ==================================================================

	private static void exercise2() {
		System.out.println("=== Exercise 2: online wallet ===");

		Wallet wallet = new Wallet("Homood", 500.00);
		BankAccount account = new BankAccount("SA-1122334455", 0.00);

		System.out.println("Before  : " + wallet + " | " + account);

		attemptWithdrawal(wallet, account, 200.00);
		attemptWithdrawal(wallet, account, 1000.00);
		attemptWithdrawal(wallet, account, 300.00);
		attemptWithdrawal(wallet, account, -50.00);

		System.out.println("After   : " + wallet + " | " + account);
	}

	/**
	 * Performs one withdrawal and reports what happened.
	 *
	 * Each failure mode is caught separately: a business rule violation
	 * (InsufficientFundsException) is expected and reported calmly, while a
	 * programming error (IllegalArgumentException, e.g. a negative amount) is
	 * reported as a bad request. Catching them together would hide the
	 * difference.
	 */
	private static void attemptWithdrawal(Wallet wallet, BankAccount account, double amount) {
		try {
			wallet.withdrawTo(account, amount);
			System.out.printf("OK      : withdrew %.2f -> wallet %.2f, account %.2f%n",
					amount, wallet.getBalance(), account.getBalance());

		} catch (InsufficientFundsException e) {
			System.out.println("DECLINED: " + e.getMessage());
			System.out.printf("          (short by %.2f - nothing was transferred)%n", e.getShortfall());

		} catch (IllegalArgumentException e) {
			System.out.println("BAD INPUT: " + e.getMessage());
		}
	}
}
