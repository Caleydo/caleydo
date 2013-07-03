/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.sorting;

import java.util.List;

import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Implementation of {@link IBrickSortingStrategy} which returns the sorting of the bricks as they were provided.
 * 
 * @author Alexander Lex
 *
 */
public class NoSortingSortingStrategy implements IBrickSortingStrategy {

	@Override
	public List<GLBrick> getSortedBricks(List<GLBrick> segmentBricks) {
		return segmentBricks;
	}

}
