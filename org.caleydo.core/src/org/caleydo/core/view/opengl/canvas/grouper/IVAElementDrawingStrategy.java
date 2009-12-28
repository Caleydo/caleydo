package org.caleydo.core.view.opengl.canvas.grouper;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public interface IVAElementDrawingStrategy {
	public void draw(GL gl, VAElementRepresentation elementRepresentation, TextRenderer textRenderer);
}
