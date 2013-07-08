/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Strategy that sorts the bricks according to an externally provided .
 *
 * @author Marc Streit
 *
 */
public class ExternallyProvidedSortingStrategy
	implements IBrickSortingStrategy {

	/**
	 * The order of the bricks in the set determines the output order of the
	 * segementBricks.
	 */
	private BrickColumn externalBrick;

	/**
	 * Hash between the converted record perspective to the original one from
	 * the dimension group on which the kaplan meier plot creation was
	 * triggered. This information is needed for being able to use the same
	 * sorting strategy.
	 */
	private HashMap<Perspective, Perspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective = new HashMap<Perspective, Perspective>();

	@Override
	public ArrayList<GLBrick> getSortedBricks(List<GLBrick> segmentBricks) {

		GLBrick[] sortedBricks = new GLBrick[segmentBricks.size()];
		for (GLBrick brick : segmentBricks) {

			Group group = brick.getTablePerspective().getRecordGroup();

			Perspective dimGroupRecordPerspective = brick.getBrickColumn()
					.getTablePerspective().getRecordPerspective();

			Perspective originalDimGroupRecordPerspective = hashConvertedRecordPerspectiveToOrginalRecordPerspective
					.get(dimGroupRecordPerspective);

			GroupList dimGroupGroupList = dimGroupRecordPerspective.getVirtualArray()
					.getGroupList();

			for (int groupIndex = 0; groupIndex < dimGroupGroupList.size(); groupIndex++) {
				if (group == dimGroupGroupList.get(groupIndex)) {

					Group originalGroup = originalDimGroupRecordPerspective.getVirtualArray()
							.getGroupList().get(groupIndex);

					int i = 0;
					for (GLBrick originalBrick : externalBrick.getSegmentBricks()) {
						if (originalBrick.getTablePerspective().getRecordGroup() == originalGroup)
						{
							sortedBricks[i] = brick;
							break;
						}
						i++;
					}

					break;
				}
			}

			continue;
		}

		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
		for (int brickIndex = sortedBricks.length - 1; brickIndex >= 0; brickIndex--) {
			bricks.add(sortedBricks[brickIndex]);
		}

		return bricks;
	}

	/**
	 * @param externalBrick
	 *            setter, see {@link externalBrick}
	 */
	public void setExternalBrick(BrickColumn externalBrick) {
		this.externalBrick = externalBrick;
	}

	/**
	 * @param hashConvertedRecordPerspectiveToOrginalRecordPerspective setter,
	 *            see
	 *            {@link #hashConvertedRecordPerspectiveToOrginalRecordPerspective}
	 */
	public void setHashConvertedRecordPerspectiveToOrginalRecordPerspective(
			HashMap<Perspective, Perspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective) {
		this.hashConvertedRecordPerspectiveToOrginalRecordPerspective = hashConvertedRecordPerspectiveToOrginalRecordPerspective;
	}
}
