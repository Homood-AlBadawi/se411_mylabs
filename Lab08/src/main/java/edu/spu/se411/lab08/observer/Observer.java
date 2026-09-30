package edu.spu.se411.lab08.observer;

/**
 * Something that wants to be told when a {@link Subject} changes.
 *
 * Given by the lab handout. Note the signature: the subject passes only
 * <em>itself</em>, not the new value. This is the classic "pull" variant of the
 * Observer pattern - the observer is told that something changed and then pulls
 * whatever state it actually cares about.
 */
public interface Observer {

	/** Updates the data with the new data. */
	void update(Subject subject);
}
