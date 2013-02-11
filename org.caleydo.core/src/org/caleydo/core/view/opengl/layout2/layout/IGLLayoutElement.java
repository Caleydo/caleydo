package org.caleydo.core.view.opengl.layout2.layout;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * accessor for an element seen by a {@link IGLLayout}
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLLayoutElement {
	GLElement asElement();

	/**
	 * returns the layout data of the element if it is of the specific class otherwise return the default value
	 * 
	 * @param clazz
	 *            the instance of expected layout data
	 * @param default_
	 *            default value
	 * @return
	 */
	<T> T getLayoutDataAs(Class<T> clazz, T default_);

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
}