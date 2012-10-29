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


public final class Triple<T1, T2, T3> {
	/** The first element of the pair */
	private final T1 first;
	/** The second element of the pair */
	private final T2 second;

	private final T3 third;

	private Triple(T1 first, T2 second, T3 third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	/**
	 * @return the first, see {@link #first}
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * @return the second, see {@link #second}
	 */
	public T2 getSecond() {
		return second;
	}

	public T3 getThird() {
		return third;
	}

	@Override
	public String toString() {
		return "<" + first + ", " + second + ", " + third + ">";
	}

	public static <T1, T2, T3> Triple<T1, T2, T3> make(T1 first, T2 second, T3 third) {
		return new Triple<T1, T2, T3>(first, second, third);
	}

}
