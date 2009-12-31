package org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.GroupRepresentation;

import com.sun.opengl.util.j2d.TextRenderer;

public interface IGroupDrawingStrategy {
	public void draw(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer);
	
	public void drawAsLeaf(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer);

	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer,
		GroupRepresentation groupRepresentation);

	public void calculateDimensions(GL gl, TextRenderer textRenderer, GroupRepresentation groupRepresentation);
	
	public void calculateDimensionsOfLeaf(GL gl, TextRenderer textRenderer, GroupRepresentation groupRepresentation);
}
