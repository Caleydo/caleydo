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
package org.caleydo.core.view.opengl.canvas.internal.newt;


import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;

/**
 * @author Samuel Gratzl
 *
 */
final class NEWTGLCanvas implements IGLCanvas {
	private final NewtCanvasSWT composite;
	private final GLWindow canvas;

	NEWTGLCanvas(GLWindow window, NewtCanvasSWT canvas) {
		this.canvas = window;
		this.composite = canvas;
	}

	/**
	 * @return the canvas
	 */
	GLWindow getCanvas() {
		return canvas;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.opengl.canvas.IGLCanvas#addMouseListener(org.caleydo.core.view.opengl.canvas.IGLMouseListener
	 * )
	 */
	@Override
	public void addMouseListener(IGLMouseListener listener) {
		NEWTMouseAdapter adapter = new NEWTMouseAdapter(listener);
		canvas.addMouseListener(adapter);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#removeMouseListener(org.caleydo.core.view.opengl.canvas.
	 * IGLMouseListener)
	 */
	@Override
	public void removeMouseListener(IGLMouseListener listener) {
		for (MouseListener l : canvas.getMouseListeners()) {
			if (l instanceof NEWTMouseAdapter && ((NEWTMouseAdapter) l).getListener() == listener) {
				canvas.removeMouseListener(l);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.opengl.canvas.IGLCanvas#addFocusListener(org.caleydo.core.view.opengl.canvas.IGLFocusListener
	 * )
	 */
	@Override
	public void addFocusListener(IGLFocusListener listener) {
		canvas.addWindowListener(new NEWTFocusAdapter(listener));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#removeFocusListener(org.caleydo.core.view.opengl.canvas.
	 * IGLFocusListener)
	 */
	@Override
	public void removeFocusListener(IGLFocusListener listener) {
		for (WindowListener l : canvas.getWindowListeners()) {
			if (l instanceof NEWTFocusAdapter && ((NEWTFocusAdapter) l).getListener() == listener) {
				canvas.removeWindowListener(l);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#requestFocus()
	 */
	@Override
	public void requestFocus() {
		canvas.requestFocus();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.opengl.canvas.IGLCanvas#addKeyListener(org.caleydo.core.view.opengl.canvas.IGLKeyListener)
	 */
	@Override
	public void addKeyListener(IGLKeyListener listener) {
		canvas.addKeyListener(new NEWTKeyAdapter(listener));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.opengl.canvas.IGLCanvas#removeKeyListener(org.caleydo.core.view.opengl.canvas.IGLKeyListener
	 * )
	 */
	@Override
	public void removeKeyListener(IGLKeyListener listener) {
		for (KeyListener l : canvas.getKeyListeners()) {
			if (l instanceof NEWTKeyAdapter && ((NEWTKeyAdapter) l).getListener() == listener) {
				canvas.removeKeyListener(l);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#addGLEventListener(javax.media.opengl.GLEventListener)
	 */
	@Override
	public void addGLEventListener(GLEventListener listener) {
		canvas.addGLEventListener(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#removeGLEventListener(javax.media.opengl.GLEventListener)
	 */
	@Override
	public void removeGLEventListener(GLEventListener listener) {
		canvas.removeGLEventListener(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#getWidth()
	 */
	@Override
	public int getWidth() {
		return canvas.getWidth();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#getHeight()
	 */
	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#asGLAutoDrawAble()
	 */
	@Override
	public GLAutoDrawable asGLAutoDrawAble() {
		return canvas;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#asComposite()
	 */
	@Override
	public Composite asComposite() {
		return composite;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NEWTGLCanvas of " + canvas.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#createTooltip(org.caleydo.core.util.base.ILabelProvider)
	 */
	@Override
	public IPickingListener createTooltip(ILabeled label) {
		// FIXME not implemented
		return new APickingListener() {
		};
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#createTooltip(java.lang.String)
	 */
	@Override
	public IPickingListener createTooltip(String label) {
		// FIXME not implemented
		return new APickingListener() {
		};
	}

}
