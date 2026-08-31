package edu.psu.se411.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Stack class.
 *
 * SE411 - Lab 03: JUnit Intro
 */
public class StackTest {

	private Stack<String> stringStack;

	@BeforeEach
	public void setUp() {
		stringStack = new Stack<String>();
	}

	// ------------------------------------------------------------------
	// Normal / expected behaviour
	// ------------------------------------------------------------------

	@Test
	@DisplayName("push -> push -> pop returns the most recently pushed element (LIFO)")
	public void testPushAndPop() {
		stringStack.push("Z");
		stringStack.push("A");

		assertEquals("A", stringStack.pop());
	}

	@Test
	@DisplayName("push -> pop on a single element returns that element")
	public void push_then_pop_single_element() {
		stringStack.push("only");

		assertEquals("only", stringStack.pop());
	}

	@Test
	@DisplayName("Elements are popped in reverse order of insertion")
	public void elements_are_popped_in_reverse_order() {
		stringStack.push("first");
		stringStack.push("second");
		stringStack.push("third");

		assertEquals("third", stringStack.pop());
		assertEquals("second", stringStack.pop());
		assertEquals("first", stringStack.pop());
	}

	// ------------------------------------------------------------------
	// Error conditions
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Popping an empty stack throws NoSuchElementException")
	public void pop_empty_stack() {
		NoSuchElementException thrown = assertThrows(
				NoSuchElementException.class,
				() -> stringStack.pop(),
				"Expected pop from empty Stack to throw, but it didn't");

		assertTrue(thrown.getMessage().equals("Stack is empty, cannot pop"));
	}

	@Test
	@DisplayName("A stack emptied by pops behaves like a new empty stack")
	public void pop_after_stack_is_emptied_throws_again() {
		stringStack.push("one");
		stringStack.pop();

		assertThrows(NoSuchElementException.class, () -> stringStack.pop());
	}

	// ------------------------------------------------------------------
	// State across multiple calls
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Interleaved pushes and pops keep the correct top of stack")
	public void interleaved_push_and_pop_keeps_correct_order() {
		stringStack.push("A");
		stringStack.push("B");

		assertEquals("B", stringStack.pop());

		stringStack.push("C");

		assertEquals("C", stringStack.pop());
		assertEquals("A", stringStack.pop());
	}

	@Test
	@DisplayName("Duplicate values are stored and popped independently")
	public void duplicate_values_are_popped_one_by_one() {
		stringStack.push("dup");
		stringStack.push("dup");

		assertEquals("dup", stringStack.pop());
		assertEquals("dup", stringStack.pop());
		assertThrows(NoSuchElementException.class, () -> stringStack.pop());
	}

	// ------------------------------------------------------------------
	// Boundary / edge cases
	// ------------------------------------------------------------------

	@Test
	@DisplayName("A null element can be pushed and is popped back as null")
	public void push_null_element_is_allowed() {
		stringStack.push(null);

		assertNull(stringStack.pop());
	}

	@Test
	@DisplayName("An empty string is a valid element, distinct from an empty stack")
	public void push_empty_string_is_a_real_element() {
		stringStack.push("");

		assertEquals("", stringStack.pop());
	}

	@Test
	@DisplayName("Capacity is only an initial hint: the stack grows past it")
	public void stack_grows_beyond_its_initial_capacity() {
		Stack<Integer> smallStack = new Stack<Integer>(2);

		smallStack.push(1);
		smallStack.push(2);
		smallStack.push(3);

		assertEquals(3, smallStack.pop());
		assertEquals(2, smallStack.pop());
		assertEquals(1, smallStack.pop());
	}

	@Test
	@DisplayName("Zero and negative capacities fall back to the default and still work")
	public void non_positive_capacity_falls_back_to_default() {
		Stack<String> zeroCapacity = new Stack<String>(0);
		Stack<String> negativeCapacity = new Stack<String>(-5);

		assertDoesNotThrow(() -> zeroCapacity.push("ok"));
		assertDoesNotThrow(() -> negativeCapacity.push("ok"));

		assertEquals("ok", zeroCapacity.pop());
		assertEquals("ok", negativeCapacity.pop());
	}

	@Test
	@DisplayName("The stack is generic and works with a non-String type")
	public void stack_works_with_another_type_parameter() {
		Stack<Integer> intStack = new Stack<Integer>();

		intStack.push(Integer.MIN_VALUE);
		intStack.push(0);
		intStack.push(Integer.MAX_VALUE);

		assertEquals(Integer.MAX_VALUE, intStack.pop());
		assertEquals(0, intStack.pop());
		assertEquals(Integer.MIN_VALUE, intStack.pop());
	}

	@Test
	@DisplayName("Two stacks are independent of each other")
	public void separate_stack_instances_do_not_share_state() {
		Stack<String> other = new Stack<String>();

		stringStack.push("mine");

		assertThrows(NoSuchElementException.class, () -> other.pop());
		assertEquals("mine", stringStack.pop());
	}
}
