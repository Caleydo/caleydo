package org.caleydo.view.visbricks.dimensiongroup;

import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

public class DimensionGroupSpacingRenderer extends LayoutRenderer {

	private boolean renderDragAndDropSpacer = false;

	private boolean isVertical = true;

	private float lineLength = 0;

	private DimensionGroup leftDimGroup;
	private DimensionGroup rightDimGroup;

	private RelationAnalyzer relationAnalyzer;

	private HashMap<Integer, GroupMatch> hashGroupID2GroupMatches = new HashMap<Integer, GroupMatch>();

	private ConnectionBandRenderer connectionRenderer = new ConnectionBandRenderer();;

	/**
	 * Default constructur needed if spacer does not need to render connections
	 */
	public DimensionGroupSpacingRenderer() {

	}

	public DimensionGroupSpacingRenderer(RelationAnalyzer relationAnalyzer, ConnectionBandRenderer connectionRenderer,
			DimensionGroup leftDimGroup, DimensionGroup rightDimGroup) {

		this.relationAnalyzer = relationAnalyzer;
		this.leftDimGroup = leftDimGroup;
		this.rightDimGroup = rightDimGroup;
		this.connectionRenderer = connectionRenderer;
	}

	public void init() {

		if (relationAnalyzer == null)
			return;

		hashGroupID2GroupMatches.clear();

		List<GLBrick> leftBricks = leftDimGroup.getBricks();
		List<GLBrick> rightBricks = rightDimGroup.getBricks();

		if (leftBricks.size() == 0 || rightBricks.size() == 0)
			return;

		SimilarityMap similarityMap = relationAnalyzer.getSimilarityMap(leftDimGroup
				.getSetID());

		if (similarityMap == null)
			return;

		VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarityMap = similarityMap
				.getVASimilarity(rightDimGroup.getSetID());
		if (vaSimilarityMap == null)
			return;

		for (GLBrick leftBrick : leftBricks) {

			GroupMatch groupMatch = new GroupMatch(leftBrick.getGroupID());
			hashGroupID2GroupMatches.put(leftBrick.getGroupID(), groupMatch);

			ElementLayout leftBrickElementLayout = leftBrick.getWrappingLayout();

			GroupSimilarity<ContentVirtualArray, ContentGroupList> leftGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(leftDimGroup.getSetID(), leftBrick.getGroupID());

			float[] leftSimilarities = leftGroupSimilarity.getSimilarities();
			float leftSimilarityOffsetY = 0;

			for (GLBrick rightBrick : rightBricks) {

				SubGroupMatch subGroupMatch = new SubGroupMatch(rightBrick.getGroupID());
				groupMatch.addSubGroupMatch(rightBrick.getGroupID(), subGroupMatch);

				float leftSimilarityRatioY = leftSimilarities[rightBrick.getGroupID()];
				leftSimilarityOffsetY += leftSimilarityRatioY;

				subGroupMatch.setLeftAnchorYStart(leftBrickElementLayout.getTranslateY()
						+ leftBrickElementLayout.getSizeScaledY()
						* (leftSimilarityOffsetY));

				subGroupMatch.setLeftAnchorYEnd(leftBrickElementLayout.getTranslateY()
						+ leftBrickElementLayout.getSizeScaledY()
						* (leftSimilarityOffsetY - leftSimilarityRatioY));
			}
		}

		for (GLBrick rightBrick : rightBricks) {

			ElementLayout rightBrickElementLayout = rightBrick.getWrappingLayout();

			GroupSimilarity<ContentVirtualArray, ContentGroupList> rightGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(rightDimGroup.getSetID(), rightBrick.getGroupID());

			float[] rightSimilarities = rightGroupSimilarity.getSimilarities();
			float rightSimilarityOffsetY = 0;

			for (GLBrick leftBrick : leftBricks) {

				GroupMatch groupMatch = hashGroupID2GroupMatches.get(leftBrick
						.getGroupID());
				SubGroupMatch subGroupMatch = groupMatch.getSubGroupMatch(rightBrick
						.getGroupID());

				float rightSimilarityRatioY = rightSimilarities[leftBrick.getGroupID()];
				rightSimilarityOffsetY += rightSimilarityRatioY;

				subGroupMatch.setRightAnchorYStart(rightBrickElementLayout
						.getTranslateY()
						+ rightBrickElementLayout.getSizeScaledY()
						* (rightSimilarityOffsetY));

				subGroupMatch.setRightAnchorYEnd(rightBrickElementLayout.getTranslateY()
						+ rightBrickElementLayout.getSizeScaledY()
						* (rightSimilarityOffsetY - rightSimilarityRatioY));
			}
		}
	}

