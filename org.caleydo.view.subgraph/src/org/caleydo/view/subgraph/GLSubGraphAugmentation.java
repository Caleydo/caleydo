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

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

import setvis.BubbleSetGLRenderer;
import setvis.bubbleset.BubbleSet;

/**
 * Renders all Elements on top of {@link GLSubGraph} such as visual links.
 *
 * @author Christian Partl
 *
 */
public class GLSubGraphAugmentation extends GLElement {

	private List<LinkRenderer> portalRenderers = new ArrayList<>();
	// private ArrayList<Rectangle2D> portals = new ArrayList<>();
	private ArrayList<Rectangle2D> bubbleSetItems = new ArrayList<>();
	private ArrayList<Line2D> bubbleSetEdges = new ArrayList<>();
	private Color bubbleSetColor = new Color(0.0f, 1.0f, 0.0f);
	private Color portalColor = new Color(1.0f, 0.0f, 0.0f);

	// private Rectangle2D portalStartNode;
	// private boolean isShowPortals = false;

	private List<IGLRenderer> renderers = new ArrayList<>();
	private BubbleSetGLRenderer bubbleSetRenderer = new BubbleSetGLRenderer();

	// public BubbleSetGLRenderer getBubbleSetGLRenderer(){return this.bubbleSetRenderer;}

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

	public static class LinkRenderer implements IGLRenderer {

		protected final Rectangle2D loc1;
		protected final Rectangle2D loc2;
		protected final PathwayMultiFormInfo info1;
		protected final PathwayMultiFormInfo info2;
		protected final boolean isLocation1Window;
		protected final boolean isLocation2Window;
		protected final float stubSize;
		protected final boolean drawLink;
		protected final boolean isContextLink;
		protected final boolean isPathLink;

		private Color portalBSColor = new Color(1.0f, 0.0f, 0.0f);

		public LinkRenderer(boolean drawLink, Rectangle2D loc1, Rectangle2D loc2, PathwayMultiFormInfo info1,
				PathwayMultiFormInfo info2, float stubSize, boolean isLocation1Window, boolean isLocation2Window,
				boolean isContextLink, boolean isPathLink) {
			this.drawLink = drawLink;
			this.loc1 = loc1;
			this.loc2 = loc2;
			this.info1 = info1;
			this.info2 = info2;
			this.stubSize = stubSize;
			this.isLocation1Window = isLocation1Window;
			this.isLocation2Window = isLocation2Window;
			this.isContextLink = isContextLink;
			this.isPathLink = isPathLink;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.incZ(0.5f);
			// g.gl.glBegin(GL.GL_LINES);
			// g.color(1, 0, 0, 1);
			// g.gl.glVertex2f((float) loc1.getCenterX(), (float) loc1.getCenterY());
			// g.color(1, 0, 0, 0);
			// g.gl.glVertex2f((float) loc2.getCenterX(), (float) loc2.getCenterY());
			// g.gl.glEnd();
			if (isPathLink) {
				g.color(0, 1, 0, 1f);
			} else if (isContextLink) {
				g.color(1, 0, 1, 1f);
			} else {
				g.color(1, 0, 0, 1f);
			}
			g.lineWidth(2);

			if (!drawLink) {
				Vec2f direction = new Vec2f((float) loc1.getCenterX() - (float) loc2.getCenterX(),
						(float) loc1.getCenterY() - (float) loc2.getCenterY());
				direction.normalize();
				if (!isLocation1Window) {
					Vec2f stub1End = new Vec2f((float) loc1.getCenterX() - 20 * direction.x() * stubSize,
							(float) loc1.getCenterY() - 20 * direction.y() * stubSize);
					g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y());
				}
				if (!isLocation2Window) {
					Vec2f stub2End = new Vec2f((float) loc2.getCenterX() + 20 * direction.x() * stubSize,
							(float) loc2.getCenterY() + 20 * direction.y() * stubSize);
					g.drawLine((float) loc2.getCenterX(), (float) loc2.getCenterY(), stub2End.x(), stub2End.y());
				}
			} else {
				g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), (float) loc2.getCenterX(),
						(float) loc2.getCenterY());
			}
			g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
			g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
			g.lineWidth(1);
			// // g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
			// // g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
			g.incZ(-0.5f);
		}
	}

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
			this.bubbleSetRenderer.update(g.gl, null, 0);
			g.gl.glTranslatef(0.f, 0.f, -1.0f);
		}
		g.gl.glTranslatef(0.f, 0.f, 1.0f);

		this.bubbleSetRenderer.renderPxl(g.gl, pxlWidth, pxlHeight);
		this.renderPortalLinks(g);
		for (IGLRenderer renderer : renderers) {
			renderer.render(g, w, h, this);
		}
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

	public void addPortalLinkRenderer(LinkRenderer renderer) {
		// System.out.println("addPortalHighlightRenderer");
		portalRenderers.add(renderer);
		renderers.add(renderer);
		repaint();
	}

	public void clearPortalConnectionRenderers() {
		if (!portalRenderers.isEmpty()) {
			renderers.removeAll(portalRenderers);
			portalRenderers.clear();
			repaint();
		}
	}
}
