package org.caleydo.view.visbricks.dimensiongroup;

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
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.view.visbricks.brick.GLBrick;

public class DimensionGroupSpacingRenderer extends LayoutRenderer {

	private boolean renderDragAndDropSpacer = false;

	private boolean isVertical = true;

	private float lineLength = 0;

	DimensionGroup leftDimGroup;
	DimensionGroup rightDimGroup;

	private RelationAnalyzer relationAnalyzer;

	/**
	 * Default constructur needed if spacer does not need to render connections
	 */
	public DimensionGroupSpacingRenderer() {
	}

	public DimensionGroupSpacingRenderer(RelationAnalyzer relationAnalyzer,
			DimensionGroup leftDimGroup, DimensionGroup rightDimGroup) {

		this.relationAnalyzer = relationAnalyzer;
		this.leftDimGroup = leftDimGroup;
		this.rightDimGroup = rightDimGroup;
	}

	@Override
	public void render(GL2 gl) {

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

		if (relationAnalyzer != null)
			renderDimensionGroupConnections(gl);
	}

	private void renderDimensionGroupConnections(GL2 gl) {

		gl.glColor4f(1, 1, 0, 1f);
		gl.glLineWidth(4);

		List<GLBrick> leftBricks = leftDimGroup.getBricks();
		List<GLBrick> rightBricks = rightDimGroup.getBricks();

		SimilarityMap similarityMap = relationAnalyzer.getSimilarityMap(leftDimGroup
				.getSetID());

		if (similarityMap == null)
			return;

		VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarityMap = similarityMap
				.getVASimilarity(rightDimGroup.getSetID());
		if (vaSimilarityMap == null)
			return;

		for (GLBrick leftBrick : leftBricks) {

			ElementLayout leftBrickElementLayout = leftBrick.getWrappingLayout();
//			GLHelperFunctions.drawPointAt(gl, 0, leftBrickElementLayout.getTranslateY(),
//					0);

			GroupSimilarity<ContentVirtualArray, ContentGroupList> leftGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(leftDimGroup.getSetID(), leftBrick.getGroupID());

			float[] leftSimilarities = leftGroupSimilarity.getSimilarities();

			float leftSimilarityOffsetY = 0;
			float rightSimilarityOffsetY = 0;

			for (GLBrick rightBrick : rightBricks) {
				ElementLayout rightBrickElementLayout = rightBrick.getWrappingLayout();
				

				float leftSimilarityRatioY = leftSimilarities[rightBrick.getGroupID()];
				leftSimilarityOffsetY += leftSimilarityRatioY;

				GroupSimilarity<ContentVirtualArray, ContentGroupList> rightGroupSimilarity = vaSimilarityMap
						.getGroupSimilarity(rightDimGroup.getSetID(),
								rightBrick.getGroupID());
				float[] rightSimilarities = rightGroupSimilarity.getSimilarities();
				float rightSimilarityRatioY = rightSimilarities[leftBrick.getGroupID()];
				rightSimilarityOffsetY += rightSimilarityRatioY;

				gl.glBegin(GL2.GL_LINES);
				gl.glVertex2f(0, leftBrickElementLayout.getTranslateY()
						+ leftBrickElementLayout.getSizeScaledY() * leftSimilarityOffsetY);


				gl.glVertex2f(x, rightBrickElementLayout.getTranslateY()
						+ rightBrickElementLayout.getSizeScaledY()
						* rightSimilarityOffsetY);
				gl.glEnd();
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
