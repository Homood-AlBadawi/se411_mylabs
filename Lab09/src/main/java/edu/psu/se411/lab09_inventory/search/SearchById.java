package edu.psu.se411.lab09_inventory.search;

import edu.psu.se411.lab09_inventory.model.Item;

/**
 * Matches an item by its exact id.
 *
 * Declared over {@code Item}, so it works on an {@code Inventory<Item>}, an
 * {@code Inventory<Book>} and an {@code Inventory<ElectronicDevice>} alike -
 * that is what {@code findItems(SearchStrategy<? super T>)} buys us.
 */
public class SearchById implements SearchStrategy<Item> {

	private final String id;

	public SearchById(String id) {
		this.id = id;
	}

	@Override
	public boolean matches(Item item) {
		return item.getId().equalsIgnoreCase(id);
	}
}
