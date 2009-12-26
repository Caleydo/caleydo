package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public interface ICompositeGraphic {

	public void add(ICompositeGraphic graphic);

	public void delete(ICompositeGraphic graphic);

	public void draw(GL gl, TextRenderer textRenderer, Vec3f vecRelativeDrawingPosition);

	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer);

	public void calculateDimensions(GL gl, TextRenderer textRenderer);
	
	public void calculateHierarchyLevels(int iLevel);

	public Vec3f getPosition();

	public void setPosition(Vec3f vecPosition);

	public float getHeight();

	public float getWidth();

	public void setToMaxWidth(float fWidth);
	
	public void setDepth(float fDepth);

}
