/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLCanvas {
	void addMouseListener(IGLMouseListener listener);

	void removeMouseListener(IGLMouseListener listener);

	void addFocusListener(IGLFocusListener listener);

	void removeFocusListener(IGLFocusListener listener);

	void requestFocus();

	void addKeyListener(IGLKeyListener listener);

	void removeKeyListener(IGLKeyListener listener);

	void addGLEventListener(GLEventListener listener);

	void removeGLEventListener(GLEventListener listener);

	int getWidth();

	int getHeight();

	GLAutoDrawable asGLAutoDrawAble();

	Composite asComposite();

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();

	IPickingListener createTooltip(ILabeled label);

	IPickingListener createTooltip(IPickingLabelProvider label);

	IPickingListener createTooltip(String label);

	void showPopupMenu(final Iterable<? extends AContextMenuItem> items);
}
