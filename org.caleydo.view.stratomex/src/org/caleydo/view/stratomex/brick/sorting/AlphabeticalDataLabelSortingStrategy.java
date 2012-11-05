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
package org.caleydo.view.stratomex.brick.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Strategy that sorts the segment bricks by the labels of their
 * {@link ISegmentData}. The summary brick is added in the middle of the list.
 * 
 * @author Partl
 * 
 */
public class AlphabeticalDataLabelSortingStrategy implements IBrickSortingStrategy {

	private class DataLabelComparable implements Comparable<DataLabelComparable> {

		private String id;
		private GLBrick brick;

		public DataLabelComparable(String id, GLBrick brick) {
			this.id = id;
			this.brick = brick;
		}

		@Override
		public int compareTo(DataLabelComparable dataLabelComparable) {
			return id.compareTo(dataLabelComparable.id);
		}

	}

	@Override
	public ArrayList<GLBrick> getSortedBricks(List<GLBrick> segmentBricks) {

		ArrayList<DataLabelComparable> comparables = new ArrayList<DataLabelComparable>();

		for (GLBrick brick : segmentBricks) {
			comparables.add(new DataLabelComparable(brick.getTablePerspective().getLabel(),
					brick));
		}
		// comparables.add(new DataLabelComparable(summaryBrick.getBrickData()
		// .getLabel(), summaryBrick));

		Collections.sort(comparables);

		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();

		for (DataLabelComparable comparable : comparables) {
			bricks.add(comparable.brick);
		}

		return bricks;
	}

}
