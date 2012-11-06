/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabelProvider;
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

	IPickingListener createTooltip(ILabelProvider label);

	IPickingListener createTooltip(String label);

}
