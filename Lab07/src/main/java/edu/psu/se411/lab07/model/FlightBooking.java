package edu.psu.se411.lab07.model;

import java.time.LocalDate;

import edu.psu.se411.lab07.config.AgencyConfig;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

/**
 * A flight.
 *
 * Base ticket price is known when the booking is made; luggage weight is
 * entered by the customer later, so it is a {@code Double} and starts as null.
 *
 * Total price = base price + (luggage weight x extra-luggage rate)
 */
public class FlightBooking extends Booking {

	private final double basePrice;

	/** Null until the customer declares it. */
	private Double luggageWeight;

	public FlightBooking(String bookingId, String customerFullName, LocalDate dateOfTravel,
			String destinationCity, double basePrice) {

		super(bookingId, customerFullName, dateOfTravel, destinationCity);

		if (basePrice < 0) {
			throw new IllegalArgumentException("Base price must not be negative: " + basePrice);
		}
		this.basePrice = basePrice;
	}

	/** Entered later by the customer. */
	public void setLuggageWeight(double luggageWeight) {
		this.luggageWeight = luggageWeight;
	}

	public Double getLuggageWeight() {
		return luggageWeight;
	}

	public double getBasePrice() {
		return basePrice;
	}

	@Override
	public double computeTotalPrice() throws MissingInformationException, InvalidArgumentException {
		requireProvided(luggageWeight, "luggage weight");
		requireInRange(luggageWeight, AgencyConfig.MIN_LUGGAGE_WEIGHT,
				AgencyConfig.MAX_LUGGAGE_WEIGHT, "luggage weight");

		return basePrice + (luggageWeight * AgencyConfig.EXTRA_LUGGAGE_RATE);
	}

	@Override
	public String getBookingType() {
		return "Flight";
	}

	@Override
	public String getDetails() {
		return String.format("base %.2f %s, luggage %s",
				basePrice, AgencyConfig.CURRENCY,
				luggageWeight == null ? "not declared" : String.format("%.1f kg", luggageWeight));
	}
}
