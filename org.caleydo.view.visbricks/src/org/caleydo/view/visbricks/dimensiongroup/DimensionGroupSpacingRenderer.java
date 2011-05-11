package org.caleydo.view.visbricks.dimensiongroup;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;

public class DimensionGroupSpacingRenderer extends LayoutRenderer implements IDropArea {

	private int ID;

	private boolean renderDragAndDropSpacer = false;

	private boolean isVertical = true;

	private float lineLength = 0;

	private DimensionGroup leftDimGroup;
	private DimensionGroup rightDimGroup;

	private RelationAnalyzer relationAnalyzer;

	private HashMap<Integer, GroupMatch> hashGroupID2GroupMatches = new HashMap<Integer, GroupMatch>();

	private ConnectionBandRenderer connectionRenderer = new ConnectionBandRenderer();

	private GLVisBricks glVisBricks;

	public DimensionGroupSpacingRenderer(RelationAnalyzer relationAnalyzer,
			ConnectionBandRenderer connectionRenderer, DimensionGroup leftDimGroup,
			DimensionGroup rightDimGroup, GLVisBricks glVisBricksView) {

		this.relationAnalyzer = relationAnalyzer;
		this.leftDimGroup = leftDimGroup;
		this.rightDimGroup = rightDimGroup;
		this.connectionRenderer = connectionRenderer;
		this.glVisBricks = glVisBricksView;

		glVisBricks.getDimensionGroupManager().getDimensionGroupSpacers().put(ID, this);
	}

	{
		ID = GeneralManager.get().getIDCreator()
				.createID(EManagedObjectType.DIMENSION_GROUP_SPACER);
	}

	public void init() {

		if (relationAnalyzer == null || leftDimGroup == null || rightDimGroup == null)
			return;

		hashGroupID2GroupMatches.clear();

		List<GLBrick> leftBricks = leftDimGroup.getBricksForRelations();
		List<GLBrick> rightBricks = rightDimGroup.getBricksForRelations();	
		
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

			GroupMatch groupMatch = new GroupMatch(leftBrick);
			hashGroupID2GroupMatches.put(leftBrick.getGroupID(), groupMatch);

			ElementLayout leftBrickElementLayout = leftBrick.getLayout();

			GroupSimilarity<ContentVirtualArray, ContentGroupList> leftGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(leftDimGroup.getSetID(), leftBrick.getGroupID());

			float[] leftSimilarities = leftGroupSimilarity.getSimilarities();
			float leftSimilarityOffsetY = 0;

			for (GLBrick rightBrick : rightBricks) {

				SubGroupMatch subGroupMatch = new SubGroupMatch(
						glVisBricks.getNextConnectionBandID(), rightBrick);
				groupMatch.addSubGroupMatch(rightBrick.getGroupID(), subGroupMatch);

				calculateSubMatchSelections(subGroupMatch,
						leftGroupSimilarity.getSimilarityVAs(rightBrick.getGroupID()));

				float leftSimilarityRatioY = leftSimilarities[rightBrick.getGroupID()];
				leftSimilarityOffsetY += leftSimilarityRatioY;

				subGroupMatch.setSimilarityRatioLeft(leftSimilarityRatioY);

				subGroupMatch.setLeftAnchorYStart(leftBrickElementLayout.getTranslateY()
						+ leftBrickElementLayout.getSizeScaledY()
						* (leftSimilarityOffsetY));

				subGroupMatch.setLeftAnchorYEnd(leftBrickElementLayout.getTranslateY()
						+ leftBrickElementLayout.getSizeScaledY()
						* (leftSimilarityOffsetY - leftSimilarityRatioY));
			}
		}

