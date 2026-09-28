package edu.psu.se411.lab07.exceptions;

/**
 * Thrown when a booking is priced before the customer (or the system) has
 * supplied a value that is only known later - luggage weight, travel distance,
 * number of rental days.
 *
 * Checked, because the caller can always recover: ask for the missing value.
 */
public class MissingInformationException extends Exception {

	private static final long serialVersionUID = 1L;

	/** The field that was not supplied, so a handler can prompt for it. */
	private final String fieldName;

	public MissingInformationException(String fieldName) {
		super("Missing information: '" + fieldName + "' has not been provided yet.");
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
