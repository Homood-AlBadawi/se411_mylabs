package edu.psu.se411.generics;

/**
 * Exercise 3 - a transformation from one type to another.
 *
 * Generic over both the input and the output type, so a Transformer may keep
 * the type the same (Transformer&lt;String, String&gt;) or change it
 * (Transformer&lt;String, Integer&gt;).
 *
 * @param <T> the input type
 * @param <R> the output type
 */
public interface Transformer<T, R> {

	R transform(T input);
}
