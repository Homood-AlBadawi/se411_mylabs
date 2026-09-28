package edu.psu.se411.lab07.exceptions;

/**
 * Thrown when a value was supplied but falls outside the range the agency
 * allows - luggage over 40 kg, a train journey longer than 2000 km, a rental
 * of 31 days.
 *
 * Deliberately a separate type from {@link MissingInformationException}: "you
 * did not tell me yet" and "what you told me is not allowed" need different
 * responses from the caller, so they must be distinguishable in a catch block.
 */
public class InvalidArgumentException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String fieldName;
	private final double value;
	private final double min;
	private final double max;

	public InvalidArgumentException(String fieldName, double value, double min, double max) {
		super(String.format("Invalid %s: %.2f is outside the allowed range %.2f to %.2f.",
				fieldName, value, min, max));
		this.fieldName = fieldName;
		this.value = value;
		this.min = min;
		this.max = max;
	}

	public String getFieldName() {
		return fieldName;
	}

	public double getValue() {
		return value;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
