/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.brick.sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.view.stratomex.brick.GLBrick;

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
	private List<GLBrick> externalSortedBricks;

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

					for (GLBrick originalBrick : externalSortedBricks) {

						if (originalBrick.getTablePerspective().getRecordGroup() == originalGroup)
						{
							sortedBricks[externalSortedBricks.indexOf(originalBrick)] = brick;
							continue;
						}
					}

					continue;
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
	 * @param externalSortedBricks setter, see {@link #externalSortedBricks}
	 */
	public void setExternalBricks(List<GLBrick> externalSortedBricks) {
		this.externalSortedBricks = externalSortedBricks;
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
