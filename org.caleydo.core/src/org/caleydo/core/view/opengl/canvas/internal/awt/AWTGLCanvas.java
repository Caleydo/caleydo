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
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.SwingUtilities;

import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
final class AWTGLCanvas implements IGLCanvas {
	private final GLCanvas canvas;
	private final Composite composite;

	AWTGLCanvas(GLCanvas canvas, Composite composite) {
		this.canvas = canvas;
		this.composite = composite;
	}

	/**
	 * @return the canvas
	 */
	GLCanvas getCanvas() {
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
		AWTMouseAdapter adapter = new AWTMouseAdapter(listener);
		canvas.addMouseListener(adapter);
		canvas.addMouseMotionListener(adapter);
		canvas.addMouseWheelListener(adapter);
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
			if (l instanceof AWTMouseAdapter && ((AWTMouseAdapter) l).getListener() == listener) {
				canvas.removeMouseListener(l);
				canvas.removeMouseMotionListener((AWTMouseAdapter) l);
				canvas.removeMouseWheelListener((AWTMouseAdapter) l);
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
		canvas.addFocusListener(new AWTFocusAdapter(listener));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.opengl.canvas.IGLCanvas#removeFocusListener(org.caleydo.core.view.opengl.canvas.
	 * IGLFocusListener)
	 */
	@Override
	public void removeFocusListener(IGLFocusListener listener) {
		for (FocusListener l : canvas.getFocusListeners()) {
			if (l instanceof AWTFocusAdapter && ((AWTFocusAdapter) l).getListener() == listener) {
				canvas.removeFocusListener(l);
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				canvas.requestFocus();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.opengl.canvas.IGLCanvas#addKeyListener(org.caleydo.core.view.opengl.canvas.IGLKeyListener)
	 */
	@Override
	public void addKeyListener(IGLKeyListener listener) {
		canvas.addKeyListener(new AWTKeyAdapter(listener));
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
			if (l instanceof AWTKeyAdapter && ((AWTKeyAdapter) l).getListener() == listener) {
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
		return "AWTGLCanvas of " + canvas.getName();
	}

}
