package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.util.AGLGUIElement;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class AVAElementDrawingStrategyRectangular
	extends AGLGUIElement
	implements IVAElementDrawingStrategy {
	
	public AVAElementDrawingStrategyRectangular() {
		setMinSize(GrouperRenderStyle.GUI_ELEMENT_MIN_SIZE);
	}

	@Override
	public abstract void draw(GL gl, VAElementRepresentation elementRepresentation, TextRenderer textRenderer);
	
	
	protected void drawElementRectangular(GL gl, VAElementRepresentation elementRepresentation, TextRenderer textRenderer) {

		Vec3f vecPosition = elementRepresentation.getPosition();
		float fHeight = elementRepresentation.getHeight();
		float fWidth = elementRepresentation.getWidth();

		beginGUIElement(gl, elementRepresentation.getHierarchyPosition());

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		float[] text_color = GrouperRenderStyle.TEXT_COLOR;
		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);

		textRenderer.begin3DRendering();

		textRenderer.draw3D(elementRepresentation.getName(), vecPosition.x()
			+ GrouperRenderStyle.TEXT_SPACING, vecPosition.y() - fHeight + GrouperRenderStyle.TEXT_SPACING,
			vecPosition.z(), GrouperRenderStyle.TEXT_SCALING);
		textRenderer.flush();

		textRenderer.end3DRendering();

		endGUIElement(gl);

	}

	protected void drawRectangularBorder(GL gl, VAElementRepresentation elementRepresentation,
		TextRenderer textRenderer) {

		Vec3f vecPosition = elementRepresentation.getPosition();
		float fHeight = elementRepresentation.getHeight();
		float fWidth = elementRepresentation.getWidth();

		beginGUIElement(gl, elementRepresentation.getHierarchyPosition());

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);
	}
}
