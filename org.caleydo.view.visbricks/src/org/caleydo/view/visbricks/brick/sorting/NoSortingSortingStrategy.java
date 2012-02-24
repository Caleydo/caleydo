package org.caleydo.view.visbricks.brick.sorting;

import java.util.List;

import org.caleydo.view.visbricks.brick.GLBrick;

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
