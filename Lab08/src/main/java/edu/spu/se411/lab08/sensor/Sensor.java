package edu.spu.se411.lab08.sensor;

import edu.spu.se411.lab08.observer.AbstractSubject;

/**
 * A sensor: a subject whose state is a single numeric reading.
 *
 * Inherits the whole observer mechanism from {@link AbstractSubject} and adds
 * only what a sensor has: a name and a reading. The one rule every sensor
 * follows is in {@link #setReading(double)} - change the state, then notify.
 *
 * Subclasses supply the two things that differ between sensor kinds: the
 * quantity measured and the unit it is measured in.
 */
public abstract class Sensor extends AbstractSubject {

	private final String name;
	private double reading;

	protected Sensor(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Sensor name must not be empty");
		}
		this.name = name.trim();
	}

	/**
	 * Records a new reading and tells every registered observer.
	 *
	 * This is the only place a sensor's state changes, so it is the only place
	 * that has to remember to notify.
	 */
	public void setReading(double reading) {
		this.reading = reading;
		notifyObservers();
	}

	public double getReading() {
		return reading;
	}

	public String getName() {
		return name;
	}

	/** What this sensor measures, e.g. "Temperature". */
	public abstract String getQuantity();

	/** The unit of {@link #getReading()}, e.g. "C". */
	public abstract String getUnit();

	/**
	 * Covariant override so callers get a {@code Sensor} back without casting.
	 * The observer list is emptied by {@link AbstractSubject#clone()}.
	 */
	@Override
	public Sensor clone() throws CloneNotSupportedException {
		return (Sensor) super.clone();
	}

	@Override
	public String toString() {
		return String.format("%s (%s) = %.2f %s", name, getQuantity(), reading, getUnit());
	}
}
