package org.caleydo.core.view.opengl.canvas.grouper;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public interface IGroupDrawingStrategy {
	public void draw(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer);

	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer,
		GroupRepresentation groupRepresentation);

	public void calculateDimensions(GL gl, TextRenderer textRenderer, GroupRepresentation groupRepresentation);
}
