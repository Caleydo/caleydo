package org.caleydo.view.bookmarking;

/**
 * The dimensions class holds the geometric properties of an object, such as
 * point of origin and widht and heigt. xOrigin and yOrigin are considered to be
 * in the top left corner, since elements using this are typically rendered from
 * top to bottom.
 * 
 * @author Alexander Lex
 */
public class Dimensions {

	private float xOrigin = -1;
	private float yOrigin = -1;
	private float width;
	private float height;

	public void setOrigins(float xOrigin, float yOrigin) {
		this.xOrigin = xOrigin;
		this.yOrigin = yOrigin;
	}

	/**
	 * xOrigin is at the left
	 * 
	 * @return
	 */
	public float getXOrigin() {
		return xOrigin;
	}

	/**
	 * xOrigin is at the left
	 */
	public void setXOrigin(float xOrigin) {
		this.xOrigin = xOrigin;
	}

	/**
	 * yOrigin is at the top
	 * 
	 * @return
	 */
	public float getYOrigin() {
		return yOrigin;
	}

	/**
	 * yOrigin is at the top
	 */
	public void setYOrigin(float yOrigin) {
		this.yOrigin = yOrigin;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void increaseHeight(float delta) {
		this.height += delta;
	}

	@Override
	public String toString() {
		return "xOrigin: " + xOrigin + ", yOrigin: " + yOrigin + ", widht: " + width
				+ ", height: " + height;
	}

}
