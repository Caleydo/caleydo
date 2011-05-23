package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.view.visbricks.brick.GLBrick;

public interface IBrickSortingStrategy {
	
	public ArrayList<GLBrick> getSortedBricks(Set<GLBrick> segmentBricks, GLBrick summaryBrick);

}