	@Override
	public void render(GL2 gl) {

		// FIXME: just for testing. this should be done only once!


		// Render drag and drop marker
		if (renderDragAndDropSpacer) {
			gl.glColor3f(0, 0, 0);
			gl.glLineWidth(3);

			gl.glBegin(GL2.GL_LINES);
			if (isVertical) {
				gl.glVertex2f(x / 2f, 0);
				gl.glVertex2f(x / 2f, y);
			} else {
				gl.glVertex2f(0, y / 2f);
				gl.glVertex2f(x, y / 2f);
			}
			gl.glEnd();
		}

		// Render center brick band connection
		float leftCenterBrickTop = 0;
		float leftCenterBrickBottom = 0;
		float rightCenterBrickTop = 0;
		float rightCenterBrickBottom = 0;

		float curveOffset = 0.4f;

		if (leftDimGroup != null) {
			GLBrick leftCenterBrick = leftDimGroup.getCenterBrick();

			ElementLayout layout = leftCenterBrick.getWrappingLayout();
			leftCenterBrickBottom = layout.getTranslateY();
			leftCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledX();
		} else {
			if (rightDimGroup != null) {
				leftCenterBrickBottom = rightDimGroup.getVisBricksView().getArchBottomY();
				leftCenterBrickTop = rightDimGroup.getVisBricksView().getArchTopY();
				curveOffset = 0.1f;
			}
		}

		if (rightDimGroup != null) {
			GLBrick rightCenterBrick = rightDimGroup.getCenterBrick();

			ElementLayout layout = rightCenterBrick.getWrappingLayout();
			rightCenterBrickBottom = layout.getTranslateY();
			rightCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledX();
		} else {
			if (leftDimGroup != null) {
				rightCenterBrickBottom = leftDimGroup.getVisBricksView().getArchBottomY();
				rightCenterBrickTop = leftDimGroup.getVisBricksView().getArchTopY();
				curveOffset = 0.1f;
			}
		}

		if (leftCenterBrickBottom == 0 && rightCenterBrickBottom == 0)
			return;

		// gl.glColor4f(0, 0, 0, 0.5f);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(0, leftCenterBrickTop, -10);
		// gl.glVertex3f(x, rightCenterBrickTop, -10);
		// gl.glVertex3f(x, rightCenterBrickBottom, -10);
		// gl.glVertex3f(0, leftCenterBrickBottom, -10);
		// gl.glEnd();

		connectionRenderer.renderSingleBand(gl, new float[] { 0, leftCenterBrickTop, 0 }, new float[] { 0,
				leftCenterBrickBottom, 0 }, new float[] { x, rightCenterBrickTop, 0 },
				new float[] { x, rightCenterBrickBottom, 0 }, true, curveOffset, 0,
				false, new float[] { 0, 0, 0 }, 0.5f);

		if (relationAnalyzer != null)
			renderDimensionGroupConnections(gl);
	}

	private void renderDimensionGroupConnections(GL2 gl) {

		gl.glLineWidth(1);
		for (GroupMatch groupMatch : hashGroupID2GroupMatches.values()) {

			for (SubGroupMatch subGroupMatch : groupMatch.getSubGroupMatches()) {
				// gl.glColor4f(0.3f, 0.3f, 1, 0.2f);
				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex2f(0, subGroupMatch.getLeftAnchorYTop());
				// gl.glVertex2f(x, subGroupMatch.getRightAnchorYTop());
				// gl.glEnd();
				//
				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex2f(0, subGroupMatch.getLeftAnchorYBottom());
				// gl.glVertex2f(x, subGroupMatch.getRightAnchorYBottom());
				// gl.glEnd();
				//
				// gl.glColor4f(0.3f, 0.3f, 1, 0.1f);
				// gl.glBegin(GL2.GL_POLYGON);
				// gl.glVertex2f(0, subGroupMatch.getLeftAnchorYTop());
				// gl.glVertex2f(0, subGroupMatch.getLeftAnchorYBottom());
				// gl.glVertex2f(x, subGroupMatch.getRightAnchorYBottom());
				// gl.glVertex2f(x, subGroupMatch.getRightAnchorYTop());
				//
				// gl.glEnd();

				connectionRenderer.renderSingleBand(gl, new float[] { 0, subGroupMatch.getLeftAnchorYTop(),
						0 }, new float[] { 0, subGroupMatch.getLeftAnchorYBottom(), 0 },
						new float[] { x, subGroupMatch.getRightAnchorYTop(), 0 },
						new float[] { x, subGroupMatch.getRightAnchorYBottom(), 0 },
						true, 0.3f, 0, false, new float[] { 0.0f, 0.0f, 1 }, 0.15f);
			}
		}
	}

	public void setRenderSpacer(boolean renderSpacer) {
		this.renderDragAndDropSpacer = renderSpacer;
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public void setLineLength(float lineLength) {
		this.lineLength = lineLength;
	}
}
