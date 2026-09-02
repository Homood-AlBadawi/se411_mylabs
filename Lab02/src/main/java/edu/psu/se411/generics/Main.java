package edu.psu.se411.generics;

import java.util.Arrays;
import java.util.List;

/**
 * SE411 - Lab 02: Introduction to Java Generics.
 *
 * Runs a small demonstration of each exercise.
 */
public class Main {

	public static void main(String[] args) {
		exercise1();
		exercise2();
		exercise3();
		exercise4();
	}

	// ==================================================================
	// Exercise 1 - generic PrintableList
	// ==================================================================

	private static void exercise1() {
		System.out.println("=== Exercise 1: PrintableList ===");

		String[] names = { "Aziz", "Homood", "Sara", "Khalid" };
		PrintableList<String> nameList = new PrintableList<String>(names);
		nameList.printAll();

		// The same class works for any type, which is the point of generics.
		Integer[] scoreArray = { 90, 75, 100 };
		PrintableList<Integer> scores = new PrintableList<Integer>(scoreArray);
		scores.printAll();

		System.out.println();
	}

	// ==================================================================
	// Exercise 2 - bounded type parameter
	// ==================================================================

	private static void exercise2() {
		System.out.println("=== Exercise 2: NumberBox ===");

		NumberBox<Integer> intBox = new NumberBox<Integer>();
		intBox.setItem(10);
		System.out.println("intBox item      : " + intBox.getItem());
		System.out.println("intBox + 5       : " + intBox.add(5));

		NumberBox<Double> doubleBox = new NumberBox<Double>(2.5);
		System.out.println("doubleBox item   : " + doubleBox.getItem());
		System.out.println("doubleBox + 0.75 : " + doubleBox.add(0.75));

		// Mixing types is fine because add() accepts any Number.
		System.out.println("intBox + 2.5     : " + intBox.add(2.5));

		List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
		List<Double> doubles = Arrays.asList(1.5, 2.5, 3.0);
		System.out.println("sum of ints      : " + NumberBox.sum(ints));
		System.out.println("sum of doubles   : " + NumberBox.sum(doubles));

		// NumberBox<String> would not compile: String is not a Number.
		// That is the bound "T extends Number" doing its job.

		System.out.println();
	}

	// ==================================================================
	// Exercise 3 - transformation pipeline
	// ==================================================================

	private static void exercise3() {
		System.out.println("=== Exercise 3: PipeLine ===");

		// A transformer written the long way, as an anonymous class.
		Transformer<String, String> trim = new Transformer<String, String>() {
			@Override
			public String transform(String input) {
				return input.trim();
			}
		};

		// String -> String -> Integer -> Boolean.
		// Note how the pipeline's second type parameter changes at each step.
		PipeLine<String, Boolean> pipeline = PipeLine.<String>start()
				.add(trim)
				.add(new Transformer<String, String>() {
					@Override
					public String transform(String input) {
						return input.toUpperCase();
					}
				})
				.add(new Transformer<String, Integer>() {
					@Override
					public Integer transform(String input) {
						return input.length();
					}
				})
				.add(new Transformer<Integer, Boolean>() {
					@Override
					public Boolean transform(Integer input) {
						return input > 3;
					}
				});

		System.out.println("steps in pipeline    : " + pipeline.size());
		System.out.println("\"  hello  \" -> " + pipeline.execute("  hello  "));
		System.out.println("\"  hi  \"    -> " + pipeline.execute("  hi  "));

		// A pipeline that keeps the same type from start to finish.
		PipeLine<Integer, Integer> doubler = PipeLine.<Integer>start()
				.add(new Transformer<Integer, Integer>() {
					@Override
					public Integer transform(Integer input) {
						return input * 2;
					}
				})
				.add(new Transformer<Integer, Integer>() {
					@Override
					public Integer transform(Integer input) {
						return input + 1;
					}
				});

		System.out.println("doubler.execute(5)   : " + doubler.execute(5));

		// An empty pipeline returns its input untouched.
		PipeLine<String, String> empty = PipeLine.start();
		System.out.println("empty.execute(\"abc\") : " + empty.execute("abc"));

		System.out.println();
	}

	// ==================================================================
	// Exercise 4 - wildcards
	// ==================================================================

	private static void exercise4() {
		System.out.println("=== Exercise 4: Wildcards ===");

		printList(Arrays.asList("red", "green", "blue"));
		printList(Arrays.asList(1, 2, 3));

		System.out.println("sumNumbers(ints)    : " + sumNumbers(Arrays.asList(1, 2, 3)));
		System.out.println("sumNumbers(doubles) : " + sumNumbers(Arrays.asList(1.5, 2.5)));
	}

	/**
	 * Prints any list, whatever its element type.
	 *
	 * The unbounded wildcard &lt;?&gt; means "a list of some unknown type". We
	 * can read from it as Object and print it, but we cannot add to it, because
	 * the compiler does not know what type it actually holds.
	 */
	public static void printList(List<?> list) {
		for (Object item : list) {
			System.out.println(item);
		}
	}

	/**
	 * Sums a list of numbers.
	 *
	 * The upper-bounded wildcard &lt;? extends Number&gt; accepts List&lt;Integer&gt;,
	 * List&lt;Double&gt; and so on. A plain List&lt;Number&gt; parameter would
	 * reject all of those, because List&lt;Integer&gt; is not a subtype of
	 * List&lt;Number&gt;.
	 */
	public static double sumNumbers(List<? extends Number> numbers) {
		double total = 0.0;
		for (Number n : numbers) {
			total += n.doubleValue();
		}
		return total;
	}
}
