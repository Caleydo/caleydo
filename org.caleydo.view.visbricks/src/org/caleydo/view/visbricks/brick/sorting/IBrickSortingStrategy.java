package org.caleydo.view.visbricks.brick.sorting;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Interface for all strategies that specify the order of bricks for a dimension
 * group.
 * 
 * @author Partl
 * 
 */
public interface IBrickSortingStrategy {

	/**
	 * Gets a sorted list of specified bricks. The order of the bricks depends
	 * on the concrete strategy being used.
	 * 
	 * @param segmentBricks
	 * @param summaryBrick
	 * @return Sorted list of the specified Segment bricks and the summary
	 *         brick.
	 */
	public ArrayList<GLBrick> getSortedBricks(Set<GLBrick> segmentBricks);

}
