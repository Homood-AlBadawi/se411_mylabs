package edu.psu.se411.lab07.model;

import java.time.LocalDate;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

/**
 * Everything every booking has in common.
 *
 * The four shared fields, their validation, {@code toString} and the two
 * validation helpers live here once. A subclass only has to describe what makes
 * it different: its own extra data and its own pricing rule. That is the "code
 * reuse" the lab asks for.
 *
 * {@link #computeTotalPrice()} is abstract, which is what makes polymorphism
 * work: a caller holding a {@code Booking} reference calls it without knowing
 * or caring which subclass it really has.
 */
public abstract class Booking {

	private final String bookingId;
	private final String customerFullName;
	private final LocalDate dateOfTravel;
	private final String destinationCity;

	protected Booking(String bookingId, String customerFullName, LocalDate dateOfTravel,
			String destinationCity) {

		this.bookingId = requireText(bookingId, "booking ID");
		this.customerFullName = requireText(customerFullName, "customer full name");
		this.destinationCity = requireText(destinationCity, "destination city");

		if (dateOfTravel == null) {
			throw new IllegalArgumentException("Date of travel must not be null");
		}
		this.dateOfTravel = dateOfTravel;
	}

	// ==================================================================
	// The polymorphic part
	// ==================================================================

	/**
	 * The total price of this booking, computed by whatever rule the concrete
	 * booking type uses.
	 *
	 * @throws MissingInformationException if a value entered later is still absent
	 * @throws InvalidArgumentException    if a supplied value is out of range
	 */
	public abstract double computeTotalPrice()
			throws MissingInformationException, InvalidArgumentException;

	/** Human-readable name of the booking type, used in reports and logs. */
	public abstract String getBookingType();

	/** The extra data this booking type carries, for display. */
	public abstract String getDetails();

	// ==================================================================
	// Shared validation helpers - used by every subclass
	// ==================================================================

	/**
	 * Fails when a value the customer or the system supplies later is still
	 * missing.
	 */
	protected static void requireProvided(Object value, String fieldName)
			throws MissingInformationException {
		if (value == null) {
			throw new MissingInformationException(fieldName);
		}
	}

	/**
	 * Fails when a supplied value falls outside the agency's allowed range.
	 */
	protected static void requireInRange(double value, double min, double max, String fieldName)
			throws InvalidArgumentException {
		if (value < min || value > max) {
			throw new InvalidArgumentException(fieldName, value, min, max);
		}
	}

	/** Constructor-time check for the shared text fields. */
	private static String requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " must not be empty");
		}
		return value.trim();
	}

	// ==================================================================
	// Shared accessors
	// ==================================================================

	public String getBookingId() {
		return bookingId;
	}

	public String getCustomerFullName() {
		return customerFullName;
	}

	public LocalDate getDateOfTravel() {
		return dateOfTravel;
	}

	public String getDestinationCity() {
		return destinationCity;
	}

	@Override
	public String toString() {
		return String.format("%s[%s] %s to %s on %s - %s",
				getBookingType(), bookingId, customerFullName, destinationCity,
				dateOfTravel, getDetails());
	}
}
