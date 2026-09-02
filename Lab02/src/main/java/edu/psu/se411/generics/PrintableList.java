package edu.psu.se411.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exercise 1 - a generic list wrapper that knows how to print itself.
 *
 * @param <T> the type of the items stored in the list
 */
public class PrintableList<T> {

	private final List<T> items;

	/**
	 * Builds a PrintableList from an array of items.
	 *
	 * Arrays.asList wraps the array in a fixed-size List, so it is copied into
	 * an ArrayList to keep the list modifiable.
	 *
	 * @param itemsArray the items to store
	 */
	public PrintableList(T[] itemsArray) {
		this.items = new ArrayList<T>(Arrays.asList(itemsArray));
	}

	/**
	 * Prints every item of the list, one per line.
	 */
	public void printAll() {
		for (T item : items) {
			System.out.println(item);
		}
	}

	public List<T> getItems() {
		return items;
	}

	public int size() {
		return items.size();
	}
}
