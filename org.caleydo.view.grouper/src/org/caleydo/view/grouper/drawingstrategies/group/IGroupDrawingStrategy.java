package org.caleydo.view.grouper.drawingstrategies.group;

import javax.media.opengl.GL2;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import com.jogamp.opengl.util.awt.TextRenderer;

public interface IGroupDrawingStrategy {
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer);

	public void drawAsLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer);

	public void calculateDrawingParameters(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation);

	public void calculateDimensions(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation);

	public void calculateDimensionsOfLeaf(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation);
}
