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
package org.caleydo.view.tourguide.v3.model;

/**
 * @author Samuel Gratzl
 *
 */
public class ARow implements IRow {
	private int rank = -1;
	private int index = 0;

	/**
	 * @return the index, see {@link #index}
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the rank, see {@link #rank}
	 */
	@Override
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank
	 *            setter, see {@link rank}
	 */
	@Override
	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int compareTo(IRow o) {
		int rank2 = o.getRank();
		if (rank == rank2)
			return 0;
		if (rank < 0)
			return 1;
		if (rank2 < 0)
			return -1;
		return rank - rank2;
	}
}
