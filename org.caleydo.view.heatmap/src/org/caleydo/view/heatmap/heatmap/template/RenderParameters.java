package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;

public class RenderParameters {

	ARenderer renderer;

	boolean scaleX = true;
	boolean scaleY = true;

	/**
	 * if true the element is rendered as bacground, i.e. that the size of it is
	 * not included in the transform
	 */
	boolean isBackground = false;

	float transformX = 0;
	float transformY = 0;
	float transformScaledX = 0;
	float transformScaledY = 0;

	/** use the remaining space in X, invalidates sizeX */
	boolean grabX = false;
	/** use the remaining space in Y */
	boolean grabY = false;
	float sizeX = 0;
	float sizeY = 0;

	float sizeScaledX = 0;
	float sizeScaledY = 0;

	public void setRenderer(ARenderer renderer) {
		this.renderer = renderer;
	}
	
	public void setSizeX(float sizeX) {
		this.sizeX = sizeX;
	}

	public float getSizeX() {
		return sizeX;
	}

	public void setSizeY(float sizeY) {
		this.sizeY = sizeY;
	}

	public float getSizeY() {
		return sizeY;
	}

	void calculateScales(float totalWidth, float totalHeight) {
		if (scaleX) {
			sizeScaledX = sizeX * totalWidth;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;
		} else {
			sizeScaledX = sizeX;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;

		}
		if (scaleY)
			sizeScaledY = sizeY * totalHeight;
		else
			sizeScaledY = sizeY;
	}

}
