package edu.psu.se411.lab09_inventory.search;

import edu.psu.se411.lab09_inventory.model.ElectronicDevice;

/**
 * Matches an electronic device by category. Typed to
 * {@code ElectronicDevice} for the same reason {@link SearchByAuthor} is typed
 * to {@code Book}.
 */
public class SearchByCategory implements SearchStrategy<ElectronicDevice> {

	private final String category;

	public SearchByCategory(String category) {
		this.category = category == null ? "" : category.trim();
	}

	@Override
	public boolean matches(ElectronicDevice device) {
		return device.getCategory().equalsIgnoreCase(category);
	}
}
