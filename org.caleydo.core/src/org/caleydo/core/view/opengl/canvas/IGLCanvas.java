/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLCanvas {
	boolean isVisible();

	void addMouseListener(IGLMouseListener listener);

	void removeMouseListener(IGLMouseListener listener);

	void addFocusListener(IGLFocusListener listener);

	void removeFocusListener(IGLFocusListener listener);

	void requestFocus();

	void addKeyListener(IGLKeyListener listener);

	void removeKeyListener(IGLKeyListener listener);

	void addGLEventListener(GLEventListener listener);

	void removeGLEventListener(GLEventListener listener);

	/**
	 * @return the width of this canvas in DIP units
	 */
	float getDIPWidth();

	/**
	 * @return the height of this canvas in DIP units
	 */
	float getDIPHeight();

	/**
	 * @return the width of this canvas the desired unit
	 */
	float getWidth(Units unit);

	/**
	 * @return the height of this canvas the desired unit
	 */
	float getHeight(Units unit);

	/**
	 * converts the (scaled) dip to pixel used for OpenGL
	 *
	 * @param dip
	 * @return
	 */
	int toRawPixel(float dip);

	/**
	 * see {@link #toRawPixel(float)} for a {@link Rectangle2D}
	 *
	 * @return
	 */
	Rectangle toRawPixel(Rectangle2D.Float viewArea_dip);

	/**
	 * converts the given {@link Rectangle} in raw pixel into DIP units
	 *
	 * @param viewArea_raw
	 * @return
	 */
	Rectangle2D.Float toDIP(Rectangle viewArea_raw);

	/**
	 * @return a function that implements {@link #toRawPixel(float)}
	 */
	Function<Float, Float> toRawPixelFunction();

	GLAutoDrawable asGLAutoDrawAble();

	Composite asComposite();

	@Override
	public String toString();

	IPickingListener createTooltip(ILabeled label);

	IPickingListener createTooltip(IPickingLabelProvider label);

	IPickingListener createTooltip(String label);

	void showPopupMenu(final Iterable<? extends AContextMenuItem> items);
}
