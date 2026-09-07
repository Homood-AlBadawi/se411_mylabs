package edu.psu.se411.exceptions;

/**
 * Exercise 1 - thrown when an age is below the minimum allowed age (18).
 *
 * It extends Exception rather than RuntimeException, so it is a CHECKED
 * exception: the compiler forces every caller either to catch it or to declare
 * it with "throws". That is the point of the exercise - the error cannot be
 * ignored by accident.
 */
public class InvalidAgeException extends Exception {

	private static final long serialVersionUID = 1L;

	/** The age that was rejected, kept so the handler can report it. */
	private final int age;

	public InvalidAgeException(int age) {
		super("Invalid age: " + age + ". Age must be 18 or higher.");
		this.age = age;
	}

	public InvalidAgeException(String message, int age) {
		super(message);
		this.age = age;
	}

	public int getAge() {
		return age;
	}
}
