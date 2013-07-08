/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Represents a rectangle that can be drawn in a label using a specific color.
 * 
 * @author Christian Partl
 */
public class RectangleItem extends ALabelItem {

	private float[] fArColor;
	private float fWidthToHeightRatio;
	private boolean bRelativeSize;

	/**
	 * Constructor.
	 * 
	 * @param fArColor
	 *            RGB-Color which shall be used to draw the rectangle. Only the
	 *            first three values of the array will be used.
	 * @param fWidth
	 *            Width of the rectangle.
	 * @param fHeight
	 *            Height of the rectangle.
	 * @param bRelativeSize
	 *            Determines whether changes to the width affects the height
	 *            (and vice versa) accordingly to their relative size or not.
	 */
	public RectangleItem(float[] fArColor, float fWidth, float fHeight,
			boolean bRelativeSize) {
		if (fArColor.length >= 3) {
			this.fArColor = fArColor;
		} else {
			fArColor = new float[3];
		}
		this.fWidth = fWidth;
		this.fHeight = fHeight;
		this.bRelativeSize = bRelativeSize;
		this.fWidthToHeightRatio = fWidth / fHeight;
	}

	@Override
	public void draw(GL2 gl) {

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glColor3fv(fArColor, 0);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - 0.01f, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - 0.01f, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fHeight - 0.01f, 0);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fHeight - 0.01f, 0);
		gl.glEnd();

		gl.glPopAttrib();

	}

	/**
	 * Sets the color the rectangle shall be drawn with.
	 * 
	 * @param fArColor
	 *            RGB-Color which shall be used to draw the rectangle. Only the
	 *            first three values of the array will be used.
	 */
	public void setColor(float[] fArColor) {
		if (fArColor.length >= 3) {
			this.fArColor = fArColor;
		}
	}

	/**
	 * Sets whether future changes to the width affects the height (and vice
	 * versa) accordingly to their relative size or not.
	 * 
	 * @param bRelativeSize
	 *            Specifies, if relative size shall be used.
	 */
	public void setRelativeSize(boolean bRelativeSize) {
		this.bRelativeSize = bRelativeSize;
	}

	@Override
	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
		if (bRelativeSize) {
			fWidth = fHeight * fWidthToHeightRatio;
		}
	}

	@Override
	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
		if (bRelativeSize) {
			fHeight = fWidth / fWidthToHeightRatio;
		}
	}

	@Override
	public int getLabelItemType() {
		return LabelItemTypes.LABEL_ITEM_TYPE_RECTANGLE;
	}

}
