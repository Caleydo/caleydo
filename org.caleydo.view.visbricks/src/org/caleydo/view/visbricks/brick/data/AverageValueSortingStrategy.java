package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Strategy that sorts the bricks by the average value of the
 * {@link ISegmentData}.
 * 
 * @author Partl
 * 
 */
public class AverageValueSortingStrategy implements IBrickSortingStrategy {

	@Override
	public ArrayList<GLBrick> getSortedBricks(Set<GLBrick> segmentBricks,
			GLBrick summaryBrick) {
		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
		for (GLBrick brick : segmentBricks) {
			insertBrick(brick, bricks);
		}
		insertBrick(summaryBrick, bricks);

		return bricks;
	}

	private void insertBrick(GLBrick brickToInsert, ArrayList<GLBrick> bricks) {

		int count;
		DataContainer brickToInsertData = brickToInsert.getDataContainer();
		for (count = 0; count < bricks.size(); count++) {
			DataContainer brickData = (DataContainer) bricks.get(count)
					.getDataContainer();
			if (brickData.getContainerStatistics().getAverageValue() < brickToInsertData
					.getContainerStatistics().getAverageValue())
				break;
		}
		bricks.add(count, brickToInsert);
	}

}
