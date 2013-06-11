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
package org.caleydo.core.view.opengl.canvas.internal.swt;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jogamp.opengl.swt.GLCanvas;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTGLCanvas implements IGLCanvas {
	private final GLCanvas canvas;

	private final Table<Integer, Object, Object> listenerMapping = HashBasedTable.create();
	private final Deque<GLEventListener> glEventListeners = new ConcurrentLinkedDeque<>();

	SWTGLCanvas(GLCanvas canvas) {
		this.canvas = canvas;
		canvas.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// problem if the canvas is not the the top level widget, only release(...) will be called -> call it
				// manually on disposing
				if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
					// e.widget.dispose();
					fireDispose(SWTGLCanvas.this.canvas);
					SWTGLCanvas.this.canvas.getContext().destroy();
					SWTGLCanvas.this.canvas.setRealized(false);
					listenerMapping.clear();
					glEventListeners.clear();
				}
			}
		});
		// wrap listeners for manually sending reshape events
		canvas.addGLEventListener(new GLEventListener() {
			int w = -1, h = -1;

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
				this.w = width;
				this.h = height;
				fireReshape(drawable, x, y, width, height);
			}

			@Override
			public void init(GLAutoDrawable drawable) {
				this.w = drawable.getWidth();
				this.h = drawable.getHeight();
				fireInit(drawable);
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {
				fireDispose(drawable);
			}

			@Override
			public void display(GLAutoDrawable drawable) {
				// manually fire reshape events
				if (w != drawable.getWidth() || h != drawable.getHeight() && w != -1) {
					w = drawable.getWidth();
					h = drawable.getHeight();
					fireReshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
				}
				fireDisplay(drawable);
			}
		});
	}

	@Override
	public IPickingListener createTooltip(ILabeled label) {
		return new SWTTooltipManager(canvas, label);
	}

	@Override
	public IPickingListener createTooltip(String label) {
		return new SWTTooltipManager(canvas, label);
	}

	/**
	 * @param drawable
	 */
	void fireDisplay(GLAutoDrawable drawable) {
		if (canvas.isDisposed())
			return;
		for (GLEventListener l : glEventListeners)
			l.display(drawable);
	}

	void fireDispose(GLAutoDrawable drawable) {
		for (GLEventListener l : glEventListeners)
			l.dispose(drawable);
	}

	void fireInit(GLAutoDrawable drawable) {
		for (GLEventListener l : glEventListeners)
			l.init(drawable);
	}

	void fireReshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		for (GLEventListener l : glEventListeners)
			l.reshape(drawable, x, y, width, height);
	}

	/**
	 * @return the canvas
	 */
	GLCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void addMouseListener(final IGLMouseListener listener) {
		if (canvas.isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (canvas.isDisposed())
					return;
				SWTMouseAdapter a = new SWTMouseAdapter(listener);
				listenerMapping.put(SWT.MouseMove, listener, a);
				canvas.addMouseListener(a);
				canvas.addMouseMoveListener(a);
				canvas.addMouseWheelListener(a);
				canvas.addMouseTrackListener(a);
				canvas.addMenuDetectListener(a);

			}
		});
	}

	@Override
	public void removeMouseListener(final IGLMouseListener listener) {
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTMouseAdapter a = (SWTMouseAdapter) listenerMapping.remove(SWT.MouseMove, listener);
				if (a == null)
					return;
				if (canvas.isDisposed())
					return;
				canvas.removeMouseListener(a);
				canvas.removeMouseMoveListener(a);
				canvas.removeMouseWheelListener(a);
				canvas.removeMouseTrackListener(a);
				canvas.removeMenuDetectListener(a);
			}
		});
	}

	@Override
	public void addFocusListener(final IGLFocusListener listener) {
		if (canvas.isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTFocusAdapter a = new SWTFocusAdapter(listener);
				listenerMapping.put(SWT.FocusIn, listener, a);
				canvas.addFocusListener(a);
			}
		});
	}

	@Override
	public void removeFocusListener(final IGLFocusListener listener) {
		if (canvas.isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTFocusAdapter a = (SWTFocusAdapter) listenerMapping.remove(SWT.FocusIn, listener);
				if (a == null)
					return;
				if (canvas.isDisposed())
					return;
				canvas.removeFocusListener(a);
			}
		});
	}

	@Override
	public void requestFocus() {
		if (canvas.isDisposed())
			return;
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (canvas.isDisposed())
					return;
				canvas.forceFocus();
			}
		});
	}

	protected Display getDisplay() {
		return canvas.getDisplay();
	}

	@Override
	public void addKeyListener(final IGLKeyListener listener) {
		if (canvas.isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (canvas.isDisposed())
					return;
				SWTKeyAdapter a = new SWTKeyAdapter(listener);
				listenerMapping.put(SWT.KeyDown, listener, a);
				canvas.addKeyListener(a);
			}
		});
	}

	@Override
	public void removeKeyListener(final IGLKeyListener listener) {
		if (canvas.isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTKeyAdapter a = (SWTKeyAdapter) listenerMapping.remove(SWT.KeyDown, listener);
				if (a == null)
					return;
				if (canvas.isDisposed())
					return;
				canvas.removeKeyListener(a);
			}
		});
	}

	@Override
	public void addGLEventListener(GLEventListener listener) {
		glEventListeners.add(listener);
	}

	@Override
	public void removeGLEventListener(GLEventListener listener) {
		glEventListeners.remove(listener);
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
		return canvas;
	}

	@Override
	public final void showPopupMenu(Iterable<? extends AContextMenuItem> items) {
		final Composite parent = asComposite();
		ASWTBasedCanvasFactory.showSWTPopupMenu(items, parent);

	}

	@Override
	public String toString() {
		return "SWTGLCanvas of " + canvas.toString();
	}

}
