package org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.VAElementRepresentation;

import com.sun.opengl.util.j2d.TextRenderer;

public interface IVAElementDrawingStrategy {

	public void draw(GL gl, VAElementRepresentation elementRepresentation, TextRenderer textRenderer);

	public void calculateDimensions(GL gl, VAElementRepresentation elementRepresentation,
		TextRenderer textRenderer);
}
