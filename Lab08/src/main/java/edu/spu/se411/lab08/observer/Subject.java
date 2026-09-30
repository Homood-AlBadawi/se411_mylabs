package edu.spu.se411.lab08.observer;

/**
 * Something observers can subscribe to.
 *
 * Given by the lab handout. The subject knows only that its observers implement
 * {@link Observer} - never which concrete classes they are. That is what makes
 * the one-to-many dependency loose: new kinds of observer can be added without
 * touching any subject.
 */
public interface Subject {

	/** Registers a new observer. */
	void register(Observer o);

	/** Unregisters an observer. */
	void unregister(Observer o);

	/** Notifies all registered observers of a data change. */
	void notifyObservers();
}
