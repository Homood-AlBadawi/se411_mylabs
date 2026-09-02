package edu.psu.se411.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 - a reusable pipeline of transformations.
 *
 * The pipeline starts at type T. Every call to add() returns a NEW pipeline
 * whose output type is that of the transformer just added, so the compiler
 * tracks the type as it changes along the chain:
 *
 * <pre>
 * PipeLine&lt;String, String&gt;  p0 = PipeLine.start();
 * PipeLine&lt;String, Integer&gt; p1 = p0.add(s -&gt; s.length());
 * PipeLine&lt;String, Boolean&gt; p2 = p1.add(n -&gt; n &gt; 3);
 * boolean result = p2.execute("hello");
 * </pre>
 *
 * The original pipeline is never modified, so a partially built pipeline can
 * safely be reused as the starting point for several different chains.
 *
 * @param <T> the type fed into the pipeline
 * @param <R> the type produced by the pipeline as it currently stands
 */
public class PipeLine<T, R> {

	private final List<Transformer<?, ?>> transformers;

	private PipeLine(List<Transformer<?, ?>> transformers) {
		this.transformers = transformers;
	}

	/**
	 * Creates an empty pipeline over type T. With no transformers added yet the
	 * output type is still T, so execute() returns its input unchanged.
	 */
	public static <T> PipeLine<T, T> start() {
		return new PipeLine<T, T>(new ArrayList<Transformer<?, ?>>());
	}

	/**
	 * Adds a transformer to the end of the chain.
	 *
	 * @param next a transformer that consumes the pipeline's current output
	 *             type R and produces a new type V
	 * @return a new pipeline whose output type is V; this pipeline is unchanged
	 */
	public <V> PipeLine<T, V> add(Transformer<? super R, ? extends V> next) {
		if (next == null) {
			throw new IllegalArgumentException("Transformer must not be null");
		}
		List<Transformer<?, ?>> extended = new ArrayList<Transformer<?, ?>>(transformers);
		extended.add(next);
		return new PipeLine<T, V>(extended);
	}

	/**
	 * Applies every transformer, in the order they were added.
	 *
	 * The cast is unchecked because the intermediate types are erased at run
	 * time, but add() guarantees that each transformer's input type matches the
	 * previous transformer's output type, so the chain is type-safe.
	 *
	 * @param input the value to feed into the pipeline
	 * @return the value after all transformations
	 */
	@SuppressWarnings("unchecked")
	public R execute(T input) {
		Object current = input;
		for (Transformer<?, ?> transformer : transformers) {
			current = ((Transformer<Object, Object>) transformer).transform(current);
		}
		return (R) current;
	}

	/**
	 * How many transformations this pipeline will apply.
	 */
	public int size() {
		return transformers.size();
	}
}
