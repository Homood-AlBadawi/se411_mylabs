package edu.spu.se411.lab08.observers;

import edu.spu.se411.lab08.observer.Observer;
import edu.spu.se411.lab08.observer.Subject;
import edu.spu.se411.lab08.sensor.Sensor;

/**
 * The reusable half of an observer that watches sensors.
 *
 * The handout fixes the callback as {@code update(Subject)}, which carries no
 * reading. So every sensor observer has to do the same two things first: check
 * the subject really is a {@link Sensor}, and narrow to it. Doing that in each
 * observer would be copy-paste, so it happens once here and subclasses get the
 * already-narrowed {@link #onReading(Sensor)} instead.
 *
 * <p>{@code update} is {@code final}: subclasses customise the reaction, never
 * the narrowing.
 */
public abstract class SensorObserver implements Observer {

	private final String observerName;

	protected SensorObserver(String observerName) {
		if (observerName == null || observerName.trim().isEmpty()) {
			throw new IllegalArgumentException("Observer name must not be empty");
		}
		this.observerName = observerName.trim();
	}

	@Override
	public final void update(Subject subject) {
		if (!(subject instanceof Sensor)) {
			// This observer only understands sensors; ignore anything else.
			return;
		}
		onReading((Sensor) subject);
	}

	/** What this particular observer does with a new reading. */
	protected abstract void onReading(Sensor sensor);

	public String getObserverName() {
		return observerName;
	}
}
