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

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.jgrapht.graph.DefaultEdge;

import setvis.BubbleSetGLRenderer;

/**
 * Renders all Elements on top of {@link GLSubGraph} such as visual links.
 *
 * @author Christian Partl
 *
 */
public class GLSubGraphAugmentation extends GLElement {

	private	ArrayList<Rectangle2D> bubbleSetItems= new ArrayList<>();
	private	ArrayList<Line2D> bubbleSetEdges= new ArrayList<>();
	private Color bubbleSetColor=new Color(0.0f,1.0f,0.0f); 

	
	private List<IGLRenderer> renderers = new ArrayList<>();
	private BubbleSetGLRenderer bubbleSetRenderer=new BubbleSetGLRenderer();
	public BubbleSetGLRenderer getBubbleSetGLRenderer(){return this.bubbleSetRenderer;}
	
	public void init(final GL2 gl){
		bubbleSetRenderer.init(gl);
	}
	private int pxlWidth=1280;
	private int pxlHeight=960;

	public void setPxlSize(int newPxlWidth, int newPxlHeight){
		pxlWidth=newPxlWidth;
		pxlHeight=newPxlHeight;
	}

	
	public static class ConnectionRenderer implements IGLRenderer {

		protected final Rectangle2D loc1;
		protected final Rectangle2D loc2;
	
		private Color portalBSColor=new Color(1.0f,0.0f,0.0f); 

		public ConnectionRenderer(Rectangle2D loc1, Rectangle2D loc2) {
			this.loc1 = loc1;
			this.loc2 = loc2;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.incZ(0.5f);
			g.color(0, 1, 0, 1)
					.lineWidth(2)
					.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), (float) loc2.getCenterX(),
							(float) loc2.getCenterY());
			g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
			g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
			g.lineWidth(1);
			// g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
			// g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
			g.incZ(-0.5f);
//update portal nodes -> bubbleSet
//			ArrayList<Rectangle2D> items= new ArrayList<>();
//			ArrayList<Line2D> edges= new ArrayList<>();
//			
//			items.add(new Rectangle2D.Double(loc1.getCenterX(), loc1.getCenterY(), loc1.getWidth(), loc1.getHeight()));
//			items.add(new Rectangle2D.Double(loc2.getCenterX(), loc2.getCenterY(), loc2.getWidth(), loc2.getHeight()));
//			edges.add(new Line2D.Double(loc1.getCenterX(), loc1.getCenterY(), loc2.getCenterX(), loc2.getCenterY()));

		}
	}

	protected List<Rectangle2D> path;

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		//prepare bubbleSet texture
		this.bubbleSetRenderer.clearBubbleSet();
		this.bubbleSetRenderer.setSize(pxlWidth, pxlHeight);
		bubbleSetItems.clear();
		bubbleSetEdges.clear();
		
		if (path != null) {
			int i=0;
			Rectangle2D prevRect=null;
			for (Rectangle2D rect : path) {
				g.incZ(0.5f);
				g.color(1, 0, 0, 0.5f);
				g.fillRect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight());
				g.incZ(-0.5f);
				
				bubbleSetItems.add(new Rectangle2D.Double(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect.getHeight()));
				if(i>0)
					bubbleSetEdges.add(new Line2D.Double(rect.getCenterX(), rect.getCenterY(), prevRect.getCenterX(), prevRect.getCenterY()));
				prevRect=rect;
				i++;
			}
		}

		for (IGLRenderer renderer : renderers) {
			renderer.render(g, w, h, this);
		}
		
		//render bubbleSet		
		this.bubbleSetRenderer.addGroup(bubbleSetItems, bubbleSetEdges , bubbleSetColor);
		this.bubbleSetRenderer.update(g.gl,null,0);
		g.gl.glTranslatef(0.f, 0.f, 1.0f);
		this.bubbleSetRenderer.renderPxl(g.gl, pxlWidth, pxlHeight);
		g.gl.glTranslatef(0.f, 0.f, -1.0f);		
	}

	public void addRenderer(IGLRenderer renderer) {
		if (renderer != null) {
			renderers.add(renderer);
			repaint();
		}
	}

	public void clearRenderers() {
		renderers.clear();
		repaint();
	}

	/**
	 * @param path
	 *            setter, see {@link path}
	 */
	public void setPath(List<Rectangle2D> path) {
		this.path = path;
	}

}
