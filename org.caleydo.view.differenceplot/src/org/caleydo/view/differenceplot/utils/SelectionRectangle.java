package org.caleydo.view.differenceplot.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author turkay
 *
 * This class represents a single selection rectangle on a differenceplot view
 */
public class SelectionRectangle {
	
	/**
	 * Minimum x-data value covered by the selection rectangle
	 */
	private float xMin;
	/**
	 * Maximum x-data value covered by the selection rectangle
	 */
	private float xMax;
	/**
	 * Minimum y-data value covered by the selection rectangle
	 */
	private float yMin;
	/**
	 * MAximum y-data value covered by the selection rectangle
	 */
	private float yMax;
	
	/**
	 * Screen coordinate of left X-value
	 */
	private float left;
	/**
	 * Screen coordinate of right X-Value
	 */
	private float right;
	/**
	 * Screen coordinate of top Y-Value
	 */
	private float top;
	/**
	 * Screen coordinate of bottom Y-value
	 */
	private float bottom;
	
	
	public SelectionRectangle(float xMin, float xMax, float yMin, float yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public SelectionRectangle() {
		this.xMin = -1;
		this.xMax = -1;
		this.yMin = -1;
		this.yMax = -1;
	}
	
	
	public void ComputeScreenToDataMapping(DifferenceplotRenderUtils renderUtil, ArrayList<ArrayList<Float>> dataColumns, float width, float height)
	{
		float xScreenMin = Math.min(this.left, this.right);
		float xScreenMax = Math.max(this.left, this.right);
		
		//y axis on the screen is inversed
		float yScreenMin = Math.max(this.top, this.bottom);
		float yScreenMax = Math.min(this.top, this.bottom);
		
		Point2D.Float minData = renderUtil.findScreenToDataMapping(new Point2D.Float(xScreenMin, yScreenMin), dataColumns, width, height);
		this.xMin = minData.x;
		this.yMin = minData.y;
		
		Point2D.Float maxData = renderUtil.findScreenToDataMapping(new Point2D.Float(xScreenMax, yScreenMax), dataColumns, width, height);
		this.xMax = maxData.x;
		this.yMax = maxData.y;
	}
	
	public void moveRectangle(float dx, float dy)
	{
		this.left += dx;
		this.right += dx;
		this.top += dy;
		this.bottom += dy;
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

	/**
	 * @return the left
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(float left) {
		this.left = left;
	}

	/**
	 * @return the right
	 */
	public float getRight() {
		return right;
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(float right) {
		this.right = right;
	}

	/**
	 * @return the top
	 */
	public float getTop() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	public void setTop(float top) {
		this.top = top;
	}

	/**
	 * @return the bottom
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	
	


	
	
}
