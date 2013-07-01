/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.sorting;

import java.util.List;

import org.caleydo.view.stratomex.brick.GLBrick;

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
	public List<GLBrick> getSortedBricks(List<GLBrick> segmentBricks);

}
