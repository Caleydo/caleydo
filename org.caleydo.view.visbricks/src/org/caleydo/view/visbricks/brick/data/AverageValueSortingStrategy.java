package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.view.visbricks.brick.GLBrick;

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
		ISegmentData brickToInsertData = (ISegmentData) brickToInsert.getSegmentData();
		for (count = 0; count < bricks.size(); count++) {
			ISegmentData brickData = (ISegmentData) bricks.get(count).getSegmentData();
			if (brickData.getContainerStatistics().getAverageValue() < brickToInsertData
					.getContainerStatistics().getAverageValue())
				break;
		}
		bricks.add(count, brickToInsert);
	}

}
