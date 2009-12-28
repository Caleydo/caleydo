package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.util.AGLGUIElement;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class AGroupDrawingStrategyRectangular
	extends AGLGUIElement
	implements IGroupDrawingStrategy {

	public AGroupDrawingStrategyRectangular() {
		setMinSize(GrouperRenderStyle.GUI_ELEMENT_MIN_SIZE);
	}

	@Override
	public abstract void draw(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer);

	@Override
	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer,
		GroupRepresentation groupRepresentation) {

		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();

		calculateDimensions(gl, textRenderer, groupRepresentation);

		float fMaxChildWidth = 0.0f;

		for (ICompositeGraphic child : alChildren) {
			if (child.getWidth() > fMaxChildWidth)
				fMaxChildWidth = child.getWidth();
		}

		for (ICompositeGraphic child : alChildren) {
			child.setToMaxWidth(fMaxChildWidth, GrouperRenderStyle.ELEMENT_LEFT_SPACING);
		}

	}

	@Override
	public void calculateDimensions(GL gl, TextRenderer textRenderer, GroupRepresentation groupRepresentation) {

		float fMaxChildWidth = 0.0f;
		float fHeight = 0.0f;
		float fWidth = 0.0f;

		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();

		alDropPositions.clear();
		alDropPositions.add(GrouperRenderStyle.ELEMENT_TOP_SPACING / 2.0f);

		for (int i = 0; i < alChildren.size(); i++) {

			ICompositeGraphic child = alChildren.get(i);

			fHeight += GrouperRenderStyle.ELEMENT_TOP_SPACING;

			child.calculateDimensions(gl, textRenderer);

			float fCurrentChildWidth = child.getWidth();

			if (fCurrentChildWidth > fMaxChildWidth)
				fMaxChildWidth = fCurrentChildWidth;

			fHeight += child.getHeight() + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;

			if (i == alChildren.size() - 1)
				alDropPositions.add(fHeight - GrouperRenderStyle.ELEMENT_BOTTOM_SPACING / 2.0f);
			else
				alDropPositions.add(fHeight);
		}

		fWidth = fMaxChildWidth + GrouperRenderStyle.ELEMENT_LEFT_SPACING;

		groupRepresentation.setHeight(fHeight);
		groupRepresentation.setWidth(fWidth);
	}

	protected void drawGroupRectangular(GL gl, GroupRepresentation groupRepresentation,
		TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);
	}

	protected void drawRectangularBorder(GL gl, GroupRepresentation groupRepresentation,
		TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);
	}

	protected void drawChildren(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		float fCurrentChildYPosition = vecPosition.y();

		for (int i = 0; i < alChildren.size(); i++) {

			ICompositeGraphic child = alChildren.get(i);

			fCurrentChildYPosition -= GrouperRenderStyle.ELEMENT_TOP_SPACING;
			child.setPosition(new Vec3f(vecPosition.x() + GrouperRenderStyle.ELEMENT_LEFT_SPACING,
				fCurrentChildYPosition, vecPosition.z() + 0.01f));
			child.draw(gl, textRenderer);

			fCurrentChildYPosition -= child.getHeight() + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;

		}
	}

	public int getClosestDropPositionIndex(GL gl, GroupRepresentation groupRepresentation,
		Set<IDraggable> alDraggables, float fMouseCoordinateY) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();
		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		float fMinDistanceFromDropPosition = Float.MAX_VALUE;
		int iDropPositionIndex = -1;

		for (int i = 0; i < alDropPositions.size(); i++) {
			Float fDropPosition =
				getScaledCoordinate(gl, vecPosition.y() - alDropPositions.get(i), groupRepresentation
					.getHierarchyPosition().y());
			float fCurrentDistanceFromDropPosition = Math.abs(fMouseCoordinateY - fDropPosition);

			if (fCurrentDistanceFromDropPosition < fMinDistanceFromDropPosition) {
				fMinDistanceFromDropPosition = fCurrentDistanceFromDropPosition;
				iDropPositionIndex = i;
			}
		}

		ICompositeGraphic childNearDropPositionLower = null;
		ICompositeGraphic childNearDropPositionUpper = null;

		if (iDropPositionIndex == alDropPositions.size() - 1) {
			childNearDropPositionLower = alChildren.get(iDropPositionIndex - 1);
		}
		else {
			childNearDropPositionLower = alChildren.get(iDropPositionIndex);
		}
		if (iDropPositionIndex == 0) {
			childNearDropPositionUpper = alChildren.get(iDropPositionIndex);
		}
		else {
			childNearDropPositionUpper = alChildren.get(iDropPositionIndex - 1);
		}

		for (IDraggable draggable : alDraggables) {
			if (draggable == childNearDropPositionLower || draggable == childNearDropPositionUpper)
				return -1;
		}

		return iDropPositionIndex;
	}

	public void drawDropPositionMarker(GL gl, GroupRepresentation groupRepresentation, int iDropPositionIndex) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glPushAttrib(GL.GL_LINE_BIT);

		gl.glLineWidth(2.0f);

		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - alDropPositions.get(iDropPositionIndex), 0.0f);
		gl.glVertex3f(vecPosition.x() + groupRepresentation.getWidth(), vecPosition.y()
			- alDropPositions.get(iDropPositionIndex), 0.0f);
		gl.glEnd();

		gl.glPopAttrib();

		endGUIElement(gl);
	}
}
