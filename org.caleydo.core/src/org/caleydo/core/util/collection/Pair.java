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
public class Pair<T extends Comparable<T>, E extends Comparable<E>>
	implements Comparable<Pair<T, E>>{
	private T first;
	private E second;
	
	public Pair()
	{
		
	}

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

	public void set(T first, E second)
	{
		this.first = first;
		this.second = second;
	}
	
	public void setFirst(T first) {
		this.first = first;
	}

	public void setSecond(E second) {
		this.second = second;
	}

	@Override
	public int compareTo(Pair<T, E> checkedPair) {
		int compareResultFirst = first.compareTo(checkedPair.getFirst());
		int compareResultSecond = second.compareTo(checkedPair.getSecond());
		
		if(compareResultFirst > 0 && compareResultSecond > 0)
			return 1;
		if(compareResultFirst == 0 && compareResultSecond == 0)
			return 0;
		
		return -1;
	}
}
