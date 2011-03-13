package org.caleydo.view.visbricks.brick;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.view.visbricks.GLVisBricks;
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

	private ASetBasedDataDomain dataDomain;
	private RelationAnalyzer relationAnalyzer;
	Integer setID;
	int groupID;
	GLVisBricks visBricks;
	int neighborSetID = -1;
	List<Integer> neighborGroupOrder;
	boolean isLeft;
	float[] similarities;

	public RelationIndicatorRenderer(GLBrick brick, GLVisBricks visBricks, boolean isLeft) {
		this.dataDomain = brick.getDataDomain();
		this.relationAnalyzer = dataDomain.getContentRelationAnalyzer();
		setID = brick.getSet().getID();
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
		ArrayList<DimensionGroup> dimensionGroups = visBricks.getDimensionGroups();

		int currentID;
		int previousID = -1;

		int count = 0;
		for (DimensionGroup dimensionGroup : dimensionGroups) {
			currentID = dimensionGroup.getSetID();
			if (currentID == setID && isLeft) {
				neighborSetID = previousID;
				if (neighborSetID != -1)
					neighborGroupOrder = dimensionGroups.get(count - 1).getGroupOrder();
				break;
			}
			if (previousID == setID && !isLeft) {
				neighborSetID = currentID;
				neighborGroupOrder = dimensionGroup.getGroupOrder();
				break;
			}

			previousID = currentID;
			count++;
		}

		SimilarityMap map = relationAnalyzer.getSimilarityMap(setID);
		if (map == null)
			return;
		VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarity = map
				.getVASimilarity(neighborSetID);
		if (vaSimilarity == null)
			return;
		GroupSimilarity<ContentVirtualArray, ContentGroupList> groupSimilarity = vaSimilarity
				.getGroupSimilarity(setID, groupID);

		similarities = groupSimilarity.getSimilarities();

	}

	@Override
	public synchronized void render(GL2 gl) {
		if (neighborSetID == -1 || similarities == null)
			return;

		float yOffset = 0;
		for (float similarity : similarities) {
			float height = similarity * y;
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0, 0, 0);
			gl.glVertex3f(0, yOffset, 0);
			gl.glVertex3f(x, yOffset, 0);
			gl.glColor3f(1, 1, 1);
			// gl.glColor3f(0.8f, 0.8f, 0f);
			gl.glVertex3f(x, yOffset + height, 0);
			gl.glVertex3f(0, yOffset + height, 0);

			gl.glEnd();

			yOffset += height;
			if (yOffset > y + 0.001f)
				GLHelperFunctions.drawSmallPointAt(gl, x, yOffset, 0);

		}
	}
}
