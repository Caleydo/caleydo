package org.caleydo.core.util.collection;

/**
 * A pair of values, inspired by STL
 * 
 * @author Alexander Lex
 * @param <T>
 *            first type
 * @param <E>
 *            second type
 */
public class Pair<T, E> {
	T first;
	E second;

	/**
	 * Constructor
	 * 
	 * @param first
	 *            the first value
	 * @param second
	 *            the second value
	 */
	public Pair(T first, E second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public E getSecond() {
		return second;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public void setSecond(E second) {
		this.second = second;
	}
}
