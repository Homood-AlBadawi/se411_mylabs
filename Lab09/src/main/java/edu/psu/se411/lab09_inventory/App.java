package edu.psu.se411.lab09_inventory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab09_inventory.inventory.Inventory;
import edu.psu.se411.lab09_inventory.model.Book;
import edu.psu.se411.lab09_inventory.model.ElectronicDevice;
import edu.psu.se411.lab09_inventory.model.Item;
import edu.psu.se411.lab09_inventory.search.SearchByAuthor;
import edu.psu.se411.lab09_inventory.search.SearchByCategory;
import edu.psu.se411.lab09_inventory.search.SearchById;
import edu.psu.se411.lab09_inventory.search.SearchByName;
import edu.psu.se411.lab09_inventory.search.SearchStrategy;

/**
 * SE411 - Lab 09: managing an inventory with generics and the Strategy pattern.
 *
 * Run with: clean package exec:java
 */
public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		logger.info("Application is starting...");

		try {
			Inventory<Item> catalogue = buildCatalogue();

			partOne(catalogue);
			partTwo(catalogue);
			partThree(catalogue);

		} catch (RuntimeException e) {
			logger.error("Inventory run failed", e);
		}

		logger.info("Application is closing...");
	}

	// ==================================================================
	// Part 1 - a generic inventory holding two different product types
	// ==================================================================

	private static Inventory<Item> buildCatalogue() {
		Inventory<Item> catalogue = new Inventory<Item>();

		catalogue.addItem(new Book("B-001", "Concurrency in Java", "Brian Goetz"));
		catalogue.addItem(new Book("B-002", "Effective Java", "Joshua Bloch"));
		catalogue.addItem(new Book("B-003", "Clean Code", "Robert C. Martin"));

		catalogue.addItem(new ElectronicDevice("E-001", "MacBook Pro 14", "Laptop"));
		catalogue.addItem(new ElectronicDevice("E-002", "iPhone 17 Pro", "Phone"));
		catalogue.addItem(new ElectronicDevice("E-003", "Logitech MX Master", "Accessory"));

		// catalogue.addItem("a loose string");   // would not compile - that is
		//                                        // the point of Inventory<Item>
		return catalogue;
	}

	private static void partOne(Inventory<Item> catalogue) {
		System.out.println("=== Part 1: a generic Inventory<Item> ===");
		System.out.println("items in the catalogue: " + catalogue.size());

		Item toRemove = catalogue.getAllItems().get(catalogue.size() - 1);
		catalogue.removeItem(toRemove);
		System.out.println("removed " + toRemove.getName() + " -> " + catalogue.size() + " left");

		catalogue.addItem(toRemove);
		System.out.println("added it back -> " + catalogue.size() + "\n");
	}

	// ==================================================================
	// Part 2 - the Strategy pattern
	// ==================================================================

	private static void partTwo(Inventory<Item> catalogue) {
		System.out.println("=== Part 2: searching with strategies ===");

		// The call the handout asks for, verbatim.
		report("findItems(new SearchByName(\"Concurrency in Java\"))",
				catalogue.findItems(new SearchByName("Concurrency in Java")));

		report("findItems(new SearchById(\"E-002\"))",
				catalogue.findItems(new SearchById("E-002")));

		// Same strategy object, a different inventory - no new class needed.
		Inventory<Book> library = new Inventory<Book>();
		library.addItem(new Book("B-010", "Java Generics and Collections", "Maurice Naftalin"));
		library.addItem(new Book("B-011", "Java Puzzlers", "Joshua Bloch"));

		// SearchByName is a SearchStrategy<Item>, yet it works on an
		// Inventory<Book>. That is findItems(SearchStrategy<? super T>).
		report("library.findItems(new SearchByName(\"Java\"))  [Item strategy on a Book inventory]",
				library.findItems(new SearchByName("Java")));

		// A Book-only strategy, on the Book inventory.
		report("library.findItems(new SearchByAuthor(\"Bloch\"))",
				library.findItems(new SearchByAuthor("Bloch")));

		// A device-only strategy, on a device inventory.
		Inventory<ElectronicDevice> store = new Inventory<ElectronicDevice>();
		store.addItem(new ElectronicDevice("E-010", "Dell XPS 15", "Laptop"));
		store.addItem(new ElectronicDevice("E-011", "Galaxy S26", "Phone"));
		store.addItem(new ElectronicDevice("E-012", "ThinkPad X1", "Laptop"));

		report("store.findItems(new SearchByCategory(\"Laptop\"))",
				store.findItems(new SearchByCategory("Laptop")));

		// SearchStrategy is a functional interface, so a lambda is a strategy.
		report("lambda: items whose name contains \"Pro\"",
				catalogue.findItems(item -> item.getName().contains("Pro")));

		// A strategy held in a variable, then reused - still just a lambda.
		SearchStrategy<Item> idStartsWithB = item -> item.getId().startsWith("B-");
		report("lambda in a variable: ids starting with \"B-\"",
				catalogue.findItems(idStartsWithB));

		System.out.println();
	}

	private static void report(String what, List<? extends Item> results) {
		System.out.println("\n" + what);
		if (results.isEmpty()) {
			System.out.println("   (no matches)");
			return;
		}
		for (Item item : results) {
			System.out.println("   " + item);
		}
	}

	// ==================================================================
	// Part 3 - display any inventory at all
	// ==================================================================

	/**
	 * Prints every item of any inventory.
	 *
	 * {@code Inventory<?>} is an unbounded wildcard: "an inventory of some type
	 * I do not care about". That is enough to read the elements, because every
	 * object has {@code toString()}. The price is that the compiler only knows
	 * them as {@code Object} - so if this method needed {@code getId()} it
	 * would have to say {@code Inventory<? extends Item>} instead.
	 */
	public static void displayInventory(Inventory<?> inventory) {
		System.out.println("Inventory contents (" + inventory.size() + " items):");
		for (Object item : inventory.getAllItems()) {
			System.out.println("   " + item);
		}
	}

	private static void partThree(Inventory<Item> catalogue) {
		System.out.println("=== Part 3: displayInventory(Inventory<?>) ===");

		displayInventory(catalogue);

		// The same method, an inventory of a completely different type.
		Inventory<String> spareParts = new Inventory<String>();
		spareParts.addItem("USB-C cable");
		spareParts.addItem("Charger 65W");

		System.out.println();
		displayInventory(spareParts);
	}
}
