package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.view.visbricks.brick.GLBrick;

public class AverageValueSortingStrategy implements IBrickSortingStrategy {

	@Override
	public ArrayList<GLBrick> getSortedBricks(Set<GLBrick> segmentBricks,
			GLBrick summaryBrick) {
		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
		for(GLBrick brick : segmentBricks) {
			insertBrick(brick, bricks);
		}
		insertBrick(summaryBrick, bricks);

		return bricks;
	}
	
	private void insertBrick(GLBrick brickToInsert, ArrayList<GLBrick> bricks) {

		int count;
		TableBasedBrickData brickToInsertData = (TableBasedBrickData) brickToInsert.getBrickData();
		for (count = 0; count < bricks.size(); count++) {
			TableBasedBrickData brickData = (TableBasedBrickData) bricks.get(count).getBrickData();
			if (brickData.getAverageValue() < brickToInsertData
					.getAverageValue())
				break;
		}
		bricks.add(count, brickToInsert);
	}

}
