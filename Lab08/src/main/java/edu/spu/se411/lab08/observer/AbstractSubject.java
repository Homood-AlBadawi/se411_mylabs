package edu.spu.se411.lab08.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The reusable half of the Observer pattern.
 *
 * Registering, unregistering and notifying are identical for every subject that
 * will ever exist, so they are written once here. A concrete subject inherits
 * all of it and only has to remember to call {@link #notifyObservers()} when its
 * own state changes.
 *
 * <p><b>Cloning.</b> The class is {@link Cloneable} and its {@code clone()}
 * deliberately gives the copy a <b>fresh, empty</b> observer list. A shallow
 * {@code super.clone()} would hand the copy the <i>same</i> list object, so
 * registering an observer on the clone would also register it on the original -
 * exactly what the lab tells us to avoid.
 */
public abstract class AbstractSubject implements Subject, Cloneable {

	/** Not final: {@link #clone()} has to replace it on the copy. */
	private List<Observer> observers = new ArrayList<Observer>();

	@Override
	public void register(Observer o) {
		if (o == null) {
			throw new IllegalArgumentException("Observer must not be null");
		}
		if (!observers.contains(o)) {
			observers.add(o);
		}
	}

	@Override
	public void unregister(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		// Iterate over a copy: an observer is allowed to unregister itself
		// while being notified without causing ConcurrentModificationException.
		for (Observer o : new ArrayList<Observer>(observers)) {
			o.update(this);
		}
	}

	/** How many observers are currently subscribed - used by the demo. */
	public int getObserverCount() {
		return observers.size();
	}

	/**
	 * A copy that starts life with <b>no observers of its own</b>.
	 *
	 * @return a shallow copy of this subject, with an empty observer list
	 */
	@Override
	public AbstractSubject clone() throws CloneNotSupportedException {
		AbstractSubject copy = (AbstractSubject) super.clone();
		copy.observers = new ArrayList<Observer>();
		return copy;
	}
}
