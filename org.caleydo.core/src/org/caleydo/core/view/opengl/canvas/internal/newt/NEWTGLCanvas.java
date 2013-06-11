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
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.internal.swt.ASWTBasedCanvasFactory;
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

	@Override
	public void addMouseListener(IGLMouseListener listener) {
		NEWTMouseAdapter adapter = new NEWTMouseAdapter(listener);
		canvas.addMouseListener(adapter);
	}

	@Override
	public void removeMouseListener(IGLMouseListener listener) {
		for (MouseListener l : canvas.getMouseListeners()) {
			if (l instanceof NEWTMouseAdapter && ((NEWTMouseAdapter) l).getListener() == listener) {
				canvas.removeMouseListener(l);
				break;
			}
		}
	}

	@Override
	public void addFocusListener(IGLFocusListener listener) {
		canvas.addWindowListener(new NEWTFocusAdapter(listener));
	}

	@Override
	public void removeFocusListener(IGLFocusListener listener) {
		for (WindowListener l : canvas.getWindowListeners()) {
			if (l instanceof NEWTFocusAdapter && ((NEWTFocusAdapter) l).getListener() == listener) {
				canvas.removeWindowListener(l);
				break;
			}
		}
	}


	@Override
	public void requestFocus() {
		canvas.requestFocus();
	}


	@Override
	public void addKeyListener(IGLKeyListener listener) {
		canvas.addKeyListener(new NEWTKeyAdapter(listener));
	}


	@Override
	public void removeKeyListener(IGLKeyListener listener) {
		for (KeyListener l : canvas.getKeyListeners()) {
			if (l instanceof NEWTKeyAdapter && ((NEWTKeyAdapter) l).getListener() == listener) {
				canvas.removeKeyListener(l);
				break;
			}
		}
	}


	@Override
	public void addGLEventListener(GLEventListener listener) {
		canvas.addGLEventListener(listener);
	}


	@Override
	public void removeGLEventListener(GLEventListener listener) {
		canvas.removeGLEventListener(listener);
	}


	@Override
	public int getWidth() {
		return canvas.getWidth();
	}

	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	@Override
	public GLAutoDrawable asGLAutoDrawAble() {
		return canvas;
	}

	@Override
	public Composite asComposite() {
		return composite;
	}

	@Override
	public String toString() {
		return "NEWTGLCanvas of " + canvas.toString();
	}


	@Override
	public IPickingListener createTooltip(ILabeled label) {
		// FIXME not implemented
		return new APickingListener() {
		};
	}

	@Override
	public IPickingListener createTooltip(String label) {
		// FIXME not implemented
		return new APickingListener() {
		};
	}

	@Override
	public void showPopupMenu(Iterable<? extends AContextMenuItem> items) {
		final Composite parent = asComposite();
		ASWTBasedCanvasFactory.showSWTPopupMenu(items, parent);

	}

}
