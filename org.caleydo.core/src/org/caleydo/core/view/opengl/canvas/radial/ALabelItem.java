package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;


public abstract class ALabelItem {
	
	protected float fHeight;
	protected float fWidth;
	protected Vec2f vecPosition;
	
	public ALabelItem() {
		vecPosition = new Vec2f(0,0);
	}

	public abstract void draw(GL gl);

	public float getHeight() {
		return fHeight;
	}

	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	public float getWidth() {
		return fWidth;
	}

	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}
	
	public void setPosition(float fXPosition, float fYPosition) {
		vecPosition.set(fXPosition, fYPosition);
	}
}
