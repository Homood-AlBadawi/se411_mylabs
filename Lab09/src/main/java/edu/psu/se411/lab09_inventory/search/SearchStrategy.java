package edu.psu.se411.lab09_inventory.search;

/**
 * Part 2 - the Strategy interface.
 *
 * One method, so it is a <b>functional interface</b> and can be written as a
 * lambda wherever a {@code SearchStrategy} is expected. The annotation makes the
 * compiler enforce that: adding a second abstract method would not compile.
 *
 * <p>The Strategy pattern at work: "how to decide whether an item matches" is a
 * behaviour that varies, so it is pulled out of the inventory and packaged as an
 * object. The inventory never grows an {@code if} per search kind.
 *
 * @param <T> the type of item this strategy can examine
 */
@FunctionalInterface
public interface SearchStrategy<T> {

	/** True when the item satisfies this criterion. */
	boolean matches(T item);
}
