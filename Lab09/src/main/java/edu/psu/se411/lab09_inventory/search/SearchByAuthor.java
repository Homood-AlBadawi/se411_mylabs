package edu.psu.se411.lab09_inventory.search;

import edu.psu.se411.lab09_inventory.model.Book;

/**
 * Matches a book by author.
 *
 * Declared over {@code Book}, not {@code Item}: asking an electronic device for
 * its author is meaningless, and the type system should say so. The compiler
 * therefore refuses to hand this strategy to an {@code Inventory<Item>} - which
 * is correct, not an inconvenience.
 */
public class SearchByAuthor implements SearchStrategy<Book> {

	private final String authorFragment;

	public SearchByAuthor(String authorFragment) {
		this.authorFragment = authorFragment == null ? "" : authorFragment.trim();
	}

	@Override
	public boolean matches(Book book) {
		return book.getAuthorName().toLowerCase().contains(authorFragment.toLowerCase());
	}
}
