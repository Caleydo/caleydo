package org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.grouper.GrouperRenderStyle;
import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.GroupRepresentation;

import com.sun.opengl.util.j2d.TextRenderer;

public class GroupDrawingStrategyDragged
	extends AGroupDrawingStrategyRectangular {

	private GrouperRenderStyle renderStyle;

	public GroupDrawingStrategyDragged(GrouperRenderStyle renderStyle) {
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer) {

		// Use drawDragged Method

	}

	public void drawDragged(GL gl, GroupRepresentation groupRepresentation, float fMouseCoordinateX,
		float fMouseCoordinateY, float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fGroupColor[] = renderStyle.getGroupColorForLevel(groupRepresentation.getHierarchyLevel());
		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		Vec3f vecRealGroupPosition = groupRepresentation.getPosition();
		Vec3f vecScaledRealGroupPosition =
			getScaledPosition(gl, vecRealGroupPosition, groupRepresentation.getHierarchyPosition());

		float fRealRelDraggingPosX = vecScaledRealGroupPosition.x() - fDraggingStartMouseCoordinateX;
		float fRealRelDraggingPosY = vecScaledRealGroupPosition.y() - fDraggingStartMouseCoordinateY;

		Vec3f vecPosition =
			new Vec3f(fMouseCoordinateX + fRealRelDraggingPosX, fMouseCoordinateY + fRealRelDraggingPosY,
				0.2f);
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glColor4f(fGroupColor[0], fGroupColor[1], fGroupColor[2], 0.5f);

		beginGUIElement(gl, vecPosition);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);

		gl.glPopAttrib();

	}

}
