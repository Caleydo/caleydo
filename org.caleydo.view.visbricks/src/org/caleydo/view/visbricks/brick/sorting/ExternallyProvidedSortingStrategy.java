package org.caleydo.view.visbricks.brick.sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.view.visbricks.brick.GLBrick;

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
	private HashMap<RecordPerspective, RecordPerspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective = new HashMap<RecordPerspective, RecordPerspective>();

	@Override
	public ArrayList<GLBrick> getSortedBricks(List<GLBrick> segmentBricks) {

		GLBrick[] sortedBricks = new GLBrick[segmentBricks.size()];
		for (GLBrick brick : segmentBricks) {

			Group group = brick.getDataContainer().getRecordGroup();

			RecordPerspective dimGroupRecordPerspective = brick.getDimensionGroup()
					.getDataContainer().getRecordPerspective();

			RecordPerspective originalDimGroupRecordPerspective = hashConvertedRecordPerspectiveToOrginalRecordPerspective
					.get(dimGroupRecordPerspective);

			RecordGroupList dimGroupGroupList = dimGroupRecordPerspective.getVirtualArray()
					.getGroupList();

			for (int groupIndex = 0; groupIndex < dimGroupGroupList.size(); groupIndex++) {
				if (group == dimGroupGroupList.get(groupIndex)) {

					Group originalGroup = originalDimGroupRecordPerspective.getVirtualArray()
							.getGroupList().get(groupIndex);

					for (GLBrick originalBrick : externalSortedBricks) {

						if (originalBrick.getDataContainer().getRecordGroup() == originalGroup)
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
			HashMap<RecordPerspective, RecordPerspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective) {
		this.hashConvertedRecordPerspectiveToOrginalRecordPerspective = hashConvertedRecordPerspectiveToOrginalRecordPerspective;
	}
}
