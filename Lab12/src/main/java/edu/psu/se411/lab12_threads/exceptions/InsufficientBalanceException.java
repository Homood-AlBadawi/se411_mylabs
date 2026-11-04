package edu.psu.se411.lab12_threads.exceptions;

public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException(String account_id) {
		super("Insufficient balance in account " + account_id);
	}
}
