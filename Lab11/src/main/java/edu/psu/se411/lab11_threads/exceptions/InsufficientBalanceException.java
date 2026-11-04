package edu.psu.se411.lab11_threads.exceptions;

/**
 * Thrown when an account is asked for more than it holds.
 */
public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException(String message) {
		super(message);
	}
}
