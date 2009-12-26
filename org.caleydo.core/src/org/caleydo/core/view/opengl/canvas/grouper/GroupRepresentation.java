package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.util.AGLGUIElement;

import com.sun.opengl.util.j2d.TextRenderer;

public class GroupRepresentation
	extends AGLGUIElement
	implements ICompositeGraphic, IDraggable, IDropArea {

	private ArrayList<ICompositeGraphic> alChildren;
	private ArrayList<Float> alDropPositions;
	private Vec3f vecPosition;
	private float fHeight;
	private float fWidth;
	private float fRelDraggingPosX;
	private float fRelDraggingPosY;
	private int iViewID;
	private int iHierarchyLevel;
	private boolean bCollapsed;
	private PickingManager pickingManager;
	private ClusterNode clusterNode;
	private GrouperRenderStyle renderStyle;

	public GroupRepresentation(int iViewID, PickingManager pickingManager, ClusterNode clusterNode,
		GrouperRenderStyle renderStyle) {
		alChildren = new ArrayList<ICompositeGraphic>();
		alDropPositions = new ArrayList<Float>();
		setMinSize(GrouperRenderStyle.GUI_ELEMENT_MIN_SIZE);
		vecPosition = new Vec3f();
		bCollapsed = false;
		this.iViewID = iViewID;
		this.pickingManager = pickingManager;
		this.clusterNode = clusterNode;
		this.renderStyle = renderStyle;
	}

	@Override
	public void add(ICompositeGraphic graphic) {
		alChildren.add(graphic);

	}

	@Override
	public void delete(ICompositeGraphic graphic) {
		alChildren.remove(graphic);

	}

	@Override
	public void draw(GL gl, TextRenderer textRenderer, Vec3f vecRelativeDrawingPosition) {
		beginGUIElement(gl, vecRelativeDrawingPosition);

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GROUPER_GROUP_SELECTION, clusterNode
			.getClusterNr()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glColor4fv(renderStyle.getGroupColorForLevel(iHierarchyLevel), 0);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		gl.glPopAttrib();

		gl.glPopName();

		endGUIElement(gl);

		float fCurrentChildYPosition = vecPosition.y();
		alDropPositions.clear();
		alDropPositions.add(vecPosition.y() - GrouperRenderStyle.ELEMENT_TOP_SPACING / 2.0f);

		for (int i = 0; i < alChildren.size(); i++) {

			ICompositeGraphic child = alChildren.get(i);

			fCurrentChildYPosition -= GrouperRenderStyle.ELEMENT_TOP_SPACING;
			child.setPosition(new Vec3f(vecPosition.x() + GrouperRenderStyle.ELEMENT_LEFT_SPACING,
				fCurrentChildYPosition, vecPosition.z() + 0.01f));
			child.draw(gl, textRenderer, vecRelativeDrawingPosition);

			fCurrentChildYPosition -= child.getHeight() + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;

			if (i == alChildren.size() - 1)
				alDropPositions
					.add(fCurrentChildYPosition + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING / 2.0f);
			else
				alDropPositions.add(fCurrentChildYPosition);
		}
	}

	@Override
	public void handleDragging(GL gl, float fMouseCoordinateX, float fMouseCoordinateY) {

		float fGroupColor[] = renderStyle.getGroupColorForLevel(iHierarchyLevel);

		gl.glColor4f(fGroupColor[0], fGroupColor[1], fGroupColor[2], 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX, fMouseCoordinateY + fRelDraggingPosY, 0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX + fWidth, fMouseCoordinateY + fRelDraggingPosY,
			0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX + fWidth, fMouseCoordinateY + fRelDraggingPosY
			- fHeight, 0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX, fMouseCoordinateY + fRelDraggingPosY - fHeight,
			0.1f);
		gl.glEnd();

	}

	@Override
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY) {
		fRelDraggingPosX = vecPosition.x() - fMouseCoordinateX;
		fRelDraggingPosY = vecPosition.y() - fMouseCoordinateY;
	}

	@Override
	public void handleDragOver(GL gl, ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {

		for (IDraggable draggable : alDraggables) {
			if (draggable == this)
				return;
		}

		float fMinDistanceFromDropPosition = Float.MAX_VALUE;
		int iDropPositionIndex = 0;

		for (int i = 0; i < alDropPositions.size(); i++) {
			Float fDropPosition = alDropPositions.get(i);
			float fCurrentDistanceFromDropPosition = Math.abs(fMouseCoordinateY - fDropPosition);
			if (fCurrentDistanceFromDropPosition < fMinDistanceFromDropPosition) {
				fMinDistanceFromDropPosition = fCurrentDistanceFromDropPosition;
				iDropPositionIndex = i;
			}
		}
		
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), alDropPositions.get(iDropPositionIndex), 0.0f);
		gl.glVertex3f(vecPosition.x() + fWidth, alDropPositions.get(iDropPositionIndex), 0.0f);
		gl.glVertex3f(vecPosition.x() + fWidth, alDropPositions.get(iDropPositionIndex) + 0.01f, 0.0f);
		gl.glVertex3f(vecPosition.x(), alDropPositions.get(iDropPositionIndex) + 0.01f, 0.0f);
		gl.glEnd();
	}

	@Override
	public void handleDrop(ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vec3f getPosition() {
		return vecPosition;
	}

	@Override
	public void setPosition(Vec3f vecPosition) {
		this.vecPosition = vecPosition;
	}

	@Override
	public float getHeight() {
		return fHeight;
	}

	@Override
	public float getWidth() {
		return fWidth;
	}

	@Override
	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer) {
		calculateDimensions(gl, textRenderer);

		float fMaxChildWidth = 0.0f;

		for (ICompositeGraphic child : alChildren) {
			if (child.getWidth() > fMaxChildWidth)
				fMaxChildWidth = child.getWidth();
		}

		for (ICompositeGraphic child : alChildren) {
			child.setToMaxWidth(fMaxChildWidth);
			child.setDepth(vecPosition.z());
		}

	}

	@Override
	public void calculateDimensions(GL gl, TextRenderer textRenderer) {

		float fMaxChildWidth = 0.0f;
		fHeight = 0.0f;

		for (int i = 0; i < alChildren.size(); i++) {

			ICompositeGraphic child = alChildren.get(i);

			fHeight += GrouperRenderStyle.ELEMENT_TOP_SPACING;

			child.calculateDimensions(gl, textRenderer);

			float fCurrentChildWidth = child.getWidth();

			if (fCurrentChildWidth > fMaxChildWidth)
				fMaxChildWidth = fCurrentChildWidth;

			fHeight += child.getHeight() + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;
		}

		fWidth = fMaxChildWidth + GrouperRenderStyle.ELEMENT_LEFT_SPACING;

	}

	@Override
	public void setToMaxWidth(float fWidth) {

		this.fWidth = fWidth;
		for (ICompositeGraphic child : alChildren) {
			child.setToMaxWidth(fWidth - GrouperRenderStyle.ELEMENT_LEFT_SPACING);
		}

	}

	public boolean isCollapsed() {
		return bCollapsed;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.bCollapsed = bCollapsed;
	}

	@Override
	public void calculateHierarchyLevels(int iLevel) {
		iHierarchyLevel = iLevel;

		for (ICompositeGraphic child : alChildren) {
			child.calculateHierarchyLevels(iLevel + 1);
		}
	}

	@Override
	public void setDepth(float fDepth) {
		vecPosition.setZ(fDepth + 0.01f);
		for (ICompositeGraphic child : alChildren) {
			child.setDepth(vecPosition.z());
		}
	}

}
