package edu.psu.se411.exceptions;

/**
 * Exercise 2 - thrown when a withdrawal is larger than the wallet balance.
 *
 * The exception carries the numbers that caused it (requested amount and
 * available balance) instead of only a message string. A handler can then
 * decide what to do - show the shortfall, offer a partial withdrawal - without
 * having to parse text.
 */
public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = 1L;

	private final double requested;
	private final double available;

	public InsufficientFundsException(double requested, double available) {
		super(String.format(
				"Insufficient funds: tried to withdraw %.2f but only %.2f is available (short by %.2f).",
				requested, available, requested - available));
		this.requested = requested;
		this.available = available;
	}

	public double getRequested() {
		return requested;
	}

	public double getAvailable() {
		return available;
	}

	/** How much the wallet is short. */
	public double getShortfall() {
		return requested - available;
	}
}
