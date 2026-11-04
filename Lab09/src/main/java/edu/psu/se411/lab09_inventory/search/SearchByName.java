package edu.psu.se411.lab09_inventory.search;

import edu.psu.se411.lab09_inventory.model.Item;

/**
 * Matches an item whose name contains the given text, ignoring case.
 *
 * Also declared over {@code Item}, so any inventory of items can use it.
 */
public class SearchByName implements SearchStrategy<Item> {

	private final String nameFragment;

	public SearchByName(String nameFragment) {
		this.nameFragment = nameFragment == null ? "" : nameFragment.trim();
	}

	@Override
	public boolean matches(Item item) {
		return item.getName().toLowerCase().contains(nameFragment.toLowerCase());
	}
}
