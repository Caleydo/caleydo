package org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.VAElementRepresentation;

import com.sun.opengl.util.j2d.TextRenderer;

public class VAElementDrawingStrategyDragged
	extends AVAElementDrawingStrategyRectangular {

	@Override
	public void draw(GL gl, VAElementRepresentation elementRepresentation, TextRenderer textRenderer) {
		// Use drawDragged Method
	}
	
	public void drawDragged(GL gl, VAElementRepresentation elementRepresentation, float fMouseCoordinateX,
		float fMouseCoordinateY, float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fHeight = elementRepresentation.getHeight();
		float fWidth = elementRepresentation.getWidth();

		Vec3f vecRealGroupPosition = elementRepresentation.getPosition();
		Vec3f vecScaledRealGroupPosition =
			getScaledPosition(gl, vecRealGroupPosition, elementRepresentation.getHierarchyPosition());

		float fRealRelDraggingPosX = vecScaledRealGroupPosition.x() - fDraggingStartMouseCoordinateX;
		float fRealRelDraggingPosY = vecScaledRealGroupPosition.y() - fDraggingStartMouseCoordinateY;

		Vec3f vecPosition =
			new Vec3f(fMouseCoordinateX + fRealRelDraggingPosX, fMouseCoordinateY + fRealRelDraggingPosY,
				0.1f);
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);

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
