package org.caleydo.view.visbricks.dimensiongroup;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.similarity.GroupSimilarity;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.data.virtualarray.similarity.SimilarityMap;
import org.caleydo.core.data.virtualarray.similarity.VASimilarity;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.GLBrick;

public class DimensionGroupSpacingRenderer extends LayoutRenderer implements
	IDropArea {

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
	    ConnectionBandRenderer connectionRenderer,
	    DimensionGroup leftDimGroup, DimensionGroup rightDimGroup,
	    GLVisBricks glVisBricksView) {

	this.relationAnalyzer = relationAnalyzer;
	this.leftDimGroup = leftDimGroup;
	this.rightDimGroup = rightDimGroup;
	this.connectionRenderer = connectionRenderer;
	this.glVisBricks = glVisBricksView;

	glVisBricks.getDimensionGroupManager().getDimensionGroupSpacers()
		.put(ID, this);
    }

    {
	ID = GeneralManager.get().getIDCreator()
		.createID(ManagedObjectType.DIMENSION_GROUP_SPACER);
    }

    public void init() {

	if (relationAnalyzer == null || leftDimGroup == null
		|| rightDimGroup == null)
	    return;

	hashGroupID2GroupMatches.clear();

	List<GLBrick> leftBricks = leftDimGroup.getBricksForRelations();
	List<GLBrick> rightBricks = rightDimGroup.getBricksForRelations();

	if (leftBricks.size() == 0 || rightBricks.size() == 0)
	    return;

	SimilarityMap similarityMap = relationAnalyzer
		.getSimilarityMap(leftDimGroup.getDataContainer()
			.getRecordPerspective().getID());

	if (similarityMap == null)
	    return;

	VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarityMap = similarityMap
		.getVASimilarity(rightDimGroup.getDataContainer()
			.getRecordPerspective().getID());
	if (vaSimilarityMap == null)
	    return;

	for (GLBrick leftBrick : leftBricks) {

	    GroupMatch groupMatch = new GroupMatch(leftBrick);
	    hashGroupID2GroupMatches.put(leftBrick.getDataContainer()
		    .getRecordGroup().getGroupID(), groupMatch);

	    ElementLayout leftBrickElementLayout = leftBrick.getLayout();

	    GroupSimilarity<RecordVirtualArray, RecordGroupList> leftGroupSimilarity = vaSimilarityMap
		    .getGroupSimilarity(leftDimGroup.getDataContainer()
			    .getRecordPerspective().getID(), leftBrick
			    .getDataContainer().getRecordGroup().getGroupID());

	    float[] leftSimilarities = leftGroupSimilarity.getSimilarities();
	    float leftSimilarityOffsetY = 0;

	    for (GLBrick rightBrick : rightBricks) {

		SubGroupMatch subGroupMatch = new SubGroupMatch(
			glVisBricks.getNextConnectionBandID(), rightBrick);
		groupMatch.addSubGroupMatch(rightBrick.getDataContainer()
			.getRecordGroup().getGroupID(), subGroupMatch);

		calculateSubMatchSelections(
			subGroupMatch,
			leftGroupSimilarity.getSimilarityVAs(rightBrick
				.getDataContainer().getRecordGroup()
				.getGroupID()));

		float leftSimilarityRatioY = leftSimilarities[rightBrick
			.getDataContainer().getRecordGroup().getGroupID()];
		leftSimilarityOffsetY += leftSimilarityRatioY;

		subGroupMatch.setSimilarityRatioLeft(leftSimilarityRatioY);

		subGroupMatch.setLeftAnchorYStart(leftBrickElementLayout
			.getTranslateY()
			+ leftBrickElementLayout.getSizeScaledY()
			* (leftSimilarityOffsetY));

		subGroupMatch.setLeftAnchorYEnd(leftBrickElementLayout
			.getTranslateY()
			+ leftBrickElementLayout.getSizeScaledY()
			* (leftSimilarityOffsetY - leftSimilarityRatioY));
	    }
	}

	for (GLBrick rightBrick : rightBricks) {

	    ElementLayout rightBrickElementLayout = rightBrick.getLayout();

	    GroupSimilarity<RecordVirtualArray, RecordGroupList> rightGroupSimilarity = vaSimilarityMap
		    .getGroupSimilarity(rightDimGroup.getDataContainer()
			    .getRecordPerspective().getID(), rightBrick
			    .getDataContainer().getRecordGroup().getGroupID());

	    float[] rightSimilarities = rightGroupSimilarity.getSimilarities();

	    float rightSimilarityOffsetY = 0;

	    for (GLBrick leftBrick : leftBricks) {

		GroupMatch groupMatch = hashGroupID2GroupMatches.get(leftBrick
			.getDataContainer().getRecordGroup().getGroupID());
		SubGroupMatch subGroupMatch = groupMatch
			.getSubGroupMatch(rightBrick.getDataContainer()
				.getRecordGroup().getGroupID());

		float rightSimilarityRatioY = rightSimilarities[leftBrick
			.getDataContainer().getRecordGroup().getGroupID()];
		rightSimilarityOffsetY += rightSimilarityRatioY;

		subGroupMatch.setSimilarityRatioRight(rightSimilarityRatioY);

		subGroupMatch.setRightAnchorYStart(rightBrickElementLayout
			.getTranslateY()
			+ rightBrickElementLayout.getSizeScaledY()
			* (rightSimilarityOffsetY));

		subGroupMatch.setRightAnchorYEnd(rightBrickElementLayout
			.getTranslateY()
			+ rightBrickElementLayout.getSizeScaledY()
			* (rightSimilarityOffsetY - rightSimilarityRatioY));

	    }

	    // break;
	}
    }

    private void calculateSubMatchSelections(SubGroupMatch subGroupMatch,
	    RecordVirtualArray recordVA) {

	if (recordVA.size() == 0)
	    return;

	RecordSelectionManager recordSelectionManager = glVisBricks
		.getRecordSelectionManager();

	glVisBricks.getHashConnectionBandIDToRecordVA().put(
		subGroupMatch.getConnectionBandID(), recordVA);

	float ratio = 0;

	// Iterate over all selection types
	for (SelectionType selectionType : recordSelectionManager
		.getSelectionTypes()) {

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

		subGroupMatch
			.addSelectionTypeRatio(ratio, SelectionType.NORMAL);
		continue;
	    }

	    int intersectionCount = 0;
	    IDMappingManager mappingManager = IDMappingManagerRegistry.get()
		    .getIDMappingManager(recordVA.getIdType().getIDCategory());
	    for (Integer recordID : recordVA) {
		if (recordVA.getIdType() != recordSelectionManager.getIDType()) {
		    IDType destIDType = recordSelectionManager.getIDType();

		    recordID = mappingManager.getID(recordVA.getIdType(),
			    destIDType, recordID);

		}

		if (recordID != null
			&& selectedByGroupSelections.contains(recordID))
		    intersectionCount++;
	    }

	    ratio = (float) intersectionCount / recordVA.size();

	    subGroupMatch.addSelectionTypeRatio(ratio, selectionType);
	}
    }

    @Override
    public void render(GL2 gl) {

	renderBackground(gl);

	renderFlexibleArch(gl);
	renderDimensionGroupConnections(gl);
	renderDragAndDropMarker(gl);
    }

    private void renderBackground(GL2 gl) {

	int pickingID = glVisBricks.getPickingManager().getPickingID(
		glVisBricks.getID(), PickingType.DIMENSION_GROUP_SPACER.name(),
		ID);

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
		gl.glVertex3f(x / 2f, 0, 1f);
		gl.glVertex3f(x / 2f, y, 1f);
	    } else {
		gl.glVertex3f(0, y / 2f, 1f);
		gl.glVertex3f(x, y / 2f, 1f);
	    }
	    gl.glEnd();

	    System.out.println("spacer line");

	    renderDragAndDropSpacer = false;
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

	float curveOffset = x * 0.2f;

	float xStart = 0;
	float xEnd;

	// handle situation in center arch where to group is contained
	if (leftDimGroup == null && rightDimGroup == null
		&& glVisBricks != null) {

	    leftCenterBrickBottom = glVisBricks.getArchBottomY();
	    leftCenterBrickTop = glVisBricks.getArchTopY();

	    rightCenterBrickBottom = glVisBricks.getArchBottomY();
	    rightCenterBrickTop = glVisBricks.getArchTopY();
	}

	if (leftDimGroup != null) {
	    if (leftDimGroup.isDetailBrickShown()
		    && !leftDimGroup.isExpandLeft())
		return;

	    GLBrick leftCenterBrick = leftDimGroup.getCenterBrick();

	    ElementLayout layout = leftCenterBrick.getLayout();
	    leftCenterBrickBottom = layout.getTranslateY();
	    leftCenterBrickTop = layout.getTranslateY()
		    + layout.getSizeScaledY();

	    if (!leftDimGroup.isDetailBrickShown())
		xStart = leftDimGroup.getLayout().getTranslateX()
			- leftCenterBrick.getLayout().getTranslateX();

	    // Render straight band connection from center brick to dimension
	    // group on
	    // the LEFT
	    if (xStart != 0) {

		connectionRenderer.renderSingleBand(gl, new float[] { xStart,
			leftCenterBrickTop, 0 }, new float[] { xStart,
			leftCenterBrickBottom, 0 }, new float[] { 0,
			leftCenterBrickTop, 0 }, new float[] { 0,
			leftCenterBrickBottom, 0 }, false, curveOffset, 0,
			new float[] { 0f, 0f, 0f, 0.5f }, true);
	    }

	} else {
	    if (rightDimGroup != null) {
		leftCenterBrickBottom = glVisBricks.getArchBottomY();
		leftCenterBrickTop = glVisBricks.getArchTopY();
		curveOffset = 0.1f;
	    }
	}

	if (rightDimGroup != null) {
	    if (rightDimGroup.isDetailBrickShown()
		    && rightDimGroup.isExpandLeft())
		return;
	    GLBrick rightCenterBrick = rightDimGroup.getCenterBrick();

	    ElementLayout layout = rightCenterBrick.getLayout();
	    rightCenterBrickBottom = layout.getTranslateY();
	    rightCenterBrickTop = layout.getTranslateY()
		    + layout.getSizeScaledY();

	    if (!rightDimGroup.isDetailBrickShown())

		xEnd = x + rightCenterBrick.getLayout().getTranslateX()
			- rightDimGroup.getLayout().getTranslateX();
	    else
		xEnd = x + rightCenterBrick.getLayout().getTranslateX();

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
			rightCenterBrickBottom, 0 }, false, curveOffset, 0,
			new float[] { 0, 0, 0 }, 0.2f);
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
	connectionRenderer.renderSingleBand(gl, new float[] { 0,
		leftCenterBrickTop, 0 }, new float[] { 0,
		leftCenterBrickBottom, 0 }, new float[] { x,
		rightCenterBrickTop, 0 }, new float[] { x,
		rightCenterBrickBottom, 0 }, false, curveOffset, 0,
		GLVisBricks.ARCH_COLOR, true);
	// gl.glPopMatrix();
    }

    private void renderDimensionGroupConnections(GL2 gl) {

	if (relationAnalyzer == null || leftDimGroup == null
		|| rightDimGroup == null)
	    return;

	float splineFactor = 0.1f * x;

	gl.glLineWidth(1);
	for (GroupMatch groupMatch : hashGroupID2GroupMatches.values()) {

	    GLBrick brick = groupMatch.getBrick();
	    float xStart = 0;
	    if (!leftDimGroup.isDetailBrickShown())
		xStart = -(leftDimGroup.getLayout().getSizeScaledX() - brick
			.getLayout().getSizeScaledX()) / 2;

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
			glVisBricks.getID(),
			PickingType.BRICK_CONNECTION_BAND.name(),
			subGroupMatch.getConnectionBandID()));

		// Render selected portion
		for (SelectionType selectionType : hashRatioToSelectionType
			.keySet()) {

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

			float maxRatio = Math.max(
				subGroupMatch.getLeftSimilarityRatio(),
				subGroupMatch.getRightSimilarityRatio());
			if (maxRatio < 0.3f)
			    trendRatio = (glVisBricks
				    .getConnectionsFocusFactor() - maxRatio);
			else
			    trendRatio = 1 - (glVisBricks
				    .getConnectionsFocusFactor() + (1 - maxRatio));

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
					    - leftYDiffSelection, 0.0f },
			    new float[] { x,
				    subGroupMatch.getRightAnchorYTop(), 0.0f },
			    new float[] {
				    x,
				    subGroupMatch.getRightAnchorYTop()
					    - rightYDiffSelection, 0.0f },
			    true, splineFactor, 0, color);// 0.15f);

		    // Render straight band connection from brick to dimension
		    // group on the LEFT. This is for the smaller bricks when
		    // the bricks are not of equal size
		    if (xStart != 0) {
			connectionRenderer.renderStraightBand(
				gl,
				new float[] { xStart,
					subGroupMatch.getLeftAnchorYTop(), 0 },
				new float[] {
					xStart,
					subGroupMatch.getLeftAnchorYTop()
						- leftYDiffSelection, 0 },
				new float[] { 0,
					subGroupMatch.getLeftAnchorYTop(), 0 },
				new float[] {
					0,
					subGroupMatch.getLeftAnchorYTop()
						- leftYDiffSelection, 0 },
				false, splineFactor, 0, color, trendRatio);// 0.5f);
		    }

		    // Render straight band connection from brick to dimension
		    // group on the RIGHT. This is for the smaller bricks when
		    // the bricks are not of equal size
		    if (xEnd != 0) {

			connectionRenderer
				.renderStraightBand(
					gl,
					new float[] {
						x,
						subGroupMatch
							.getRightAnchorYTop(),
						0 },
					new float[] {
						x,
						subGroupMatch
							.getRightAnchorYTop()
							- rightYDiffSelection,
						0 },
					new float[] {
						xEnd,
						subGroupMatch
							.getRightAnchorYTop(),
						0 },
					new float[] {
						xEnd,
						subGroupMatch
							.getRightAnchorYTop()
							- rightYDiffSelection,
						0 }, false, splineFactor, 0,
					color, trendRatio);// 0.5f);
		    }
		    // gl.glPopMatrix();
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

	    glVisBricks.moveDimensionGroup(this, (DimensionGroup) draggable,
		    leftDimGroup);
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