		for (GLBrick rightBrick : rightBricks) {

			ElementLayout rightBrickElementLayout = rightBrick.getLayout();

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
				
				subGroupMatch.setSimilarityRatioRight(rightSimilarityRatioY);

				subGroupMatch.setRightAnchorYStart(rightBrickElementLayout
						.getTranslateY()
						+ rightBrickElementLayout.getSizeScaledY()
						* (rightSimilarityOffsetY));

				subGroupMatch.setRightAnchorYEnd(rightBrickElementLayout.getTranslateY()
						+ rightBrickElementLayout.getSizeScaledY()
						* (rightSimilarityOffsetY - rightSimilarityRatioY));

			}
			
//			break;
		}
	}

	private void calculateSubMatchSelections(SubGroupMatch subGroupMatch,
			ContentVirtualArray contentVA) {

		if (contentVA.size() == 0)
			return;

		ContentSelectionManager contentSelectionManager = glVisBricks
				.getContentSelectionManager();

		glVisBricks.getHashConnectionBandIDToContentVA().put(
				subGroupMatch.getConnectionBandID(), contentVA);

		float ratio = 0;

		// Iterate over all selection types
		for (SelectionType selectionType : contentSelectionManager.getSelectionTypes()) {

			if (selectionType == SelectionType.MOUSE_OVER
					|| selectionType == SelectionType.DESELECTED
					|| selectionType == SelectionType.LEVEL_HIGHLIGHTING)
				continue;

			Set<Integer> selectedByGroupSelections = contentSelectionManager
					.getElements(selectionType);

			if (selectedByGroupSelections == null
					|| selectedByGroupSelections.size() == 0) {

				ratio = 1;//(float) contentVA.size()
//				 / subGroupMatch.getBrick().getContentVA().size();

				subGroupMatch.addSelectionTypeRatio(ratio, SelectionType.NORMAL);
				continue;
			}

			int intersectionCount = 0;
			for (int contentID : contentVA) {
				if (selectedByGroupSelections.contains(contentID))
					intersectionCount++;
			}
			
			ratio = (float) intersectionCount / contentVA.size();
			
//			if (intersectionCount > 0) {
//			System.out.println("intersection: " +intersectionCount);
//			System.out.println("ratio:" +ratio);
//			}
			
			subGroupMatch.addSelectionTypeRatio(ratio, selectionType);
		}
	}

	@Override
	public void render(GL2 gl) {

		renderBackground(gl);
		renderDragAndDropMarker(gl);

		renderFlexibleArch(gl);
		renderDimensionGroupConnections(gl);
	}

	private void renderBackground(GL2 gl) {

		int pickingID = glVisBricks.getPickingManager().getPickingID(glVisBricks.getID(),
				EPickingType.DIMENSION_GROUP_SPACER, ID);

		gl.glPushName(pickingID);
		gl.glColor4f(1, 1, 0, 0f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(x, 0);
		gl.glVertex2f(x, y);
		gl.glVertex2f(0, y);
		gl.glEnd();
		gl.glPopName();
	}

	private void renderDragAndDropMarker(GL2 gl) {

		// Render drag and drop marker
		if (renderDragAndDropSpacer) {
			gl.glColor4f(1, 0, 0, 1);
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

			System.out.println("spacer line");

			renderDragAndDropSpacer = false;
		}
	}

	private void renderFlexibleArch(GL2 gl) {
		
		if (connectionRenderer == null)
			return;

		// Do not render the arch for group spacer in the arch sides
		if (!isVertical)
			return;

		float leftCenterBrickTop = 0;
		float leftCenterBrickBottom = 0;
		float rightCenterBrickTop = 0;
		float rightCenterBrickBottom = 0;

		float curveOffset = x * 0.2f;

		float xStart = 0;
		float xEnd;
		
	

		// handle situation in center arch where to group is contained
		if (leftDimGroup == null && rightDimGroup == null && glVisBricks != null) {

			leftCenterBrickBottom = glVisBricks.getArchBottomY();
			leftCenterBrickTop = glVisBricks.getArchTopY();

			rightCenterBrickBottom = glVisBricks.getArchBottomY();
			rightCenterBrickTop = glVisBricks.getArchTopY();
		}

		
		
		if (leftDimGroup != null) {
			if (leftDimGroup.isDetailBrickShown() && !leftDimGroup.isExpandLeft())
				return;
			
			GLBrick leftCenterBrick = leftDimGroup.getCenterBrick();

			ElementLayout layout = leftCenterBrick.getLayout();
			leftCenterBrickBottom = layout.getTranslateY();
			leftCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledY();

			if(!leftDimGroup.isDetailBrickShown())
			xStart = leftDimGroup.getLayout().getTranslateX()
					- leftCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from center brick to dimension
			// group on
			// the LEFT
			if (xStart != 0) {

				connectionRenderer.renderSingleBand(gl, new float[] { xStart,
						leftCenterBrickTop, 0 }, new float[] { xStart,
						leftCenterBrickBottom, 0 }, new float[] { 0, leftCenterBrickTop,
						0 }, new float[] { 0, leftCenterBrickBottom, 0 }, false,
						curveOffset, 0, false, new float[] { 0, 0, 0 }, 0.5f);
			}

		} else {
			if (rightDimGroup != null) {
				leftCenterBrickBottom = glVisBricks.getArchBottomY();
				leftCenterBrickTop = glVisBricks.getArchTopY();
				curveOffset = 0.1f;
			}
		}

		
		
		if (rightDimGroup != null) {
			if (rightDimGroup.isDetailBrickShown() && rightDimGroup.isExpandLeft())
				return;
			GLBrick rightCenterBrick = rightDimGroup.getCenterBrick();

			ElementLayout layout = rightCenterBrick.getLayout();
			rightCenterBrickBottom = layout.getTranslateY();
			rightCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledY();

			if(!rightDimGroup.isDetailBrickShown())
					
			xEnd = x + rightCenterBrick.getLayout().getTranslateX()
					- rightDimGroup.getLayout().getTranslateX();
			else
				xEnd = x + rightCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from center brick to dimension
			// group on
			// the RIGHT
			if (xEnd != 0) {

				connectionRenderer.renderStraightBand(gl, new float[] { x,
						rightCenterBrickTop, 0 }, new float[] { x,
						rightCenterBrickBottom, 0 }, new float[] { xEnd,
						rightCenterBrickTop, 0 }, new float[] { xEnd,
						rightCenterBrickBottom, 0 }, false, curveOffset, 0, false,
						new float[] { 0, 0, 0 }, 0.5f);
			}

		} else {
			if (leftDimGroup != null) {
				rightCenterBrickBottom = glVisBricks.getArchBottomY();
				rightCenterBrickTop = glVisBricks.getArchTopY();
				curveOffset = 0.1f;
			}
		}

		if (leftCenterBrickBottom == 0 && rightCenterBrickBottom == 0)
			return;

		connectionRenderer.renderSingleBand(gl, new float[] { 0, leftCenterBrickTop, 0 },
				new float[] { 0, leftCenterBrickBottom, 0 }, new float[] { x,
						rightCenterBrickTop, 0 }, new float[] { x,
						rightCenterBrickBottom, 0 }, false, curveOffset, 0, false,
				new float[] { 0, 0, 0 }, 0.5f);
	}

	private void renderDimensionGroupConnections(GL2 gl) {

		if (relationAnalyzer == null || leftDimGroup == null || rightDimGroup == null)
			return;

		float splineFactor = 0.1f * x;

		gl.glLineWidth(1);
		for (GroupMatch groupMatch : hashGroupID2GroupMatches.values()) {

			GLBrick brick = groupMatch.getBrick();
			float xStart = 0;
			if (!leftDimGroup.isDetailBrickShown())
				xStart = -(leftDimGroup.getLayout().getSizeScaledX() - brick.getLayout()
						.getSizeScaledX()) / 2;

//			if (groupMatch.getBrick().isInOverviewMode())
//				continue;

			for (SubGroupMatch subGroupMatch : groupMatch.getSubGroupMatches()) {

				GLBrick subBrick = subGroupMatch.getBrick();
				
//				if (subBrick.getGroupID() != 4)
//					continue;

//				if (subGroupMatch.getBrick().isInOverviewMode())
//					continue;

				HashMap<SelectionType, Float> hashRatioToSelectionType = subGroupMatch
						.getHashRatioToSelectionType();

				float xEnd = x + subBrick.getLayout().getTranslateX()
						- rightDimGroup.getLayout().getTranslateX();

				gl.glPushName(glVisBricks.getPickingManager().getPickingID(
						glVisBricks.getID(), EPickingType.BRICK_CONNECTION_BAND,
						subGroupMatch.getConnectionBandID()));

				// Render selected portion
				for (SelectionType selectionType : hashRatioToSelectionType.keySet()) {

					float ratio = hashRatioToSelectionType.get(selectionType);
					float trendRatio = 0;
					float[] color = new float[] { 0, 0, 0 };

					if (selectionType == SelectionType.NORMAL
							&& !glVisBricks.isConnectionsOn()) {
						continue;
					}

					color = selectionType.getColor();

					if (glVisBricks.isConnectionsHighlightDynamic() == false) {

						if (selectionType == SelectionType.NORMAL) {

							// if (glVisBricks.getSelectedConnectionBandID() ==
							// subGroupMatch
							// .getConnectionBandID())
							// {
							// trendRatio = 0.5f;
							// color = new float[] { 0, 0, 0 };
							//
							// }
							// else
							// {
							trendRatio = 0.15f;
							// }
						} else {

							trendRatio = 0.5f;

						}
					} else {

						float maxRatio = Math.max(subGroupMatch.getLeftSimilarityRatio(), 
						subGroupMatch.getRightSimilarityRatio());
						if (maxRatio < 0.3f)
							trendRatio = (glVisBricks.getConnectionsFocusFactor() - maxRatio);
						else
							trendRatio = 1 - (glVisBricks.getConnectionsFocusFactor() + (1 - maxRatio));

						if (glVisBricks.getSelectedConnectionBandID() == subGroupMatch
								.getConnectionBandID()) {
							trendRatio = 0.8f;
						} else {
							// it would be too opaque if we use the factor
							// determined by the slider
							trendRatio /= 2f;
						}
					}

					if (ratio == 0)
						continue;

					float leftYDiff = subGroupMatch.getLeftAnchorYTop()
							- subGroupMatch.getLeftAnchorYBottom();
					float leftYDiffSelection = leftYDiff * ratio;

					float rightYDiff = subGroupMatch.getRightAnchorYTop()
							- subGroupMatch.getRightAnchorYBottom();
					float rightYDiffSelection = rightYDiff * ratio;
				
					connectionRenderer.renderSingleBand(gl, new float[] { 0,
							subGroupMatch.getLeftAnchorYTop(), 0.0f },
							new float[] {
									0,
									subGroupMatch.getLeftAnchorYTop()
											- leftYDiffSelection, 0.0f }, new float[] {
									x, subGroupMatch.getRightAnchorYTop(), 0.0f },
							new float[] {
									x,
									subGroupMatch.getRightAnchorYTop()
											- rightYDiffSelection, 0.0f }, true,
							splineFactor, 0, false, color, trendRatio);// 0.15f);

					// Render straight band connection from brick to dimension
					// group
					// on the LEFT
					if (xStart != 0) {
						connectionRenderer.renderStraightBand(gl, new float[] { xStart,
								subGroupMatch.getLeftAnchorYTop(), 0 },
								new float[] {
										xStart,
										subGroupMatch.getLeftAnchorYTop()
												- leftYDiffSelection, 0 }, new float[] {
										0, subGroupMatch.getLeftAnchorYTop(), 0 },
								new float[] {
										0,
										subGroupMatch.getLeftAnchorYTop()
												- leftYDiffSelection, 0 }, false,
								splineFactor, 0, false, color, trendRatio);// 0.5f);
					}

					// Render straight band connection from brick to dimension
					// group
					// on the RIGHT
					if (xEnd != 0) {

						connectionRenderer
								.renderStraightBand(
										gl,
										new float[] { x,
												subGroupMatch.getRightAnchorYTop(), 0 },
										new float[] {
												x,
												subGroupMatch.getRightAnchorYTop()
														- rightYDiffSelection, 0 },
										new float[] { xEnd,
												subGroupMatch.getRightAnchorYTop(), 0 },
										new float[] {
												xEnd,
												subGroupMatch.getRightAnchorYTop()
														- rightYDiffSelection, 0 },
										false, splineFactor, 0, false, color, trendRatio);// 0.5f);
					}

				}

				gl.glPopName();
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

	@Override
	public void handleDragOver(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {

		setRenderSpacer(true);
	}

	@Override
	public void handleDrop(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {

		glVisBricks.clearDimensionGroupSpacerHighlight();

		for (IDraggable draggable : draggables) {

			if (draggable == this)
				break;

			glVisBricks
					.moveDimensionGroup(this, (DimensionGroup) draggable, leftDimGroup);
		}

		draggables.clear();
	}

	public int getID() {
		return ID;
	}

	public DimensionGroup getLeftDimGroup() {
		return leftDimGroup;
	}

	public DimensionGroup getRightDimGroup() {
		return rightDimGroup;
	}

	public void setLeftDimGroup(DimensionGroup leftDimGroup) {
		this.leftDimGroup = leftDimGroup;
	}

	public void setRightDimGroup(DimensionGroup rightDimGroup) {
		this.rightDimGroup = rightDimGroup;
	}
}
