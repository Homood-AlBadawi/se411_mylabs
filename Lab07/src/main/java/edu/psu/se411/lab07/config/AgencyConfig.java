package edu.psu.se411.lab07.config;

/**
 * Global configuration for the travel agency.
 *
 * Every rate and every business limit lives here, so changing a price or a
 * validation bound is a one-line edit in one file rather than a hunt through
 * the booking classes. The class cannot be instantiated.
 */
public final class AgencyConfig {

	// ---------------- Flight ----------------

	/** Charged per kilogram of luggage, on top of the base ticket price. */
	public static final double EXTRA_LUGGAGE_RATE = 25.00;

	public static final double MIN_LUGGAGE_WEIGHT = 0.0;
	public static final double MAX_LUGGAGE_WEIGHT = 40.0;

	// ---------------- Train ----------------

	/** Charged per kilometre in standard seating. */
	public static final double TRAIN_STANDARD_RATE = 0.35;

	/** Charged per kilometre in first class. */
	public static final double TRAIN_FIRST_CLASS_RATE = 0.60;

	public static final double MIN_TRAIN_DISTANCE = 1.0;
	public static final double MAX_TRAIN_DISTANCE = 2000.0;

	// ---------------- Car rental ----------------

	public static final int MIN_RENTAL_DAYS = 1;
	public static final int MAX_RENTAL_DAYS = 30;

	// ---------------- General ----------------

	/** Currency label used when printing prices. */
	public static final String CURRENCY = "SAR";

	private AgencyConfig() {
		throw new AssertionError("AgencyConfig is a constants holder and must not be instantiated");
	}
}
