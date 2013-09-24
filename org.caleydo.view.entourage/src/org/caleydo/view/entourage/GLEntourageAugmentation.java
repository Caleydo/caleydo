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
package org.caleydo.view.entourage;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

import setvis.BubbleSetGLRenderer;

/**
 * Renders all Elements on top of {@link GLEntourage} such as visual links.
 *
 * @author Christian Partl
 *
 */
public class GLEntourageAugmentation extends GLElementContainer {

	private ArrayList<Rectangle2D> bubbleSetItems = new ArrayList<>();
	private ArrayList<Line2D> bubbleSetEdges = new ArrayList<>();
	private Color bubbleSetColor = new Color(0.0f, 1.0f, 0.0f);
	private BubbleSetGLRenderer bubbleSetRenderer = new BubbleSetGLRenderer();

	/**
	 *
	 */
	public GLEntourageAugmentation(GLEntourage view) {
		setLayout(GLLayouts.LAYERS);
	}

	public void init(final GL2 gl) {
		bubbleSetRenderer.init(gl);
	}

	private int pxlWidth = 1280;
	private int pxlHeight = 960;

	public void setPxlSize(int newPxlWidth, int newPxlHeight) {
		pxlWidth = newPxlWidth;
		pxlHeight = newPxlHeight;
	}

	private boolean isDirty = false;
	protected List<Rectangle2D> path;
	private boolean disabled = false;

	public void disable() {
		disabled = true;
	}

	public void enable() {
		disabled = false;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		// if (disabled) {
		// return;
		// }
		// if (this.isDirty) {
		// this.isDirty = false;
		// // prepare bubbleSet texture
		// this.bubbleSetRenderer.clearBubbleSet();
		// this.bubbleSetRenderer.setSize(pxlWidth, pxlHeight);
		// bubbleSetItems.clear();
		// bubbleSetEdges.clear();
		//
		// if (path != null) {
		// int i = 0;
		// Rectangle2D prevRect = new Rectangle2D.Double(0f, 0f, 0f, 0f);
		// for (Rectangle2D rect : path) {
		// g.incZ(0.5f);
		// // g.color(0, 0, 1, 0.5f);
		// // g.fillRect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float)
		// // rect.getHeight());
		//
		// bubbleSetItems.add(new Rectangle2D.Double(rect.getCenterX(), rect.getCenterY(), rect.getWidth(),
		// rect.getHeight()));
		// if (i > 0) {
		// bubbleSetEdges.add(new Line2D.Double(rect.getCenterX(), rect.getCenterY(), prevRect
		// .getCenterX(), prevRect.getCenterY()));
		// }
		// prevRect.setRect(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect.getHeight());
		// i++;
		// g.incZ(-0.5f);
		//
		// }
		// }
		//
		// // render bubbleSet
		// g.gl.glTranslatef(0.f, 0.f, 1.0f);
		// this.bubbleSetRenderer.addGroup(bubbleSetItems, bubbleSetEdges, bubbleSetColor);
		// ((BubbleSet) this.bubbleSetRenderer.setOutline).useVirtualEdges(false);
		// // routingIterations, marchingIterations,pixelGroup
		// ((BubbleSet) this.bubbleSetRenderer.setOutline).setParameter(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 5);
		// // ((BubbleSet)this.bubbleSetRenderer.setOutline).setParameter(1, 1,1,1.0,1.0,.5,1.5, 1.0, 1);
		// // setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
		// // BubbleSet(routingIterations, marchingIterations,pixelGroup,
		// // edgeR0,edgeR1, nodeR0, nodeR1,
		// // morphBuffer,skip)
		// // if (this.portals != null && isShowPortals) {
		// // for (Rectangle2D rect : this.portals) {
		// // ArrayList<Rectangle2D> items = new ArrayList<>();
		// // items.add(new Rectangle2D.Double(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect
		// // .getHeight()));
		// // this.bubbleSetRenderer.addGroup(items, null, portalColor);
		// // }
		// // }
		// //this.bubbleSetRenderer.update(g.gl, null, 0);
		// g.gl.glTranslatef(0.f, 0.f, -1.0f);
		// }
		// g.gl.glTranslatef(0.f, 0.f, 1.0f);

		//this.bubbleSetRenderer.renderPxl(g.gl, pxlWidth, pxlHeight);
		//this.renderPortalLinks(g);
		// for (IGLRenderer renderer : renderers) {
		// renderer.render(g, w, h, this);
		// }
		// g.gl.glTranslatef(0.f, 0.f, -1.0f);

	}


	/**
	 * @param path
	 *            setter, see {@link path}
	 */
	public void setPath(List<Rectangle2D> path) {
		this.path = path;
	}

}
