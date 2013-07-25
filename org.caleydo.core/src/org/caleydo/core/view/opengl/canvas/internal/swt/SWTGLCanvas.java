/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jogamp.opengl.swt.GLCanvas;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTGLCanvas extends AGLCanvas {
	private final GLCanvas canvas;

	private final Table<Integer, Object, Object> listenerMapping = HashBasedTable.create();

	/**
	 * volatile for better multi thread access
	 */
	private volatile boolean visible;
	private final Listener showHide = new Listener() {
		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Hide:
				visible = false;
				break;
			case SWT.Show:
				if (!visible) // schedule a visible update check
					event.display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (!visible) {
								boolean r = canvas.isVisible();
								visible = r;
							}
						}
					});
				break;
			}
		}
	};

	SWTGLCanvas(final GLCanvas canvas) {
		super(canvas);
		this.canvas = canvas;
		Composite c = canvas;
		// add a listener to the whole chain to the top as setVisible doesn't propagate down
		while (c != null) {
			c.addListener(SWT.Show, showHide);
			c.addListener(SWT.Hide, showHide);
			c = c.getParent();
		}
		// cleanup the hierarchy at the end
		this.canvas.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				Composite c = canvas;
				while (c != null) {
					c.removeListener(SWT.Show, showHide);
					c.removeListener(SWT.Hide, showHide);
					c = c.getParent();
				}
			}
		});
	}

	/**
	 * @return the visible, see {@link #visible}
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public IPickingListener createTooltip(ILabeled label) {
		return new SWTTooltipManager(canvas, label);
	}

	@Override
	public IPickingListener createTooltip(String label) {
		return new SWTTooltipManager(canvas, label);
	}

	@Override
	public IPickingListener createTooltip(IPickingLabelProvider label) {
		return new SWTTooltipManager(canvas, label);
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
				SWTMouseAdapter a = new SWTMouseAdapter(listener, SWTGLCanvas.this);
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
		canvas.addGLEventListener(listener);
	}

	@Override
	public void removeGLEventListener(GLEventListener listener) {
		canvas.removeGLEventListener(listener);
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
