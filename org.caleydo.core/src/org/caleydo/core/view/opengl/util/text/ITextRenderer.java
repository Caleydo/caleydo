/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.text;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;

/**
 * basic interface for a class than can render text
 *
 * @author Samuel Gratzl
 *
 */
public interface ITextRenderer {
	/**
	 * set the text color to the specified one
	 *
	 * @param color
	 */
	public void setColor(Color color);

	/**
	 * returns the size of the given text using the given height
	 *
	 * @param text
	 * @param height
	 * @return
	 */
	public float getTextWidth(String text, float height);

	/**
	 * Render the text at the position specified (lower left corner) within the bounding box The height is scaled to
	 * fit, the string is truncated to fit the width
	 *
	 * @param gl
	 * @param text
	 * @param x
	 *            x of lower left corner
	 * @param y
	 *            y of lower left corner
	 * @param z
	 * @param w
	 *            width of the bounding box
	 * @param h
	 *            height of the bounding box
	 */
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h);

	/**
	 * Same as {@link #renderTextInBounds(GL2, String, float, float, float, float, float)} but can align right or left.
	 *
	 * @param gl
	 * @param text
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @param h
	 * @param alignRight
	 *            if true the text is aligned to start at x, if false, the text is aligned to end at x
	 */
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h, boolean alignRight);

	/**
	 * describes the rendering information of this teture renderer
	 *
	 * @return
	 */
	public boolean isOriginTopLeft();

	/**
	 * whether since the previous call the texture was updated
	 *
	 * @return
	 */
	public boolean isDirty();
}
