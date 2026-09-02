package edu.psu.se411.generics;

import java.util.List;

/**
 * Exercise 2 - a wrapper whose type parameter is bounded to Number.
 *
 * "T extends Number" is the bounded type parameter: the compiler will reject
 * NumberBox&lt;String&gt;, and inside the class we may call any Number method
 * (such as doubleValue()) on the stored item.
 *
 * @param <T> any subclass of Number (Integer, Double, Long, ...)
 */
public class NumberBox<T extends Number> {

	private T item;

	public NumberBox() {
	}

	public NumberBox(T item) {
		this.item = item;
	}

	/**
	 * Stores an item of type T in the wrapper.
	 */
	public void setItem(T item) {
		this.item = item;
	}

	/**
	 * Retrieves the stored item.
	 */
	public T getItem() {
		return item;
	}

	/**
	 * Adds another number to the one held in this box.
	 *
	 * The result is a double because the two operands may be of different
	 * numeric types (for example an Integer box plus a Double).
	 *
	 * @param other the number to add
	 * @return the sum as a double
	 */
	public double add(Number other) {
		if (item == null) {
			throw new IllegalStateException("NumberBox is empty, nothing to add to");
		}
		return item.doubleValue() + other.doubleValue();
	}

	/**
	 * Calculates the sum of a list of numbers.
	 *
	 * "? extends Number" is an upper-bounded wildcard: it accepts
	 * List&lt;Integer&gt;, List&lt;Double&gt;, List&lt;Number&gt; and so on,
	 * which a plain List&lt;Number&gt; parameter would not.
	 *
	 * @param numbers the numbers to add up
	 * @return the total as a double
	 */
	public static double sum(List<? extends Number> numbers) {
		double total = 0.0;
		for (Number n : numbers) {
			total += n.doubleValue();
		}
		return total;
	}

	@Override
	public String toString() {
		return "NumberBox(" + item + ")";
	}
}
