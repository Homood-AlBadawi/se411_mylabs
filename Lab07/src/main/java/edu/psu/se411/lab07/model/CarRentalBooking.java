package edu.psu.se411.lab07.model;

import java.time.LocalDate;

import edu.psu.se411.lab07.config.AgencyConfig;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

/**
 * A car rental.
 *
 * Daily rate is known when the booking is made; the number of days is entered
 * by the customer later, so it is an {@code Integer} and starts as null.
 *
 * Total price = daily rate x number of days
 */
public class CarRentalBooking extends Booking {

	private final double dailyRate;

	/** Null until the customer chooses how long to keep the car. */
	private Integer numberOfDays;

	public CarRentalBooking(String bookingId, String customerFullName, LocalDate dateOfTravel,
			String destinationCity, double dailyRate) {

		super(bookingId, customerFullName, dateOfTravel, destinationCity);

		if (dailyRate < 0) {
			throw new IllegalArgumentException("Daily rate must not be negative: " + dailyRate);
		}
		this.dailyRate = dailyRate;
	}

	/** Entered later by the customer. */
	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	public Integer getNumberOfDays() {
		return numberOfDays;
	}

	public double getDailyRate() {
		return dailyRate;
	}

	@Override
	public double computeTotalPrice() throws MissingInformationException, InvalidArgumentException {
		requireProvided(numberOfDays, "number of rental days");
		requireInRange(numberOfDays, AgencyConfig.MIN_RENTAL_DAYS,
				AgencyConfig.MAX_RENTAL_DAYS, "number of rental days");

		return dailyRate * numberOfDays;
	}

	@Override
	public String getBookingType() {
		return "Car rental";
	}

	@Override
	public String getDetails() {
		return String.format("%.2f %s/day, %s",
				dailyRate, AgencyConfig.CURRENCY,
				numberOfDays == null ? "duration not set" : numberOfDays + " day(s)");
	}
}
