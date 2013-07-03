/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.sorting;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Strategy that sorts the bricks by the average value of the {@link ISegmentData}.
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
			TablePerspective brickData = bricks.get(count).getTablePerspective();
			if (brickData.getContainerStatistics().getAverageValue() < brickToInsertData.getContainerStatistics()
					.getAverageValue())
				break;
		}
		bricks.add(count, brickToInsert);
	}

}
