package edu.psu.se411.lab07.service;

import java.util.List;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import edu.psu.se411.lab07.model.Booking;

/**
 * The agency's pricing service.
 *
 * This is where the lab's polymorphism requirement is satisfied:
 * {@link #computeTotalPrice(Booking)} takes the abstract type and works for
 * any booking. There is no {@code instanceof}, no cast and no switch on the
 * booking type anywhere in this class - the JVM dispatches to the right
 * implementation at run time.
 *
 * Adding a fourth transport mode (a bus, a ferry) means writing one new
 * subclass. Nothing in this file changes.
 */
public class BookingService {

	/**
	 * Prices any booking, whatever its concrete type.
	 */
	public double computeTotalPrice(Booking booking)
			throws MissingInformationException, InvalidArgumentException {

		if (booking == null) {
			throw new IllegalArgumentException("Booking must not be null");
		}
		return booking.computeTotalPrice();
	}

	/**
	 * Prices a whole mixed list of bookings and returns what the agency can
	 * currently invoice.
	 *
	 * Bookings that cannot be priced yet are reported through the collector
	 * rather than aborting the run, so one incomplete booking does not stop the
	 * agency from billing the rest.
	 *
	 * @param bookings a list holding any mixture of booking types
	 * @param failures called for each booking that could not be priced
	 * @return the sum of the prices that could be computed
	 */
	public double computeTotalForAll(List<Booking> bookings, FailureHandler failures) {
		double total = 0.0;

		for (Booking booking : bookings) {
			try {
				total += computeTotalPrice(booking);
			} catch (MissingInformationException | InvalidArgumentException e) {
				failures.onFailure(booking, e);
			}
		}

		return total;
	}

	/** What the caller wants done with a booking that could not be priced. */
	@FunctionalInterface
	public interface FailureHandler {
		void onFailure(Booking booking, Exception cause);
	}
}
