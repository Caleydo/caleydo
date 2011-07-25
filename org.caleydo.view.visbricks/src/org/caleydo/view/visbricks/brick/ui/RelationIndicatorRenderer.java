package org.caleydo.view.visbricks.brick.ui;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

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
	Integer tableID;
	int groupID;
	GLVisBricks visBricks;
	int neighborSetID = -1;
	List<GLBrick> neighborBrickOrder;
	boolean isLeft;
	float[] similarities;
	GLBrick brick;

	int[] scores;

	public RelationIndicatorRenderer(GLBrick brick, GLVisBricks visBricks, boolean isLeft) {
		this.brick = brick;
		// this.dataDomain = brick.getDataDomain();
		this.relationAnalyzer = visBricks.getRelationAnalyzer();
		tableID = brick.getDimensionGroup().getTableID();
		groupID = brick.getGroupID();
		this.visBricks = visBricks;
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
		ArrayList<DimensionGroup> dimensionGroups = visBricks.getDimensionGroupManager()
				.getDimensionGroups();

		int currentID;
		int previousID = -1;

		int count = 0;
		for (DimensionGroup dimensionGroup : dimensionGroups) {
			currentID = dimensionGroup.getTableID();
			if (currentID == tableID && isLeft) {
				neighborSetID = previousID;
				if (neighborSetID != -1)
					neighborBrickOrder = dimensionGroups.get(count - 1)
							.getBricksForRelations();
				break;
			}
			if (previousID == tableID && !isLeft) {
				neighborSetID = currentID;
				neighborBrickOrder = dimensionGroup.getBricksForRelations();
				break;
			}

			previousID = currentID;
			count++;
		}

		SimilarityMap map = relationAnalyzer.getSimilarityMap(tableID);
		if (map == null)
			return;
		VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarity = map
				.getVASimilarity(neighborSetID);

		// SimilarityMap map = relationAnalyzer.getSimilarityMap(neighborSetID);
		// if (map == null)
		// return;
		// VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarity =
		// map
		// .getVASimilarity(tableID);

		if (vaSimilarity == null)
			return;
		GroupSimilarity<RecordVirtualArray, RecordGroupList> groupSimilarity = vaSimilarity
				.getGroupSimilarity(tableID, groupID);

		similarities = groupSimilarity.getSimilarities();
		scores = groupSimilarity.getScores();

	}

	@Override
	public synchronized void render(GL2 gl) {
		if (neighborSetID == -1 || similarities == null)
			return;

		// float xDebugOffset = -0.05f;
		float yOffset = 0;
		for (GLBrick brick : neighborBrickOrder) {

			int foreignGroupID = brick.getGroup().getGroupID();
			float similarity = similarities[foreignGroupID];
			float height = similarity * y;
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0, 0, 0);
			gl.glVertex3f(0, yOffset, 0);
			gl.glVertex3f(x, yOffset, 0);
			if (brick.getRecordGroupSelectionManager().checkStatus(
					SelectionType.SELECTION, brick.getGroup().getID()))
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
}
