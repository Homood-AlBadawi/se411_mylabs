package edu.psu.se411.lab07;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab07.config.AgencyConfig;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import edu.psu.se411.lab07.model.Booking;
import edu.psu.se411.lab07.model.CarRentalBooking;
import edu.psu.se411.lab07.model.FlightBooking;
import edu.psu.se411.lab07.model.SeatClass;
import edu.psu.se411.lab07.model.TrainBooking;
import edu.psu.se411.lab07.service.BookingService;

/**
 * SE411 - Lab 07: Polymorphism in Java.
 *
 * Travel agency booking system. Run with: clean package exec:java
 */
public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private static final BookingService service = new BookingService();

	public static void main(String[] args) {

		logger.info("Application is starting...");

		try {
			List<Booking> bookings = buildBookings();

			priceEachBooking(bookings);
			priceWholeList(bookings);
			demonstrateFailures();

		} catch (RuntimeException e) {
			// Nothing should reach here; if it does, the log must say so.
			logger.error("Unexpected failure in the booking run", e);
		}

		logger.info("Application is ending...");
	}

	// ==================================================================
	// Building a mixed set of bookings
	// ==================================================================

	private static List<Booking> buildBookings() {
		List<Booking> bookings = new ArrayList<Booking>();

		// Known at booking time: base price. Entered later: luggage weight.
		FlightBooking flight = new FlightBooking("FL-001", "Homood AlBadawi",
				LocalDate.of(2026, 10, 12), "Istanbul", 1850.00);
		flight.setLuggageWeight(23.5);
		bookings.add(flight);

		// Known at booking time: seat class. Entered later: distance.
		TrainBooking train = new TrainBooking("TR-002", "Sara Al-Otaibi",
				LocalDate.of(2026, 10, 3), "Dammam", SeatClass.FIRST_CLASS);
		train.setDistanceInKm(450.0);
		bookings.add(train);

		TrainBooking economyTrain = new TrainBooking("TR-003", "Khalid Al-Harbi",
				LocalDate.of(2026, 10, 5), "Qassim", SeatClass.STANDARD);
		economyTrain.setDistanceInKm(450.0);
		bookings.add(economyTrain);

		// Known at booking time: daily rate. Entered later: number of days.
		CarRentalBooking car = new CarRentalBooking("CR-004", "Noura Al-Zahrani",
				LocalDate.of(2026, 10, 20), "Abha", 160.00);
		car.setNumberOfDays(6);
		bookings.add(car);

		return bookings;
	}

	// ==================================================================
	// Polymorphism: one loop, one call, three different pricing rules
	// ==================================================================

	private static void priceEachBooking(List<Booking> bookings) {
		System.out.println("=== Pricing every booking through one Booking reference ===");

		for (Booking booking : bookings) {
			try {
				// The variable's type is Booking. Which computeTotalPrice()
				// actually runs is decided at run time by the object itself.
				double price = service.computeTotalPrice(booking);

				System.out.printf("%-12s %-50s %10.2f %s%n",
						booking.getBookingId(), shorten(booking.toString()), price,
						AgencyConfig.CURRENCY);

			} catch (MissingInformationException | InvalidArgumentException e) {
				logger.error("Could not price booking {}", booking.getBookingId(), e);
				System.out.println(booking.getBookingId() + " -> " + e.getMessage());
			}
		}

		System.out.println();
	}

	private static void priceWholeList(List<Booking> bookings) {
		System.out.println("=== Agency total for the whole mixed list ===");

		double total = service.computeTotalForAll(bookings, (booking, cause) -> {
			logger.error("Skipped booking {} while totalling: {}",
					booking.getBookingId(), cause.getMessage());
			System.out.println("skipped " + booking.getBookingId() + ": " + cause.getMessage());
		});

		System.out.printf("Total: %.2f %s%n%n", total, AgencyConfig.CURRENCY);
	}

	// ==================================================================
	// Every business rule from the handout, exercised
	// ==================================================================

	private static void demonstrateFailures() {
		System.out.println("=== Business rules ===");

		// 1. Luggage weight never declared.
		FlightBooking noLuggage = new FlightBooking("FL-100", "Test Customer",
				LocalDate.of(2026, 11, 1), "Cairo", 900.00);
		attempt(noLuggage, "flight with no luggage weight declared");

		// 2. Luggage weight above the 40 kg limit.
		FlightBooking heavy = new FlightBooking("FL-101", "Test Customer",
				LocalDate.of(2026, 11, 1), "Cairo", 900.00);
		heavy.setLuggageWeight(55.0);
		attempt(heavy, "flight with 55 kg of luggage");

		// 3. Train distance never set.
		TrainBooking noDistance = new TrainBooking("TR-102", "Test Customer",
				LocalDate.of(2026, 11, 2), "Jeddah", SeatClass.STANDARD);
		attempt(noDistance, "train with no distance set");

		// 4. Train distance beyond 2000 km.
		TrainBooking tooFar = new TrainBooking("TR-103", "Test Customer",
				LocalDate.of(2026, 11, 2), "Jeddah", SeatClass.STANDARD);
		tooFar.setDistanceInKm(2500.0);
		attempt(tooFar, "train of 2500 km");

		// 5. Rental days never chosen.
		CarRentalBooking noDays = new CarRentalBooking("CR-104", "Test Customer",
				LocalDate.of(2026, 11, 3), "Taif", 200.00);
		attempt(noDays, "car rental with no duration");

		// 6. Rental longer than 30 days.
		CarRentalBooking tooLong = new CarRentalBooking("CR-105", "Test Customer",
				LocalDate.of(2026, 11, 3), "Taif", 200.00);
		tooLong.setNumberOfDays(45);
		attempt(tooLong, "car rental of 45 days");
	}

	/** Prices one booking and reports which exception, if any, it raised. */
	private static void attempt(Booking booking, String description) {
		try {
			double price = service.computeTotalPrice(booking);
			System.out.printf("%-42s -> priced at %.2f %s%n",
					description, price, AgencyConfig.CURRENCY);

		} catch (MissingInformationException e) {
			logger.warn("MissingInformationException for {}: {}",
					booking.getBookingId(), e.getMessage());
			System.out.printf("%-42s -> MissingInformationException: %s%n",
					description, e.getMessage());

		} catch (InvalidArgumentException e) {
			logger.warn("InvalidArgumentException for {}: {}",
					booking.getBookingId(), e.getMessage());
			System.out.printf("%-42s -> InvalidArgumentException: %s%n",
					description, e.getMessage());
		}
	}

	private static String shorten(String text) {
		return text.length() <= 50 ? text : text.substring(0, 47) + "...";
	}
}
