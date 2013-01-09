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

import java.util.Comparator;
import java.util.Objects;

import com.google.common.base.Function;


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
public class Pair<T, E> {

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

	@Override
	public String toString() {
		return "<" + first + ", " + second + ">";
	}

	/**
	 * factory method for a pair
	 *
	 * @param first
	 * @param second
	 * @return
	 */
	public static <T, E> Pair<T, E> make(T first, E second) {
		return new Pair<T, E>(first, second);
	}

	/**
	 * factory method for a pair
	 *
	 * @param first
	 * @param second
	 * @return
	 */
	public static <T extends Comparable<T>, E> ComparablePair<T, E> make(T first, E second) {
		return new ComparablePair<T, E>(first, second);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
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
		return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
	}

	/**
	 * returns a {@link Function}, which maps the pair to the first element
	 *
	 * @return
	 */
	public static final <T1, T2> Function<Pair<T1, T2>, T1> mapFirst() {
		return new Function<Pair<T1,T2>,T1>() {
			@Override
			public T1 apply(Pair<T1, T2> arg0) {
				return arg0 == null ? null : arg0.getFirst();
			}
		};
	}

	/**
	 * returns a {@link Function}, which maps the pair to the second element
	 *
	 * @return
	 */
	public static final <T1, T2> Function<Pair<T1, T2>, T2> mapSecond() {
		return new Function<Pair<T1, T2>, T2>() {
			@Override
			public T2 apply(Pair<T1, T2> arg0) {
				return arg0 == null ? null : arg0.getSecond();
			}
		};
	}

	/**
	 * returns a comparator, which compares the first element
	 *
	 * @return
	 */
	public static <T extends Comparable<T>> Comparator<Pair<T, ?>> compareFirst() {
		return new Comparator<Pair<T, ?>>() {
			@Override
			public int compare(Pair<T, ?> o1, Pair<T, ?> o2) {
				return o1.first.compareTo(o2.first);
			}
		};
	}

	/**
	 * returns a comparator, which compares the second element
	 *
	 * @return
	 */
	public static <T extends Comparable<T>> Comparator<Pair<?, T>> compareSecond() {
		return new Comparator<Pair<?, T>>() {
			@Override
			public int compare(Pair<?, T> o1, Pair<?, T> o2) {
				return o1.second.compareTo(o2.second);
			}
		};
	}

	public static class ComparablePair<T extends Comparable<T>, E> extends Pair<T, E> implements
			Comparable<ComparablePair<T, E>> {
		public ComparablePair(T first, E second) {
			super(first, second);
		}

		@Override
		public int compareTo(ComparablePair<T, E> o) {
			return this.getFirst().compareTo(o.getFirst());
		}

	}

}
