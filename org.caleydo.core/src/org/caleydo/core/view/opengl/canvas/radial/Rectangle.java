package org.caleydo.core.view.opengl.canvas.radial;

public class Rectangle {
	
	private float fMinX;
	private float fMinY;
	private float fMaxX;
	private float fMaxY;
	
	public Rectangle(float fMinX, float fMinY, float fMaxX, float fMaxY) {
		this.fMinX = fMinX;
		this.fMinY = fMinY;
		this.fMaxX = fMaxX;
		this.fMaxY = fMaxY;
	}
	
	
	public float getMinX() {
		return fMinX;
	}
	public void setMinX(float fMinX) {
		this.fMinX = fMinX;
	}
	public float getMinY() {
		return fMinY;
	}
	public void setMinY(float fMinY) {
		this.fMinY = fMinY;
	}
	public float getMaxX() {
		return fMaxX;
	}
	public void setMaxX(float fMaxX) {
		this.fMaxX = fMaxX;
	}
	public float getMaxY() {
		return fMaxY;
	}
	public void setMaxY(float fMaxY) {
		this.fMaxY = fMaxY;
	}

	
}
