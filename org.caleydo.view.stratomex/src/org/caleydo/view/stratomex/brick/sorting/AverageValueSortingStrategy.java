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
import java.util.List;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Strategy that sorts the bricks by the average value of the
 * {@link ISegmentData}.
 * 
 * @author Partl
 * 
 */
public class AverageValueSortingStrategy implements IBrickSortingStrategy {

	@Override
	public ArrayList<GLBrick> getSortedBricks(List<GLBrick> segmentBricks) {
		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
		for (GLBrick brick : segmentBricks) {
			insertBrick(brick, bricks);
		}
		return bricks;
	}

	private void insertBrick(GLBrick brickToInsert, ArrayList<GLBrick> bricks) {

		int count;
		TablePerspective brickToInsertData = brickToInsert.getTablePerspective();
		for (count = 0; count < bricks.size(); count++) {
			TablePerspective brickData = (TablePerspective) bricks.get(count)
					.getTablePerspective();
			if (brickData.getContainerStatistics().getAverageValue() < brickToInsertData
					.getContainerStatistics().getAverageValue())
				break;
		}
		bricks.add(count, brickToInsert);
	}

}
