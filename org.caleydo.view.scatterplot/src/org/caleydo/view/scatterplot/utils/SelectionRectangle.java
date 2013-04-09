package org.caleydo.view.scatterplot.utils;

/**
 * @author turkay
 *
 * This class represents a single selection rectangle on a scatterplot view
 */
public class SelectionRectangle {
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;
	
	
	public SelectionRectangle(float xMin, float xMax, float yMin, float yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public SelectionRectangle() {
		this.xMin = 0;
		this.xMax = 0;
		this.yMin = 0;
		this.yMax = 0;
	}
	
	/**
	 * @return the xMin
	 */
	public float getxMin() {
		return xMin;
	}
	/**
	 * @param xMin the xMin to set
	 */
	public void setxMin(float xMin) {
		this.xMin = xMin;
	}
	/**
	 * @return the xMax
	 */
	public float getxMax() {
		return xMax;
	}
	/**
	 * @param xMax the xMax to set
	 */
	public void setxMax(float xMax) {
		this.xMax = xMax;
	}
	/**
	 * @return the yMin
	 */
	public float getyMin() {
		return yMin;
	}
	/**
	 * @param yMin the yMin to set
	 */
	public void setyMin(float yMin) {
		this.yMin = yMin;
	}
	/**
	 * @return the yMax
	 */
	public float getyMax() {
		return yMax;
	}
	/**
	 * @param yMax the yMax to set
	 */
	public void setyMax(float yMax) {
		this.yMax = yMax;
	}
	
	
}
