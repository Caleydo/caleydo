/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * accessor for an element seen by a {@link IGLLayout}
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLLayoutElement extends IHasGLLayoutData {
	GLElement asElement();

	/**
	 * sets the computed layouted position of this element
	 *
	 * @param x
	 * @param y
	 */
	void setLocation(float x, float y);

	/**
	 * sets the computed layouted size of this element
	 *
	 * @param w
	 * @param h
	 */
	void setSize(float w, float h);

	/**
	 * shortcut for {@link #setLocation(float, float)} and {@link #setSize(float, float)}
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	void setBounds(float x, float y, float w, float h);

	/**
	 * see {@link #setBounds(float, float, float, float)} with a {@link Vec4f}
	 *
	 * @param xywh
	 */
	void setBounds(Vec4f xywh);

	/**
	 * returns the layouted location
	 *
	 * @return
	 */
	Vec2f getLocation();

	/**
	 * returns the current layouted width of the element
	 *
	 * @return
	 */
	float getWidth();

	/**
	 * returns the current layouted height of the element
	 *
	 * @return
	 */
	float getHeight();

	/**
	 * returns the layouted bounds
	 *
	 * @return
	 */
	Vec4f getBounds();

	/**
	 * @return
	 */
	Rect getRectBounds();

	/**
	 * returns the set width of the element, i.e the width that was directly set by the element and not by the layout
	 * 
	 * @return
	 */
	float getSetWidth();

	/**
	 * returns the set height of the element, i.e the height that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	float getSetHeight();

	/**
	 * returns the set x position of the element, i.e the x that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	float getSetX();

	/**
	 * returns the set y position of the element, i.e the y that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	float getSetY();

	/**
	 * returns the set location of the element, i.e the x,y that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	Vec2f getSetLocation();

	/**
	 * returns the set location of the element, i.e the size that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	Vec2f getSetSize();

	/**
	 * returns the set bounds of the element, i.e the x,y,w,h that was directly set by the element and not by the layout
	 *
	 * @return
	 */
	Vec4f getSetBounds();

	/**
	 * hides the element from a layout view
	 */
	void hide();
}
