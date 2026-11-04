package edu.psu.se411.lab09_inventory.model;

/**
 * An electronic device: an {@link Item} that also has a category.
 */
public class ElectronicDevice extends Item {

	private final String category;

	public ElectronicDevice(String id, String name, String category) {
		super(id, name);
		this.category = requireText(category, "category");
	}

	public String getCategory() {
		return category;
	}

	@Override
	public String getType() {
		return "Electronic device";
	}

	@Override
	public String getDetails() {
		return "category: " + category;
	}
}
