package edu.psu.se411.lab07.model;

import java.time.LocalDate;

import edu.psu.se411.lab07.config.AgencyConfig;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

/**
 * A train ride.
 *
 * Seat class is chosen when the booking is made; the distance is filled in by
 * the system later, so it is a {@code Double} and starts as null.
 *
 * Total price = distance x the rate carried by the chosen {@link SeatClass}.
 * No branching on the seat class is needed - the enum supplies its own rate.
 */
public class TrainBooking extends Booking {

	private final SeatClass seatClass;

	/** Null until the system computes the route. */
	private Double distanceInKm;

	public TrainBooking(String bookingId, String customerFullName, LocalDate dateOfTravel,
			String destinationCity, SeatClass seatClass) {

		super(bookingId, customerFullName, dateOfTravel, destinationCity);

		if (seatClass == null) {
			throw new IllegalArgumentException("Seat class must not be null");
		}
		this.seatClass = seatClass;
	}

	/** Entered later by the system. */
	public void setDistanceInKm(double distanceInKm) {
		this.distanceInKm = distanceInKm;
	}

	public Double getDistanceInKm() {
		return distanceInKm;
	}

	public SeatClass getSeatClass() {
		return seatClass;
	}

	@Override
	public double computeTotalPrice() throws MissingInformationException, InvalidArgumentException {
		requireProvided(distanceInKm, "distance in km");
		requireInRange(distanceInKm, AgencyConfig.MIN_TRAIN_DISTANCE,
				AgencyConfig.MAX_TRAIN_DISTANCE, "distance in km");

		return distanceInKm * seatClass.getRatePerKilometre();
	}

	@Override
	public String getBookingType() {
		return "Train";
	}

	@Override
	public String getDetails() {
		return String.format("%s, distance %s",
				seatClass,
				distanceInKm == null ? "not set" : String.format("%.1f km", distanceInKm));
	}
}
