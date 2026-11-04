package edu.psu.se411.lab09_inventory.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.psu.se411.lab09_inventory.search.SearchStrategy;

/**
 * Part 1 - a type-safe container for products.
 *
 * Generic, so one class serves an {@code Inventory<Item>}, an
 * {@code Inventory<Book>} or an inventory of anything else, and the compiler
 * stops a device being added to a book inventory. Before generics this would
 * have held {@code Object} and every read would need a cast that could fail at
 * run time.
 *
 * @param <T> the type of item this inventory holds
 */
public class Inventory<T> {

	private final List<T> items = new ArrayList<T>();

	public void addItem(T item) {
		if (item == null) {
			throw new IllegalArgumentException("Cannot add a null item to the inventory");
		}
		items.add(item);
	}

	/** @return true if the item was present and removed */
	public boolean removeItem(T item) {
		return items.remove(item);
	}

	/**
	 * Every item currently held.
	 *
	 * Returns an unmodifiable view, so a caller cannot bypass
	 * {@link #addItem(Object)} and its null check.
	 */
	public List<T> getAllItems() {
		return Collections.unmodifiableList(items);
	}

	public int size() {
		return items.size();
	}

	/**
	 * Part 2 - returns every item the strategy accepts.
	 *
	 * <p>The parameter is {@code SearchStrategy<? super T>}, not
	 * {@code SearchStrategy<T>}. A strategy that can examine any {@code Item}
	 * is perfectly able to examine a {@code Book}, so an
	 * {@code Inventory<Book>} should be allowed to use it. The lower-bounded
	 * wildcard is what permits that; with a plain {@code SearchStrategy<T>},
	 * {@code SearchByName} would have to be duplicated for every item type.
	 *
	 * <p>This is the "consumer super" half of PECS: the strategy <i>consumes</i>
	 * items.
	 *
	 * @param strategy how to decide whether an item matches
	 * @return the matching items, in insertion order; never null
	 */
	public List<T> findItems(SearchStrategy<? super T> strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException("Search strategy must not be null");
		}

		List<T> matches = new ArrayList<T>();
		for (T item : items) {
			if (strategy.matches(item)) {
				matches.add(item);
			}
		}
		return matches;
	}
}
