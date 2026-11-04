package edu.psu.se411.lab09_inventory.model;

/**
 * A book: an {@link Item} that also has an author.
 */
public class Book extends Item {

	private final String authorName;

	public Book(String id, String name, String authorName) {
		super(id, name);
		this.authorName = requireText(authorName, "authorName");
	}

	public String getAuthorName() {
		return authorName;
	}

	@Override
	public String getType() {
		return "Book";
	}

	@Override
	public String getDetails() {
		return "by " + authorName;
	}
}
