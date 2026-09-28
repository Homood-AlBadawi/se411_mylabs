package edu.psu.se411.lab07.model;

import edu.psu.se411.lab07.config.AgencyConfig;

/**
 * Train seating options.
 *
 * Each constant carries its own per-kilometre rate, so pricing a train ticket
 * needs no if/else or switch on the seat class - the enum itself answers the
 * question. Adding a BUSINESS class later means adding one line here.
 */
public enum SeatClass {

	STANDARD(AgencyConfig.TRAIN_STANDARD_RATE),
	FIRST_CLASS(AgencyConfig.TRAIN_FIRST_CLASS_RATE);

	private final double ratePerKilometre;

	SeatClass(double ratePerKilometre) {
		this.ratePerKilometre = ratePerKilometre;
	}

	public double getRatePerKilometre() {
		return ratePerKilometre;
	}
}
