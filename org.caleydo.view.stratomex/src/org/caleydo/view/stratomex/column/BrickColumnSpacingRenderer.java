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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
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
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.stratomex.BrickConnection;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.ui.RectangleCoordinates;
import org.eclipse.core.runtime.IStatus;
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
public class BrickColumnSpacingRenderer extends ALayoutRenderer implements IDropArea {

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

	private GLStratomex stratomex;

	private boolean hovered = false;

	public BrickColumnSpacingRenderer(RelationAnalyzer relationAnalyzer, ConnectionBandRenderer connectionRenderer,
			BrickColumn leftDimGroup, BrickColumn rightDimGroup, GLStratomex glVisBricksView) {

		this.relationAnalyzer = relationAnalyzer;
		this.leftDimGroup = leftDimGroup;
		this.rightDimGroup = rightDimGroup;
		this.connectionRenderer = connectionRenderer;
		this.stratomex = glVisBricksView;

		stratomex.getBrickColumnManager().getBrickColumnSpacers().put(ID, this);
	}

	{
		ID = GeneralManager.get().getIDCreator().createID(ManagedObjectType.DIMENSION_GROUP_SPACER);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void init() {

		if (relationAnalyzer == null || leftDimGroup == null || rightDimGroup == null)
			return;

		hashGroupID2GroupMatches.clear();
		stratomex.getHashTablePerspectivesToConnectionBandID().clear();

		List<GLBrick> leftBricks = leftDimGroup.getBricksForRelations();
		List<GLBrick> rightBricks = rightDimGroup.getBricksForRelations();

		if (leftBricks.size() == 0 || rightBricks.size() == 0)
			return;

		SimilarityMap similarityMap = relationAnalyzer.getSimilarityMap(leftDimGroup.getTablePerspective()
				.getRecordPerspective().getPerspectiveID());

		if (similarityMap == null)
			return;

		VASimilarity vaSimilarityMap = similarityMap.getVASimilarity(rightDimGroup.getTablePerspective()
				.getRecordPerspective().getPerspectiveID());
		if (vaSimilarityMap == null)
			return;

		for (GLBrick leftBrick : leftBricks) {

			if (leftBrick.isHeaderBrick())
				continue;

			Group group = leftBrick.getTablePerspective().getRecordGroup();
			GroupMatch groupMatch = new GroupMatch(leftBrick, group);
			hashGroupID2GroupMatches.put(group.getGroupIndex(), groupMatch);

			RectangleCoordinates leftBrickElementLayout = leftBrick.getLayoutForConnections();

			GroupSimilarity leftGroupSimilarity = vaSimilarityMap.getGroupSimilarity(leftDimGroup.getTablePerspective()
					.getRecordPerspective().getPerspectiveID(), leftBrick.getTablePerspective().getRecordGroup()
					.getGroupIndex());

			float[] leftSimilarities = leftGroupSimilarity.getSimilarities();
			float leftSimilarityOffsetY = 0;

			for (GLBrick rightBrick : rightBricks) {

				if (rightBrick.isHeaderBrick())
					continue;

				Group subGroup = rightBrick.getTablePerspective().getRecordGroup();
				SubGroupMatch subGroupMatch = new SubGroupMatch(stratomex.getNextConnectionBandID(), rightBrick,
						subGroup);
				groupMatch.addSubGroupMatch(subGroup.getGroupIndex(), subGroupMatch);

				VirtualArray similarityVA = leftGroupSimilarity.getSimilarityVAs(rightBrick.getTablePerspective()
						.getRecordGroup().getGroupIndex());

				BrickConnection brickConnectionBand = new BrickConnection();
				brickConnectionBand.setConnectionBandID(subGroupMatch.getConnectionBandID());
				brickConnectionBand.setLeftBrick(leftBrick);
				brickConnectionBand.setRightBrick(rightBrick);
				brickConnectionBand.setSharedRecordVirtualArray(similarityVA);
				stratomex.getHashConnectionBandIDToRecordVA().put(subGroupMatch.getConnectionBandID(),
						brickConnectionBand);

				Perspective leftRecordPerspective = leftBrick.getTablePerspective().getRecordPerspective();
				Perspective rightRecordPerspective = rightBrick.getTablePerspective().getRecordPerspective();
				HashMap<Perspective, HashMap<Perspective, BrickConnection>> hashRecordPerspectivesToConnectionBandID = stratomex
						.getHashTablePerspectivesToConnectionBandID();

				if (hashRecordPerspectivesToConnectionBandID.containsKey(leftRecordPerspective)) {
					hashRecordPerspectivesToConnectionBandID.get(leftRecordPerspective).put(rightRecordPerspective,
							brickConnectionBand);
				} else {
					HashMap<Perspective, BrickConnection> tmp = new HashMap<Perspective, BrickConnection>();
					tmp.put(rightRecordPerspective, brickConnectionBand);
					hashRecordPerspectivesToConnectionBandID.put(leftRecordPerspective, tmp);
				}

				calculateSubMatchSelections(subGroupMatch, similarityVA);

				float leftSimilarityRatioY = leftSimilarities[rightBrick.getTablePerspective().getRecordGroup()
						.getGroupIndex()];
				leftSimilarityOffsetY += leftSimilarityRatioY;

				subGroupMatch.setSimilarityRatioLeft(leftSimilarityRatioY);

				subGroupMatch.setLeftAnchorYStart(leftBrickElementLayout.getBottom()
						+ leftBrickElementLayout.getHeight() * (leftSimilarityOffsetY));

				subGroupMatch.setLeftAnchorYEnd(leftBrickElementLayout.getBottom() + leftBrickElementLayout.getHeight()
						* (leftSimilarityOffsetY - leftSimilarityRatioY));
			}
		}

		for (GLBrick rightBrick : rightBricks) {
			if (rightBrick.isHeaderBrick())
				continue;
			RectangleCoordinates rightBrickElementLayout = rightBrick.getLayoutForConnections();

			GroupSimilarity rightGroupSimilarity = vaSimilarityMap.getGroupSimilarity(rightDimGroup
					.getTablePerspective().getRecordPerspective().getPerspectiveID(), rightBrick.getTablePerspective()
					.getRecordGroup().getGroupIndex());

			float[] rightSimilarities = rightGroupSimilarity.getSimilarities();

			float rightSimilarityOffsetY = 0;

			for (GLBrick leftBrick : leftBricks) {
				if (leftBrick.isHeaderBrick())
					continue;
				GroupMatch groupMatch = hashGroupID2GroupMatches.get(leftBrick.getTablePerspective().getRecordGroup()
						.getGroupIndex());
				SubGroupMatch subGroupMatch = groupMatch.getSubGroupMatch(rightBrick.getTablePerspective()
						.getRecordGroup().getGroupIndex());

				float rightSimilarityRatioY = rightSimilarities[leftBrick.getTablePerspective().getRecordGroup()
						.getGroupIndex()];
				rightSimilarityOffsetY += rightSimilarityRatioY;

				subGroupMatch.setSimilarityRatioRight(rightSimilarityRatioY);

				subGroupMatch.setRightAnchorYStart(rightBrickElementLayout.getBottom()
						+ rightBrickElementLayout.getHeight() * (rightSimilarityOffsetY));

				subGroupMatch.setRightAnchorYEnd(rightBrickElementLayout.getBottom()
						+ rightBrickElementLayout.getHeight() * (rightSimilarityOffsetY - rightSimilarityRatioY));

			}
		}
	}

	private void calculateSubMatchSelections(SubGroupMatch subGroupMatch, VirtualArray recordVA) {

		if (recordVA.size() == 0)
			return;

		SelectionManager recordSelectionManager = stratomex.getRecordSelectionManager();

		float ratio = 0;

		// Iterate over all selection types
		for (SelectionType selectionType : recordSelectionManager.getSelectionTypes()) {

			if (selectionType == SelectionType.MOUSE_OVER || selectionType == SelectionType.DESELECTED
					|| selectionType == SelectionType.LEVEL_HIGHLIGHTING)
				continue;

			Set<Integer> selectedByGroupSelections = recordSelectionManager.getElements(selectionType);

			if (selectedByGroupSelections == null || selectedByGroupSelections.size() == 0) {

				ratio = 1;// (float) recordVA.size()
				// / subGroupMatch.getBrick().getRecordVA().size();

				subGroupMatch.addSelectionTypeRatio(ratio, SelectionType.NORMAL);
				continue;
			}

			int intersectionCount = 0;
			IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
					recordVA.getIdType().getIDCategory());
			for (Integer recordID : recordVA) {
				if (recordVA.getIdType() != recordSelectionManager.getIDType()) {
					IDType destIDType = recordSelectionManager.getIDType();

					Set<Integer> recordIDs = mappingManager.getIDAsSet(recordVA.getIdType(), destIDType, recordID);
					if (recordIDs == null)
						continue;
					recordID = recordIDs.iterator().next();
					if (recordIDs.size() > 1) {
						Logger.log(new Status(IStatus.WARNING, this.toString(), "Multi-Mapping, not handled"));
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

		// FIXME Stratomex 2.0 testing
		// if (leftDimGroup != null && rightDimGroup != null) {
		// float score =
		// leftDimGroup.getTablePerspective().getContainerStatistics()
		// .getAdjustedRandIndex()
		// .getScore(rightDimGroup.getTablePerspective(), true);

		// glVisBricks.getTextRenderer().renderText(gl,
		// new Float(score).toString().substring(0, 4), 0, 0, 0, 0.004f,
		// 50);
		// }
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	private void renderBackground(GL2 gl) {

		int pickingID = stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.DIMENSION_GROUP_SPACER.name(), ID);

		gl.glPushName(pickingID);
		gl.glColor4f(1f, 1f, 1f, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, 0, 0.f);
		gl.glVertex3f(x, 0, 0.f);
		gl.glVertex3f(x, y, 0.f);
		gl.glVertex3f(0, y, 0.f);
		gl.glEnd();
		gl.glPopName();
	}

	private void renderDragAndDropMarker(GL2 gl) {

		if (renderDragAndDropMarker) {
			gl.glColor3fv(DRAG_AND_DROP_MARKER_COLOR, 0);
			gl.glLineWidth(3);

			gl.glBegin(GL.GL_LINES);
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

		final int pickingID = stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.DIMENSION_GROUP_SPACER_HEADER.name(), ID);

		// handle situation where no group is contained in center arch
		if (leftDimGroup == null && rightDimGroup == null && stratomex != null) {

			leftCenterBrickBottom = stratomex.getArchBottomY();
			leftCenterBrickTop = stratomex.getArchTopY();

			rightCenterBrickBottom = stratomex.getArchBottomY();
			rightCenterBrickTop = stratomex.getArchTopY();
		}

		if (leftDimGroup != null) {
			if (leftDimGroup.isDetailBrickShown() && !leftDimGroup.isExpandLeft()) {
				return;
			}

			GLBrick leftCenterBrick = leftDimGroup.getHeaderBrick();

			ElementLayout layout = leftCenterBrick.getLayout();
			leftCenterBrickBottom = layout.getTranslateY();
			leftCenterBrickTop = layout.getTranslateY() + layout.getSizeScaledY();

			if (!leftDimGroup.isDetailBrickShown())
				xStart = leftDimGroup.getLayout().getTranslateX() - leftCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from center brick to dimension
			// group on
			// the LEFT
			if (xStart != 0) {
				connectionRenderer.renderStraightBand(gl, new float[] { xStart, leftCenterBrickTop, 0 }, new float[] {
						xStart, leftCenterBrickBottom, 0 }, new float[] { 0, leftCenterBrickTop, 0 }, new float[] { 0,
						leftCenterBrickBottom, 0 }, false, 0, GLStratomex.ARCH_COLOR, GLStratomex.ARCH_COLOR[3], true);
				// render add button
				gl.glPushName(pickingID);
				drawQuad(gl, xStart, leftCenterBrickTop, x, leftCenterBrickBottom - leftCenterBrickTop);
				if (hovered)
					stratomex.getTourguide().renderAddButton(gl, xStart, leftCenterBrickTop, x,
							leftCenterBrickBottom - leftCenterBrickTop, ID);
				gl.glPopName();
			}

		} else {
			if (rightDimGroup != null) {
				leftCenterBrickBottom = stratomex.getArchBottomY();
				leftCenterBrickTop = stratomex.getArchTopY();

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
				xEnd = x + rightCenterBrick.getLayout().getTranslateX() - rightDimGroup.getLayout().getTranslateX();
			else
				xEnd = rightCenterBrick.getLayout().getTranslateX();

			// Render straight band connection from header brick to dimension
			// group on
			// the RIGHT
			if (xEnd != 0 && !(xEnd < x + 0.000001f && xEnd > x - 0.000001f)) {

				connectionRenderer.renderStraightBand(gl, new float[] { x, rightCenterBrickTop, 0 }, new float[] { x,
						rightCenterBrickBottom, 0 }, new float[] { xEnd, rightCenterBrickTop, 0 }, new float[] { xEnd,
						rightCenterBrickBottom, 0 }, false, 0, GLStratomex.ARCH_COLOR, GLStratomex.ARCH_COLOR[3], true);
				// render add button
				gl.glPushName(pickingID);
				drawQuad(gl, x, rightCenterBrickTop, xEnd - x, rightCenterBrickBottom - rightCenterBrickTop);
				if (hovered)
					stratomex.getTourguide().renderAddButton(gl, x, rightCenterBrickTop, xEnd - x,
							rightCenterBrickBottom - rightCenterBrickTop, ID);
				gl.glPopName();

			}

		} else {
			if (leftDimGroup != null) {
				rightCenterBrickBottom = stratomex.getArchBottomY();
				rightCenterBrickTop = stratomex.getArchTopY();

			}
		}

		if (leftCenterBrickBottom == 0 && rightCenterBrickBottom == 0)
			return;

		connectionRenderer.renderSingleBand(gl, new float[] { 0, leftCenterBrickTop, 0 }, new float[] { 0,
				leftCenterBrickBottom, 0 }, new float[] { x, rightCenterBrickTop, 0 }, new float[] { x,
				rightCenterBrickBottom, 0 }, false, curveOffset, 0, GLStratomex.ARCH_COLOR, true);

		// render add button
		gl.glPushName(pickingID);
		drawQuad(gl, 0, (leftCenterBrickTop + rightCenterBrickTop) * 0.5f, x, (leftCenterBrickBottom
				- leftCenterBrickTop + rightCenterBrickBottom - rightCenterBrickTop) * 0.5f);
		if (hovered)
			stratomex.getTourguide().renderAddButton(gl, 0, (leftCenterBrickTop + rightCenterBrickTop) * 0.5f, x,
					(leftCenterBrickBottom - leftCenterBrickTop + rightCenterBrickBottom - rightCenterBrickTop) * 0.5f,
					ID);
		gl.glPopName();
	}

	private static void drawQuad(GL2 gl, float x, float y, float w, float h) {
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(1, 1, 1, 0);
		gl.glVertex3f(x, y, 1.f);
		gl.glVertex3f(x + w, y, 1.f);
		gl.glVertex3f(x + w, y + h, 1.f);
		gl.glVertex3f(x, y + h, 1.f);
		gl.glEnd();
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
				xStart = -(leftDimGroup.getLayout().getSizeScaledX() - brick.getLayoutForConnections().getWidth()) / 2;

			// if (groupMatch.getBrick().isInOverviewMode())
			// continue;

			for (SubGroupMatch subGroupMatch : groupMatch.getSubGroupMatches()) {

				GLBrick subBrick = subGroupMatch.getBrick();

				// if (subBrick.getGroupID() != 4)
				// continue;

				// if (subGroupMatch.getBrick().isInOverviewMode())
				// continue;

				HashMap<SelectionType, Float> hashRatioToSelectionType = subGroupMatch.getHashRatioToSelectionType();

				float xEnd = x + subBrick.getLayout().getTranslateX() - rightDimGroup.getLayout().getTranslateX();

				gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(),
						EPickingType.BRICK_CONNECTION_BAND.name(), subGroupMatch.getConnectionBandID()));

				// Render selected portion
				for (SelectionType selectionType : hashRatioToSelectionType.keySet()) {

					float ratio = hashRatioToSelectionType.get(selectionType);
					float trendRatio = 0;
					float[] color = new float[] { 0, 0, 0, 1 };

					if (selectionType == SelectionType.NORMAL && !stratomex.isConnectionsOn()) {
						continue;
					}

					color = selectionType.getColor().getRGBA();

					if (stratomex.isConnectionsHighlightDynamic() == false) {

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
							trendRatio = (stratomex.getConnectionsFocusFactor() - maxRatio);
						else
							trendRatio = 1 - (stratomex.getConnectionsFocusFactor() + (1 - maxRatio));

						if (stratomex.getSelectedConnectionBandID() == subGroupMatch.getConnectionBandID()) {
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

					float leftYDiff = subGroupMatch.getLeftAnchorYTop() - subGroupMatch.getLeftAnchorYBottom();
					float leftYDiffSelection = leftYDiff * ratio;

					float rightYDiff = subGroupMatch.getRightAnchorYTop() - subGroupMatch.getRightAnchorYBottom();
					float rightYDiffSelection = rightYDiff * ratio;

					// gl.glPushMatrix();
					// gl.glTranslatef(0, 0, 0.1f);

					connectionRenderer.renderSingleBand(gl, new float[] { 0, subGroupMatch.getLeftAnchorYTop(), 0.0f },
							new float[] { 0, subGroupMatch.getLeftAnchorYTop() - leftYDiffSelection, 0.0f },
							new float[] { x, subGroupMatch.getRightAnchorYTop(), 0.0f },
							new float[] { x, subGroupMatch.getRightAnchorYTop() - rightYDiffSelection, 0.0f }, true,
							curveOffset, 0, color);// 0.15f);

					// Render straight band connection from brick to dimension
					// group on the LEFT. This is for the smaller bricks when
					// the bricks are not of equal size
					if (xStart != 0) {
						connectionRenderer.renderStraightBand(gl,
								new float[] { xStart, subGroupMatch.getLeftAnchorYTop(), 0 }, new float[] { xStart,
										subGroupMatch.getLeftAnchorYTop() - leftYDiffSelection, 0 }, new float[] { 0,
										subGroupMatch.getLeftAnchorYTop(), 0 },
								new float[] { 0, subGroupMatch.getLeftAnchorYTop() - leftYDiffSelection, 0 }, false, 0,
								color, trendRatio);// 0.5f);
					}

					// Render straight band connection from brick to dimension
					// group on the RIGHT. This is for the smaller bricks when
					// the bricks are not of equal size
					if (xEnd != 0) {

						connectionRenderer.renderStraightBand(gl, new float[] { x, subGroupMatch.getRightAnchorYTop(),
								0 }, new float[] { x, subGroupMatch.getRightAnchorYTop() - rightYDiffSelection, 0 },
								new float[] { xEnd, subGroupMatch.getRightAnchorYTop(), 0 }, new float[] { xEnd,
										subGroupMatch.getRightAnchorYTop() - rightYDiffSelection, 0 }, false, 0, color,
								trendRatio);// 0.5f);
					}

					// FIXME Stratomex 2.0 testing

					// glVisBricks.getTextRenderer().begin3DRendering();

					// String similarity =
					// Float.toString(subGroupMatch.getLeftSimilarityRatio());
					// if (similarity.length() >= 4)
					// similarity = similarity.substring(0, 4);
					//
					// glVisBricks.getTextRenderer().draw3D(gl, similarity,
					// xStart,
					// subGroupMatch.getLeftAnchorYTop(), 0.5f, 0.003f, 50);
					//
					// similarity =
					// Float.toString(subGroupMatch.getRightSimilarityRatio());
					// if (similarity.length() >= 4)
					// similarity = similarity.substring(0, 4);
					//
					// glVisBricks.getTextRenderer().draw3D(gl, similarity, xEnd
					// - .2f,
					// subGroupMatch.getLeftAnchorYTop(), 0.5f, 0.003f, 50);

					// HashMap<Group, HashMap<Group, Float>> groupToSubGroup =
					// leftDimGroup.getTablePerspective()
					// .getContainerStatistics().getJaccardIndex()
					// .getScore(rightDimGroup.getTablePerspective(), true);
					//
					// HashMap<Group, Float> subGroupToScore = groupToSubGroup
					// .get(subGroupMatch.getSubGroup());
					//
					// float jacc = subGroupToScore.get(groupMatch.getGroup());
					//
					// String jaccardIndex = Float.toString(jacc);
					//
					// if (jaccardIndex.length() >= 4)
					// jaccardIndex = jaccardIndex.substring(0, 4);
					//
					// glVisBricks.getTextRenderer().draw3D(gl, jaccardIndex,
					// xStart,
					// subGroupMatch.getLeftAnchorYTop(), 0.5f, 0.003f, 50);
					//
					// glVisBricks.getTextRenderer().end3DRendering();
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
	public void handleDragOver(GL2 gl, java.util.Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY) {

		setRenderSpacer(true);
	}

	@Override
	public void handleDrop(GL2 gl, java.util.Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {

		stratomex.clearColumnSpacerHighlight();

		for (IDraggable draggable : draggables) {

			if (draggable == this)
				break;

			if (!(draggable instanceof BrickColumn)) {
				System.out.println("CHRISTIAN HEEEEELP!!");
				break;
			}
			stratomex.moveColumn(this, (BrickColumn) draggable, leftDimGroup);
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

	/**
	 * @param b
	 */
	public void setHeaderHovered(boolean hovered) {
		this.hovered = hovered;
		setDisplayListDirty(true);
	}
}
