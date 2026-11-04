package edu.psu.se411.lab09_inventory.model;

/**
 * Anything the inventory can hold.
 *
 * The two things every product has - an id and a name - live here once, with
 * their validation. A subclass adds only its own extra field and says what kind
 * of thing it is.
 *
 * Abstract because "a plain Item" is not a real product: every item in the
 * catalogue is a book, a device, or some future type.
 */
public abstract class Item {

	private final String id;
	private final String name;

	protected Item(String id, String name) {
		this.id = requireText(id, "id");
		this.name = requireText(name, "name");
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/** The kind of product, for display: "Book", "Electronic device". */
	public abstract String getType();

	/** The extra information this item type carries. */
	public abstract String getDetails();

	protected static String requireText(String value, String field) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(field + " must not be empty");
		}
		return value.trim();
	}

	@Override
	public String toString() {
		return String.format("%-18s %-8s %-34s %s", getType(), id, name, getDetails());
	}
}
