/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLRunnable;

import org.apache.commons.lang.SystemUtils;
import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.util.function.AFloatFunction;
import org.caleydo.core.util.function.IFloatFunction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AGLCanvas implements IGLCanvas {
	private float scale;

	/**
	 * volatile for better multi thread access
	 */
	protected volatile boolean visible = true;
	private final Runnable updateVisibility = new Runnable() {
		@Override
		public void run() {
			Composite c = asComposite();
			boolean r = !c.isDisposed() && c.isVisible();
			if (r != visible) {
				visible = r;
				if (visible)
					triggerResize();
			}
		}
	};
	private final Listener showHide = new Listener() {
		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Hide:
				visible = false;
				break;
			case SWT.Show:
				visible = true;
				triggerResize();
				break;
			}
		}
	};

	public AGLCanvas(GLAutoDrawable drawable) {
		float s = MyPreferences.getViewZoomFactor();
		if (s <= 0)
			s = 1;
		scale = s;
		drawable.addGLEventListener(new ReshapeOnScaleChange());
	}

	/**
	 *
	 */
	protected void triggerResize() {
		if (!SystemUtils.IS_OS_MAC_OSX) // mac special
			return;
		Composite c = asComposite();
		c.layout(true);
		org.eclipse.swt.graphics.Point size = c.getSize();
		// change the size of composite to force some surface updates, see #
		c.setSize(size.x - 1, size.y);
	}

	protected void init(final Composite canvas) {
		Composite c = canvas;
		// add a listener to the whole chain to the top as setVisible doesn't propagate down
		c.addListener(SWT.Show, showHide);
		c.addListener(SWT.Hide, showHide);
	}

	/**
	 * @return the visible, see {@link #visible}
	 */
	@Override
	public boolean isVisible() {
		Composite c = asComposite();
		Display display = c.isDisposed() ? null : c.getDisplay();
		if (display == null)
			return false;
		// async not perfect as we have old state but better than blocking
		display.asyncExec(updateVisibility);
		if (!visible)
			return false;
		//in addition check if the frame buffer is ok
		GL gl = asGLAutoDrawAble().getGL();
		if (gl != null && !isFrameBufferComplete(gl))
			return false;
		return true;
	}

	private static boolean isFrameBufferComplete(GL gl) {
		if (!gl.isFunctionAvailable("glCheckFramebufferStatus"))
			return true;
		int frameBuffer = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		if (frameBuffer != GL.GL_FRAMEBUFFER_COMPLETE) {
			return false;
		}
		return true;
	}

	@Override
	public final float getDIPWidth() {
		return dip(asGLAutoDrawAble().getWidth());
	}

	@Override
	public final float getDIPHeight() {
		return dip(asGLAutoDrawAble().getHeight());
	}

	private float dip(int px) {
		return Units.px(px) / scale;
	}

	public final float toDIP(int value_px) {
		return dip(value_px);
	}

	public final Vec2f toDIP(Point point) {
		return new Vec2f(dip(point.x), dip(point.y));
	}

	@Override
	public Float toDIP(Rectangle viewArea_raw) {
		return new Rectangle2D.Float(toDIP(viewArea_raw.x), toDIP(viewArea_raw.y), toDIP(viewArea_raw.width),
				toDIP(viewArea_raw.height));
	}

	@Override
	public final int toRawPixel(float value_dip) {
		return Units.dip2px(value_dip, scale);
	}

	@Override
	public IFloatFunction toRawPixelFunction() {
		return new AFloatFunction() {
			@Override
			public float apply(float dip) {
				return toRawPixel(dip);
			}
		};
	}

	@Override
	public final float getWidth(Units unit) {
		return unit.unapply(getDIPWidth());
	}

	@Override
	public final float getHeight(Units unit) {
		return unit.unapply(getDIPHeight());
	}

	@Override
	public final Rectangle toRawPixel(Rectangle2D.Float viewArea_dip) {
		return new Rectangle(toRawPixel(viewArea_dip.x), toRawPixel(viewArea_dip.y), toRawPixel(viewArea_dip.width),
				toRawPixel(viewArea_dip.height));
	}

	protected final void updateScale() {
		scale = MyPreferences.getViewZoomFactor();
		GLAutoDrawable d = asGLAutoDrawAble();
		// fire a reshape too all registered listeners
		d.invoke(false, new GLRunnable() {
			@Override
			public boolean run(GLAutoDrawable drawable) {
				final int count = drawable.getGLEventListenerCount();
				for (int i = 0; i < count; ++i) {
					GLEventListener l = drawable.getGLEventListener(i);
					l.reshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
				}
				return true;
			}
		});
	}

	private class ReshapeOnScaleChange implements GLEventListener, IPropertyChangeListener {
		@Override
		public void init(GLAutoDrawable drawable) {
			MyPreferences.prefs().addPropertyChangeListener(this);
		}

		@Override
		public void display(GLAutoDrawable drawable) {

		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			MyPreferences.prefs().removePropertyChangeListener(this);
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		}

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (MyPreferences.VIEW_ZOOM_FACTOR.equals(event.getProperty())) {
				updateScale();
			}
		}
	}
}
