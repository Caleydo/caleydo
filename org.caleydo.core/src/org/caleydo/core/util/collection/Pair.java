/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.collection;


/**
 * A pair of values, inspired by STL Caution: when using the compare function
 * only the first element of the pair is used
 *
 * @author Alexander Lex
 * @param <T>
 *            first type
 * @param <E>
 *            second type
 */
public class Pair<T, E> implements Comparable<Pair<T, E>> {

	/** The first element of the pair */
	private T first;
	/** The second element of the pair */
	private E second;

	public Pair() {

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

	/**
	 * @return the first, see {@link #first}
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @return the second, see {@link #second}
	 */
	public E getSecond() {
		return second;
	}

	public void set(T first, E second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @param first
	 *            setter, see {@link #first}
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * @param second
	 *            setter, see {@link #second}
	 */
	public void setSecond(E second) {
		this.second = second;
	}

	// @Override
	// public int compareTo(Pair<T, E> checkedPair) {
	// int compareResultFirst = first.compareTo(checkedPair.getFirst());
	// int compareResultSecond = second.compareTo(checkedPair.getSecond());
	//
	// if(compareResultFirst > 0 && compareResultSecond > 0)
	// return 1;
	// if(compareResultFirst == 0 && compareResultSecond == 0)
	// return 0;
	//
	// return -1;
	// }

	@Override
	public String toString() {
		return "<" + first + ", " + second + ">";
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Pair<T, E> o) {
		if (o.getFirst() instanceof Comparable<?>) {
			return ((Comparable<T>) first).compareTo(o.getFirst());
		} else {
			throw new IllegalStateException("Tried to compare non-comparable values");
		}
	}

	public static <T, E> Pair<T, E> make(T first, E second) {
		return new Pair<T, E>(first, second);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

}
