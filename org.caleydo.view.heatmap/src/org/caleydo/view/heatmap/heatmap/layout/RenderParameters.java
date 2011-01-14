package org.caleydo.view.heatmap.heatmap.layout;

import org.caleydo.view.heatmap.heatmap.renderer.ARenderer;

/**
 * Size parameters for a single element
 * 
 * @author Alexander Lex
 * 
 */
public class RenderParameters {

	ARenderer renderer;

	boolean scaleX = true;
	boolean scaleY = true;

	/**
	 * if true the element is rendered as background, i.e. that the size of it
	 * is not included in the transform
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

	public ARenderer getRenderer() {
		return renderer;
	}

	/**
	 * Set the x size of the element. The size has to be normalized between 0
	 * and 1, where 1 is the whole space available for the rendered elements
	 * 
	 * @param sizeX
	 */
	public void setSizeX(float sizeX) {
		this.sizeX = sizeX;
	}

	public float getSizeX() {
		return sizeX;
	}

	/**
	 * Set the y size of the element. The size has to be normalized between 0
	 * and 1, where 1 is the whole space available for the rendered elements
	 * 
	 * @param sizeY
	 */
	public void setSizeY(float sizeY) {
		this.sizeY = sizeY;
	}

	public float getSizeY() {
		return sizeY;
	}

	/**
	 * Get the scaled size of X (i.e. not normalized to 0-1)
	 * 
	 * @return
	 */
	public float getSizeScaledX() {
		return sizeScaledX;
	}

	/**
	 * Get the scaled size of Y (i.e. not normalized to 0-1)
	 * 
	 * @return
	 */
	public float getSizeScaledY() {
		return sizeScaledY;
	}

	/**
	 * Instruct the element to grab the remaining space in the x direction
	 */
	public void setGrabX(boolean grabX) {
		this.grabX = grabX;
	}

	/**
	 * Instruct the element to grab the remaining space in the y direction
	 */
	public void setGrabY(boolean grabY) {
		this.grabY = grabY;
	}

	/**
	 * Set whether the element should be rendered in the background. If true,
	 * it's size is not taken into account when calculating layouts. Default is
	 * false.
	 * 
	 * @param isBackground
	 */
	public void setIsBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

	public void setTransformX(float transformX) {
		this.transformX = transformX;
	}

	public void setTransformY(float transformY) {
		this.transformY = transformY;
	}

	public float getTransformScaledX() {
		return transformScaledX;
	}

	public float getTransformScaledY() {
		return transformScaledY;
	}

	public float getTransformX() {
		return transformX;
	}

	public float getTransformY() {
		return transformY;
	}

	/**
	 * Set whether the values set should be scaled according to the available
	 * window, or whether they should be of static size. Default is true.
	 * 
	 * @param scaleX
	 */
	public void setScaleX(boolean scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * Set whether the values set should be scaled according to the available
	 * window, or whether they should be of static size. Default is true.
	 * 
	 * @param scaleY
	 */

	public void setScaleY(boolean scaleY) {
		this.scaleY = scaleY;
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
