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
package org.caleydo.view.stratomex.brick.ui;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * <p>
 * Renders a small sequence of bars indicating the relations to other groups in
 * neighboring DimensionGroups.
 * </p>
 * <p>
 * A RelationIndicatorRenderer can be either on the left side or on the right
 * side of a brick. Where it is has to be specified in the constructor. This
 * affects which dimensionGroup it is compared to.
 * 
 * @author Alexander Lex
 * 
 */
public class RelationIndicatorRenderer extends LayoutRenderer {

	// private ASetBasedDataDomain dataDomain;
	private RelationAnalyzer relationAnalyzer;
	String perspectiveID;
	int groupID;
	GLStratomex stratomex;
	String neighborPerspectiveID = null;
	List<GLBrick> neighborBrickOrder;
	boolean isLeft;
	float[] similarities;
	GLBrick brick;

	public RelationIndicatorRenderer(GLBrick brick, GLStratomex stratomex, boolean isLeft) {
		this.brick = brick;
		// this.dataDomain = brick.getDataDomain();
		this.relationAnalyzer = stratomex.getRelationAnalyzer();
		perspectiveID = brick.getBrickColumn().getTablePerspective()
				.getRecordPerspective().getPerspectiveID();
		groupID = brick.getTablePerspective().getRecordGroup().getGroupIndex();
		this.stratomex = stratomex;
		this.isLeft = isLeft;
	}

	/**
	 * To be called if the relations in the {@link RelationAnalyzer} have been
	 * updated. Should typically be triggered via a
	 * {@link RelationsUpdatedEvent} in the managing view.
	 * 
	 * TODO: add parameters to check whether an update is actually necessary.
	 */
	public synchronized void updateRelations() {
		neighborBrickOrder = null;
		neighborPerspectiveID = null;
		similarities = null;
		ArrayList<BrickColumn> dimensionGroups = stratomex.getBrickColumnManager()
				.getBrickColumns();

		String currentID;
		String previousID = null;

		int count = 0;
		for (BrickColumn dimensionGroup : dimensionGroups) {
			currentID = dimensionGroup.getTablePerspective().getRecordPerspective().getPerspectiveID();
			if (currentID.equals(perspectiveID) && isLeft) {
				neighborPerspectiveID = previousID;
				if (neighborPerspectiveID != null)
					neighborBrickOrder = dimensionGroups.get(count - 1)
							.getBricksForRelations();
				break;
			}
			if (previousID == perspectiveID && !isLeft) {
				neighborPerspectiveID = currentID;
				neighborBrickOrder = dimensionGroup.getBricksForRelations();
				break;
			}

			previousID = currentID;
			count++;
		}

		SimilarityMap map = relationAnalyzer.getSimilarityMap(perspectiveID);
		if (map == null)
			return;
		VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarity = map
				.getVASimilarity(neighborPerspectiveID);

		// SimilarityMap map = relationAnalyzer.getSimilarityMap(neighborSetID);
		// if (map == null)
		// return;
		// VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarity =
		// map
		// .getVASimilarity(tableID);

		if (vaSimilarity == null)
			return;
		GroupSimilarity<RecordVirtualArray, RecordGroupList> groupSimilarity = vaSimilarity
				.getGroupSimilarity(perspectiveID, groupID);

		similarities = groupSimilarity.getSimilarities();

	}

	@Override
	public synchronized void renderContent(GL2 gl) {
		if (neighborPerspectiveID == null || similarities == null)
			return;

		// float xDebugOffset = -0.05f;
		float yOffset = 0;
		for (GLBrick brick : neighborBrickOrder) {

			Group recordGroup = brick.getTablePerspective().getRecordGroup();
			if (recordGroup == null) {
				// this is true for header bricks
				continue;
				// TODO this could be improved if we define groups for header
				// bricks
			}
			int foreignGroupID = recordGroup.getGroupIndex();
			float similarity = similarities[foreignGroupID];
			float height = similarity * y;
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0, 0, 0);
			gl.glVertex3f(0, yOffset, 0);
			gl.glVertex3f(x, yOffset, 0);
			if (brick.getTablePerspectiveSelectionManager().checkStatus(
					SelectionType.SELECTION,
					brick.getTablePerspective().getRecordGroup().getID()))
				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
			else

				gl.glColor3f(1, 1, 1);
			// gl.glColor3f(0.8f, 0.8f, 0f);
			gl.glVertex3f(x, yOffset + height, 0);
			gl.glVertex3f(0, yOffset + height, 0);

			gl.glEnd();

			// if (isLeft) {
			// gl.glColor3f(0, 0, 0);
			// this.brick.getTextRenderer().setColor(1, 0, 0, 1);
			//
			// this.brick.getTextRenderer().renderText(gl,
			// "" + foreignGroupID + " - " + scores[foreignGroupID],
			// xDebugOffset, yOffset + height / 2, 1);
			// xDebugOffset -= 0.2f;
			//
			// }

			// GLHelperFunctions.drawPointAt(gl, -0.2f, yOffset + height, 0);
			yOffset += height;
			if (yOffset > y + 0.0001f) {
				GLHelperFunctions.drawSmallPointAt(gl, x, yOffset, 0);
			}
		}
	}
	
	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}
}
