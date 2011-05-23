package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.caleydo.view.visbricks.brick.GLBrick;

public class AlphabeticalDataLabelSortingStrategy implements
		IBrickSortingStrategy {

	private class DataLabelComparable implements
			Comparable<DataLabelComparable> {

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
	public ArrayList<GLBrick> getSortedBricks(Set<GLBrick> segmentBricks,
			GLBrick summaryBrick) {

		ArrayList<DataLabelComparable> comparables = new ArrayList<DataLabelComparable>();

		for (GLBrick brick : segmentBricks) {
			comparables.add(new DataLabelComparable(brick.getBrickData()
					.getLabel(), brick));
		}
//		comparables.add(new DataLabelComparable(summaryBrick.getBrickData()
//				.getLabel(), summaryBrick));
		
		Collections.sort(comparables);

		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
		
		for(DataLabelComparable comparable : comparables) {
			bricks.add(comparable.brick);
		}
		
		bricks.add((int)Math.floor(bricks.size() / 2), summaryBrick);

		return bricks;
	}

}
