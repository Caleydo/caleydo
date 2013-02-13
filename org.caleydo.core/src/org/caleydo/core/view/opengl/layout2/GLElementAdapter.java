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
package org.caleydo.core.view.opengl.layout2;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLElementAccessor;
import org.caleydo.core.view.opengl.canvas.IGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;

/**
 * an adapter to use a {@link ALayoutRenderer} as as {@link GLElement}
 *
 * requires that the {@link IGLElementContext} is a {@link IGLView} and requires {@link AGLView} as wrapper
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementAdapter extends GLElement {
	/**
	 * faked elements
	 */
	private final LayoutManager layout;
	private final ViewFrustum viewFrustum;
	private final PixelGLConverter pixelGLConverter;

	/**
	 * the underlying element
	 */
	private ElementLayout wrappee;

	/**
	 * @param view
	 *            the base view for getting status information
	 */
	public GLElementAdapter(AGLView view) {
		this.viewFrustum = new ViewFrustum();
		this.viewFrustum.setNear(view.getViewFrustum().getNear());
		this.viewFrustum.setFar(view.getViewFrustum().getFar());
		this.pixelGLConverter = GLElementAccessor.createPixelGLConverter(viewFrustum, view.getParentGLCanvas());

		this.layout = new LayoutManager(viewFrustum, pixelGLConverter);
		this.layout.setUseDisplayLists(false);
	}

	public GLElementAdapter(AGLView view, ALayoutRenderer renderer) {
		this(view);
		setRenderer(renderer);
	}

	@Override
	protected void takeDown() {
		// assumption
		IGLView view = (IGLView) context;
		GL2 gl = view.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		this.layout.destroy(gl);
		super.takeDown();
	}

	/**
	 * @param renderer
	 *            setter, see {@link renderer}
	 */
	public GLElementAdapter setRenderer(ALayoutRenderer renderer) {
		if (this.wrappee == null) {
			this.wrappee = new ElementLayout("wrappee");
		}
		this.wrappee.setRenderer(renderer);
		return this;
	}

	public GLElementAdapter setElement(ElementLayout element) {
		this.wrappee = element;
		wrappee.setGrabX(true);
		wrappee.setGrabY(true);
		if (this.layout != null) {
			this.layout.setBaseElementLayout(wrappee);
		}
		return this;
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (this.wrappee == null) {
			this.wrappee = new ElementLayout("wrappee");
		}
		wrappee.setGrabX(true);
		wrappee.setGrabY(true);
		this.layout.setBaseElementLayout(wrappee);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (viewFrustum.getRight() != w || viewFrustum.getTop() != h) {
			viewFrustum.setRight(w);
			viewFrustum.setTop(h);
			layout.updateLayout();

		}
		// g.color(Color.BLACK).fillRect(0, 0, w, h);
		g.save();
		final GL2 gl = g.gl;
		// swap the origin to the top bottom corner
		// convert the coordinate system to
		// 0,h w,h
		// 0,0 w,0
		gl.glTranslatef(0, h, 0);
		gl.glScalef(1, -1, 1);

		layout.render(gl);

		g.restore();

		repaintAll(); // invalidate it again to avoid that parent display lists will be activated
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		// the same again
		renderImpl(g, w, h);
	}
}
