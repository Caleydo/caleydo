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
package org.caleydo.view.stratomex.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.stratomex.BrickConnection;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.ui.RectangleCoordinates;
import org.eclipse.core.runtime.Status;

/**
 * 
 * Renders the connection band between the dimension groups.
 * 
 * FIXME: Improve documentation
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public class BrickColumnSpacingRenderer extends LayoutRenderer implements IDropArea {

	public static float[] DRAG_AND_DROP_MARKER_COLOR = { 0.5f, 0.5f, 0.5f };

	private int ID;

	private boolean renderDragAndDropMarker = false;

	private boolean isVertical = true;

	/** The DimensionGroup left of the spacer */
	private BrickColumn leftDimGroup;
	/** The DimensionGroup right of the spacer */
	private BrickColumn rightDimGroup;

	private RelationAnalyzer relationAnalyzer;

	private HashMap<Integer, GroupMatch> hashGroupID2GroupMatches = new HashMap<Integer, GroupMatch>();

	private ConnectionBandRenderer connectionRenderer = new ConnectionBandRenderer();

	private GLStratomex glVisBricks;

	/**
	 * Stores all created connectionBandIDs so that they can be removed once
	 * this object is destroyed
	 */
	ArrayList<Integer> ribbonIDs = new ArrayList<Integer>();

	public BrickColumnSpacingRenderer(RelationAnalyzer relationAnalyzer,
			ConnectionBandRenderer connectionRenderer, BrickColumn leftDimGroup,
			BrickColumn rightDimGroup, GLStratomex glVisBricksView) {

		this.relationAnalyzer = relationAnalyzer;
		this.leftDimGroup = leftDimGroup;
		this.rightDimGroup = rightDimGroup;
		this.connectionRenderer = connectionRenderer;
		this.glVisBricks = glVisBricksView;

		glVisBricks.getDimensionGroupManager().getBrickColumnSpacers().put(ID, this);
	}

	{
		ID = GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.DIMENSION_GROUP_SPACER);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		// remove the brick connections from visbricks
		// HashMap<Integer, BrickConnection> brickConnections = glVisBricks
		// .getHashConnectionBandIDToRecordVA();
		// for (Integer ribbonID : ribbonIDs) {
		// brickConnections.remove(ribbonID);
		// }
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
				.getTablePerspective().getRecordPerspective().getPerspectiveID());

		if (similarityMap == null)
			return;

		VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarityMap = similarityMap
				.getVASimilarity(rightDimGroup.getTablePerspective().getRecordPerspective()
						.getPerspectiveID());
		if (vaSimilarityMap == null)
			return;

		for (GLBrick leftBrick : leftBricks) {
			if (leftBrick.isHeaderBrick())
				continue;
			GroupMatch groupMatch = new GroupMatch(leftBrick);
			hashGroupID2GroupMatches.put(leftBrick.getTablePerspective().getRecordGroup()
					.getGroupIndex(), groupMatch);

			RectangleCoordinates leftBrickElementLayout = leftBrick
					.getLayoutForConnections();

			GroupSimilarity<RecordVirtualArray, RecordGroupList> leftGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(leftDimGroup.getTablePerspective()
							.getRecordPerspective().getPerspectiveID(), leftBrick.getTablePerspective()
							.getRecordGroup().getGroupIndex());

			float[] leftSimilarities = leftGroupSimilarity.getSimilarities();
			float leftSimilarityOffsetY = 0;

			for (GLBrick rightBrick : rightBricks) {
				if (rightBrick.isHeaderBrick())
					continue;
				SubGroupMatch subGroupMatch = new SubGroupMatch(
						glVisBricks.getNextConnectionBandID(), rightBrick);
				groupMatch.addSubGroupMatch(rightBrick.getTablePerspective()
						.getRecordGroup().getGroupIndex(), subGroupMatch);

				RecordVirtualArray similarityVA = leftGroupSimilarity
						.getSimilarityVAs(rightBrick.getTablePerspective().getRecordGroup()
								.getGroupIndex());

				ribbonIDs.add(subGroupMatch.getConnectionBandID());
				BrickConnection brickConnection = new BrickConnection();
				brickConnection.setConnectionBandID(subGroupMatch.getConnectionBandID());
				brickConnection.setLeftBrick(leftBrick);
				brickConnection.setRightBrick(rightBrick);
				brickConnection.setSharedRecordVirtualArray(similarityVA);
				glVisBricks.getHashConnectionBandIDToRecordVA().put(
						subGroupMatch.getConnectionBandID(), brickConnection);

				calculateSubMatchSelections(subGroupMatch, similarityVA);

				float leftSimilarityRatioY = leftSimilarities[rightBrick
						.getTablePerspective().getRecordGroup().getGroupIndex()];
				leftSimilarityOffsetY += leftSimilarityRatioY;

				subGroupMatch.setSimilarityRatioLeft(leftSimilarityRatioY);

				subGroupMatch.setLeftAnchorYStart(leftBrickElementLayout.getBottom()
						+ leftBrickElementLayout.getHeight() * (leftSimilarityOffsetY));

				subGroupMatch.setLeftAnchorYEnd(leftBrickElementLayout.getBottom()
						+ leftBrickElementLayout.getHeight()
						* (leftSimilarityOffsetY - leftSimilarityRatioY));
			}
		}

		for (GLBrick rightBrick : rightBricks) {
			if (rightBrick.isHeaderBrick())
				continue;
			RectangleCoordinates rightBrickElementLayout = rightBrick
					.getLayoutForConnections();

			GroupSimilarity<RecordVirtualArray, RecordGroupList> rightGroupSimilarity = vaSimilarityMap
					.getGroupSimilarity(rightDimGroup.getTablePerspective()
							.getRecordPerspective().getPerspectiveID(), rightBrick
							.getTablePerspective().getRecordGroup().getGroupIndex());

			float[] rightSimilarities = rightGroupSimilarity.getSimilarities();

			float rightSimilarityOffsetY = 0;

			for (GLBrick leftBrick : leftBricks) {
				if (leftBrick.isHeaderBrick())
					continue;
				GroupMatch groupMatch = hashGroupID2GroupMatches.get(leftBrick
						.getTablePerspective().getRecordGroup().getGroupIndex());
				SubGroupMatch subGroupMatch = groupMatch.getSubGroupMatch(rightBrick
						.getTablePerspective().getRecordGroup().getGroupIndex());

				float rightSimilarityRatioY = rightSimilarities[leftBrick
						.getTablePerspective().getRecordGroup().getGroupIndex()];
				rightSimilarityOffsetY += rightSimilarityRatioY;

				subGroupMatch.setSimilarityRatioRight(rightSimilarityRatioY);

				subGroupMatch.setRightAnchorYStart(rightBrickElementLayout.getBottom()
						+ rightBrickElementLayout.getHeight() * (rightSimilarityOffsetY));

				subGroupMatch.setRightAnchorYEnd(rightBrickElementLayout.getBottom()
						+ rightBrickElementLayout.getHeight()
						* (rightSimilarityOffsetY - rightSimilarityRatioY));

			}
		}
	}

	private void calculateSubMatchSelections(SubGroupMatch subGroupMatch,
			RecordVirtualArray recordVA) {

		if (recordVA.size() == 0)
			return;

		RecordSelectionManager recordSelectionManager = glVisBricks
				.getRecordSelectionManager();

		float ratio = 0;

		// Iterate over all selection types
		for (SelectionType selectionType : recordSelectionManager.getSelectionTypes()) {

			if (selectionType == SelectionType.MOUSE_OVER
					|| selectionType == SelectionType.DESELECTED
					|| selectionType == SelectionType.LEVEL_HIGHLIGHTING)
				continue;

			Set<Integer> selectedByGroupSelections = recordSelectionManager
					.getElements(selectionType);

			if (selectedByGroupSelections == null
					|| selectedByGroupSelections.size() == 0) {

				ratio = 1;// (float) recordVA.size()
				// / subGroupMatch.getBrick().getRecordVA().size();

				subGroupMatch.addSelectionTypeRatio(ratio, SelectionType.NORMAL);
				continue;
			}

			int intersectionCount = 0;
			IDMappingManager mappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(recordVA.getIdType().getIDCategory());
			for (Integer recordID : recordVA) {
				if (recordVA.getIdType() != recordSelectionManager.getIDType()) {
					IDType destIDType = recordSelectionManager.getIDType();

					Set<Integer> recordIDs = mappingManager.getIDAsSet(
							recordVA.getIdType(), destIDType, recordID);
					if(recordIDs == null)
						continue;
					recordID = recordIDs.iterator().next();
					if (recordIDs.size() > 1) {
						Logger.log(new Status(Status.WARNING, this.toString(),
								"Multi-Mapping, not handled"));
					}
				}

				if (recordID != null && selectedByGroupSelections.contains(recordID))
					intersectionCount++;
			}

			ratio = (float) intersectionCount / recordVA.size();

			subGroupMatch.addSelectionTypeRatio(ratio, selectionType);
		}
	}

	@Override
	public void renderContent(GL2 gl) {

		renderBackground(gl);

		renderFlexibleArch(gl);
		renderDimensionGroupConnections(gl);
		renderDragAndDropMarker(gl);
	}
	
	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}

	private void renderBackground(GL2 gl) {

		int pickingID = glVisBricks.getPickingManager().getPickingID(glVisBricks.getID(),
				EPickingType.DIMENSION_GROUP_SPACER.name(), ID);
		

		gl.glPushName(pickingID);
		gl.glColor4f(1, 1, 1, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(x, 0);
		gl.glVertex2f(x, y);
		gl.glVertex2f(0, y);
		gl.glEnd();
		gl.glPopName();
	}

	private void renderDragAndDropMarker(GL2 gl) {

		if (renderDragAndDropMarker) {
			gl.glColor3fv(DRAG_AND_DROP_MARKER_COLOR, 0);
			gl.glLineWidth(3);

			gl.glBegin(GL2.GL_LINES);
			if (isVertical) {
				gl.glVertex3f(x / 2f, 0, 1f);
				gl.glVertex3f(x / 2f, y, 1f);
			} else {
				gl.glVertex3f(0, y / 2f, 1f);
				gl.glVertex3f(x, y / 2f, 1f);
			}
			gl.glEnd();

			renderDragAndDropMarker = false;
		}
	}

	/** Renders the center parts of the arch except for the legs */
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

		/** A offset determining when the bend starts */
		float curveOffset = x * 0.2f;

		float xStart = 0;
		float xEnd;

		// handle situation where no group is contained in center arch
		if (leftDimGroup == null && rightDimGroup == null && glVisBricks != null) {

			leftCenterBrickBottom = glVisBricks.getArchBottomY();
			leftCenterBrickTop = glVisBricks.getArchTopY();

			rightCenterBrickBottom = glVisBricks.getArchBottomY();
			rightCenterBrickTop = glVisBricks.getArchTopY();
		}

		if (leftDimGroup != null) {
			if (leftDimGroup.isDetailBrickShown() && !leftDimGroup.isExpandLeft())
				return;

			GLBrick leftCenterBrick = leftDimGroup.getHeaderBrick();

			ElementLayout layout = leftCenterBrick.getLayout();
			leftCenterBrickBottom = layout.getTranslateY();
			leftCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledY();

			if (!leftDimGroup.isDetailBrickShown())
				xStart = leftDimGroup.getLayout().getTranslateX()
						- leftCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from center brick to dimension
			// group on
			// the LEFT
			if (xStart != 0) {
				connectionRenderer.renderStraightBand(gl, new float[] { xStart,
						leftCenterBrickTop, 0 }, new float[] { xStart,
						leftCenterBrickBottom, 0 }, new float[] { 0, leftCenterBrickTop,
						0 }, new float[] { 0, leftCenterBrickBottom, 0 }, false, 0,
						GLStratomex.ARCH_COLOR, GLStratomex.ARCH_COLOR[3], true);
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
			GLBrick rightCenterBrick = rightDimGroup.getHeaderBrick();

			ElementLayout layout = rightCenterBrick.getLayout();
			rightCenterBrickBottom = layout.getTranslateY();
			rightCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledY();

			if (!rightDimGroup.isDetailBrickShown())
				xEnd = x + rightCenterBrick.getLayout().getTranslateX()
						- rightDimGroup.getLayout().getTranslateX();
			else
				xEnd = rightCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from center brick to dimension
			// group on
			// the RIGHT
			if (xEnd != 0 && !(xEnd < x + 0.000001f && xEnd > x - 0.000001f)) {

				// gl.glPushMatrix();
				// gl.glTranslatef(0, 0, 0.1f);
				connectionRenderer.renderStraightBand(gl, new float[] { x,
						rightCenterBrickTop, 0 }, new float[] { x,
						rightCenterBrickBottom, 0 }, new float[] { xEnd,
						rightCenterBrickTop, 0 }, new float[] { xEnd,
						rightCenterBrickBottom, 0 }, false, 0, GLStratomex.ARCH_COLOR,
						GLStratomex.ARCH_COLOR[3], true);
				// gl.glPopMatrix();
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

		// gl.glPushMatrix();
		// gl.glTranslatef(0, 0, 0.1f);
		connectionRenderer.renderSingleBand(gl, new float[] { 0, leftCenterBrickTop, 0 },
				new float[] { 0, leftCenterBrickBottom, 0 }, new float[] { x,
						rightCenterBrickTop, 0 }, new float[] { x,
						rightCenterBrickBottom, 0 }, false, curveOffset, 0,
				GLStratomex.ARCH_COLOR, true);
		// gl.glPopMatrix();
	}

	private void renderDimensionGroupConnections(GL2 gl) {

		if (relationAnalyzer == null || leftDimGroup == null || rightDimGroup == null)
			return;

		/** A offset determining when the bend starts */
		float curveOffset = 0.1f * x;

		gl.glLineWidth(1);
		for (GroupMatch groupMatch : hashGroupID2GroupMatches.values()) {

			GLBrick brick = groupMatch.getBrick();
			float xStart = 0;
			if (!leftDimGroup.isDetailBrickShown())
				xStart = -(leftDimGroup.getLayout().getSizeScaledX() - brick
						.getLayoutForConnections().getWidth()) / 2;

			// if (groupMatch.getBrick().isInOverviewMode())
			// continue;

			for (SubGroupMatch subGroupMatch : groupMatch.getSubGroupMatches()) {

				GLBrick subBrick = subGroupMatch.getBrick();

				// if (subBrick.getGroupID() != 4)
				// continue;

				// if (subGroupMatch.getBrick().isInOverviewMode())
				// continue;

				HashMap<SelectionType, Float> hashRatioToSelectionType = subGroupMatch
						.getHashRatioToSelectionType();

				float xEnd = x + subBrick.getLayout().getTranslateX()
						- rightDimGroup.getLayout().getTranslateX();

				gl.glPushName(glVisBricks.getPickingManager().getPickingID(
						glVisBricks.getID(), EPickingType.BRICK_CONNECTION_BAND.name(),
						subGroupMatch.getConnectionBandID()));

				// Render selected portion
				for (SelectionType selectionType : hashRatioToSelectionType.keySet()) {

					float ratio = hashRatioToSelectionType.get(selectionType);
					float trendRatio = 0;
					float[] color = new float[] { 0, 0, 0, 1 };

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

					// set the transparency of the band
					color[3] = trendRatio;

					if (ratio == 0)
						continue;

					float leftYDiff = subGroupMatch.getLeftAnchorYTop()
							- subGroupMatch.getLeftAnchorYBottom();
					float leftYDiffSelection = leftYDiff * ratio;

					float rightYDiff = subGroupMatch.getRightAnchorYTop()
							- subGroupMatch.getRightAnchorYBottom();
					float rightYDiffSelection = rightYDiff * ratio;

					// gl.glPushMatrix();
					// gl.glTranslatef(0, 0, 0.1f);

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
							curveOffset, 0, color);// 0.15f);

					// Render straight band connection from brick to dimension
					// group on the LEFT. This is for the smaller bricks when
					// the bricks are not of equal size
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
												- leftYDiffSelection, 0 }, false, 0,
								color, trendRatio);// 0.5f);
					}

					// Render straight band connection from brick to dimension
					// group on the RIGHT. This is for the smaller bricks when
					// the bricks are not of equal size
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
										false, 0, color, trendRatio);// 0.5f);
					}
					// gl.glPopMatrix();
				}

				gl.glPopName();

			}
		}
	}

	public void setRenderSpacer(boolean renderSpacer) {
		this.renderDragAndDropMarker = renderSpacer;
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	// public void setLineLength(float lineLength) {
	// this.lineLength = lineLength;
	// }

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

			if (!(draggable instanceof BrickColumn)) {
				System.out.println("CHRISTIAN HEEEEELP!!");
				break;
			}
			glVisBricks
					.moveDimensionGroup(this, (BrickColumn) draggable, leftDimGroup);
		}

		draggables.clear();
	}

	public int getID() {
		return ID;
	}

	public BrickColumn getLeftDimGroup() {
		return leftDimGroup;
	}

	public BrickColumn getRightDimGroup() {
		return rightDimGroup;
	}

	public void setLeftDimGroup(BrickColumn leftDimGroup) {
		this.leftDimGroup = leftDimGroup;
	}

	public void setRightDimGroup(BrickColumn rightDimGroup) {
		this.rightDimGroup = rightDimGroup;
	}

	@Override
	public void handleDropAreaReplaced() {
		// TODO Auto-generated method stub

	}
}
