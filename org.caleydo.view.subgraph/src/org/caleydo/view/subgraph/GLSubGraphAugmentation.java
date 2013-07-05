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
package org.caleydo.view.subgraph;

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
import setvis.bubbleset.BubbleSet;

/**
 * Renders all Elements on top of {@link GLSubGraph} such as visual links.
 *
 * @author Christian Partl
 *
 */
public class GLSubGraphAugmentation extends GLElementContainer {

	// private List<LinkRenderer> portalRenderers = new ArrayList<>();
	// private ArrayList<Rectangle2D> portals = new ArrayList<>();
	private ArrayList<Rectangle2D> bubbleSetItems = new ArrayList<>();
	private ArrayList<Line2D> bubbleSetEdges = new ArrayList<>();
	private Color bubbleSetColor = new Color(0.0f, 1.0f, 0.0f);
	private Color portalColor = new Color(1.0f, 0.0f, 0.0f);

	private GLSubGraph view;

	// private Rectangle2D portalStartNode;
	// private boolean isShowPortals = false;

	// private List<IGLRenderer> renderers = new ArrayList<>();
	private BubbleSetGLRenderer bubbleSetRenderer = new BubbleSetGLRenderer();

	// public BubbleSetGLRenderer getBubbleSetGLRenderer(){return this.bubbleSetRenderer;}

	/**
	 *
	 */
	public GLSubGraphAugmentation(GLSubGraph view) {
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

	public boolean isDirty = false;

	// public static class PortalHighlightRenderer implements IGLRenderer {
	// protected final Rectangle2D location;
	//
	// public PortalHighlightRenderer(Rectangle2D location) {
	// this.location = location;
	// }
	//
	// @Override
	// public void render(GLGraphics g, float w, float h, GLElement parent) {
	//
	// g.incZ(1f);
	// g.color(new Color(51, 160, 44)).lineWidth(2);
	// g.drawRect((float) location.getX() - 3, (float) location.getY() - 3, (float) location.getWidth() + 6,
	// (float) location.getHeight() + 10);
	// g.lineWidth(1);
	// g.incZ(-1f);
	// }
	// }



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
		if (disabled) {
			return;
		}
		if (this.isDirty) {
			this.isDirty = false;
			// prepare bubbleSet texture
			this.bubbleSetRenderer.clearBubbleSet();
			this.bubbleSetRenderer.setSize(pxlWidth, pxlHeight);
			bubbleSetItems.clear();
			bubbleSetEdges.clear();

			if (path != null) {
				int i = 0;
				Rectangle2D prevRect = new Rectangle2D.Double(0f, 0f, 0f, 0f);
				for (Rectangle2D rect : path) {
					g.incZ(0.5f);
					// g.color(0, 0, 1, 0.5f);
					// g.fillRect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float)
					// rect.getHeight());

					bubbleSetItems.add(new Rectangle2D.Double(rect.getCenterX(), rect.getCenterY(), rect.getWidth(),
							rect.getHeight()));
					if (i > 0) {
						bubbleSetEdges.add(new Line2D.Double(rect.getCenterX(), rect.getCenterY(), prevRect
								.getCenterX(), prevRect.getCenterY()));
					}
					prevRect.setRect(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect.getHeight());
					i++;
					g.incZ(-0.5f);

				}
			}

			// render bubbleSet
			g.gl.glTranslatef(0.f, 0.f, 1.0f);
			this.bubbleSetRenderer.addGroup(bubbleSetItems, bubbleSetEdges, bubbleSetColor);
			((BubbleSet) this.bubbleSetRenderer.setOutline).useVirtualEdges(false);
			// routingIterations, marchingIterations,pixelGroup
			((BubbleSet) this.bubbleSetRenderer.setOutline).setParameter(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 5);
			// ((BubbleSet)this.bubbleSetRenderer.setOutline).setParameter(1, 1,1,1.0,1.0,.5,1.5, 1.0, 1);
			// setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
			// BubbleSet(routingIterations, marchingIterations,pixelGroup,
			// edgeR0,edgeR1, nodeR0, nodeR1,
			// morphBuffer,skip)
			// if (this.portals != null && isShowPortals) {
			// for (Rectangle2D rect : this.portals) {
			// ArrayList<Rectangle2D> items = new ArrayList<>();
			// items.add(new Rectangle2D.Double(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect
			// .getHeight()));
			// this.bubbleSetRenderer.addGroup(items, null, portalColor);
			// }
			// }
			//this.bubbleSetRenderer.update(g.gl, null, 0);
			g.gl.glTranslatef(0.f, 0.f, -1.0f);
		}
		g.gl.glTranslatef(0.f, 0.f, 1.0f);

		//this.bubbleSetRenderer.renderPxl(g.gl, pxlWidth, pxlHeight);
		//this.renderPortalLinks(g);
		// for (IGLRenderer renderer : renderers) {
		// renderer.render(g, w, h, this);
		// }
		g.gl.glTranslatef(0.f, 0.f, -1.0f);

	}

	// public void showPortals(boolean boolVal) {
	// isShowPortals = boolVal;
	// isDirty = true;
	// }

	public void renderPortalLinks(GLGraphics g) {
		// if (this.portals == null || !isShowPortals)
		// return;
		// for (Rectangle2D rect : this.portals) {
		// g.color(1, 0, 0, 1)
		// .lineWidth(2)
		// .drawLine((float) this.portalStartNode.getCenterX(), (float) this.portalStartNode.getCenterY(),
		// (float) rect.getCenterX(), (float) rect.getCenterY());
		// }
	}

	// public void addConnectionRenderer(ConnectionRenderer renderer) {
	// if (renderer != null) {
	// renderers.add(renderer);
	// repaint();
	// }
	// }
	//
	// public void clearRenderers() {
	// renderers.clear();
	// repaint();
	// }

	/**
	 * @param path
	 *            setter, see {@link path}
	 */
	public void setPath(List<Rectangle2D> path) {
		this.path = path;
	}

	// public void updatePortalRects(Rectangle2D node, ArrayList<Rectangle2D> portalList) {
	// this.portals = portalList;
	// this.portalStartNode = node;
	// }

	// public void addPortalLinkRenderer(LinkRenderer renderer) {
	// // System.out.println("addPortalHighlightRenderer");
	// // portalRenderers.add(renderer);
	// add(renderer);
	// // repaint();
	// }

	// public void clearPortalConnectionRenderers() {
	// if (!portalRenderers.isEmpty()) {
	// removeAll(portalRenderers);
	// portalRenderers.clear();
	// repaint();
	// }
	// }
}
