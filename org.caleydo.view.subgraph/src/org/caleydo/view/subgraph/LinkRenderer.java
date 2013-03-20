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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.subgraph.GLSubGraph.PathwayMultiFormInfo;

/**
 * Renders a single Link as augmentation.
 *
 * @author Christian Partl
 *
 */

// TODO: cleanup and refactor
public class LinkRenderer extends PickableGLElement {

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
	protected final PathwayVertexRep vertexRep1;
	protected final PathwayVertexRep vertexRep2;
	protected final GLSubGraph view;
	private ConnectionBandRenderer bandRenderer=null;


	public LinkRenderer(GLSubGraph view, boolean drawLink, Rectangle2D loc1, Rectangle2D loc2,
			PathwayMultiFormInfo info1, PathwayMultiFormInfo info2, float stubSize, boolean isLocation1Window,
			boolean isLocation2Window, boolean isContextLink, boolean isPathLink, PathwayVertexRep vertexRep1,
			PathwayVertexRep vertexRep2, ConnectionBandRenderer newBandRenderer) {
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
		this.vertexRep1 = vertexRep1;
		this.vertexRep2 = vertexRep2;
		this.view = view;
		
		bandRenderer = newBandRenderer;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
	}

	public void render(GLGraphics g, float w, float h) {
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
				Vec2f stub1End = new Vec2f(
						(float) loc1.getCenterX() - 60 * direction.x() * stubSize,
						(float) loc1.getCenterY() - 60 * direction.y() * stubSize);
				//g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y());
				drawStub(g.gl,
						(float) loc1.getCenterX(), (float) loc1.getCenterY(), 
						(float) stub1End.x(), (float)stub1End.y(),
						(float) loc1.getHeight(),(float) loc1.getHeight());
//				drawLink(g.gl, 
//						(float) loc1.getCenterX(), (float) loc1.getCenterY(), 
//						(float) loc2.getCenterX(),(float) loc2.getCenterY(),
//						(float) loc1.getHeight(),(float) loc2.getHeight());
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
		//g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
		g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
		g.lineWidth(1);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
		g.incZ(-0.5f);
	}

	

	
	private Vec2f rotateVec2(Vec2f vec, float angle)
	{
		Vec2f val=new Vec2f();
		val.setX((float)((vec.get(0) * Math.cos(angle)) - (vec.get(1) * Math.sin(angle))));
		val.setY((float)((vec.get(0) * Math.sin(angle)) + (vec.get(1) * Math.cos(angle))));
		return val;
	}
	
	
	public void  drawLink(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth){

	}
	///////////////////////
	
	public void  drawStub(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth){
		//
		//
		PixelGLConverter pxlConverter = this.view.getPixelGLConverter();		
		Color bColor = new Color( 0.4f, 0.4f, 0.4f, 1f );		
		//
        Vec2f dirNorm=new Vec2f(((float)xE-(float)xS),(float)yE-(float)yS);
        float length = dirNorm.length();
        dirNorm.normalize();
        Vec2f normalVec=rotateVec2(dirNorm,(float)Math.PI/2f);

        // BAND WIDTH START
        float glStartOffsetX=pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)*startWidth/1.25f));
        float glStartOffsetY=pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)*startWidth/1.25f));
		// BAND WIDTH END
        float glEndOffsetX=pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)*endWidth));
        float glEndOffsetY=pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)*endWidth));

    	// START POINTS 
        float glxS=pxlConverter.getGLWidthForPixelWidth(Math.round(xS));
        float glyS=pxlConverter.getGLHeightForPixelHeight(Math.round(yS));           
        float p1x=glxS-(glStartOffsetX);//
        float p1y=glyS-(glStartOffsetY);//
        float p2x=glxS+(glStartOffsetX);//
        float p2y=glyS+(glStartOffsetY);//
    	// END POINTS           
        float glxE=pxlConverter.getGLWidthForPixelWidth(Math.round(xE));
        float glyE=pxlConverter.getGLHeightForPixelHeight(Math.round(yE));
        float p3x=glxE-(glEndOffsetX/8.0f);//
        float p3y=glyE-(glEndOffsetY/8.0f);//
        float p4x=glxE+(glEndOffsetX/8.0f);//
        float p4y=glyE+(glEndOffsetY/8.0f);//
    	// MID POINTS           
        float p5x=p1x+(dirNorm.get(0)*(length/3.0f))+(glEndOffsetX/3.0f);//
        float p5y=p1y+(dirNorm.get(1)*(length/3.0f))+(glEndOffsetY/3.0f);//
        float p6x=p2x+(dirNorm.get(0)*(length/3.0f))-(glEndOffsetX/3.0f);//
        float p6y=p2y+(dirNorm.get(1)*(length/3.0f))-(glEndOffsetY/3.0f);//
		//
        float p7x=p1x+(2f*dirNorm.get(0)*(length/3.0f))+(glEndOffsetX/2.25f);//
        float p7y=p1y+(2f*dirNorm.get(1)*(length/3.0f))+(glEndOffsetY/2.25f);//
        float p8x=p2x+(2f*dirNorm.get(0)*(length/3.0f))-(glEndOffsetX/2.25f);//
        float p8y=p2y+(2f*dirNorm.get(1)*(length/3.0f))-(glEndOffsetY/2.25f);//


        gl.glTranslatef(0f, 0f, 4f);
        
//        float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, 1 };
//		float[] rightTopPos = new float[] { p3x, p3y };
//		float[] rightBottomPos = new float[] { p4x, p4y };       
//		float[] leftTopPos = new float[] { p1x, p1y };
//		float[] leftBottomPos = new float[] { p2x, p2y };
//		float offsetX = 0.0f;//this.x * 0.4f;			
//		bandRenderer.renderSingleBand(gl, 
//				leftTopPos, leftBottomPos,		
//				rightTopPos, rightBottomPos, 
//				false, offsetX, 0,bandColor);		
		//
        Point2D p00=new Point2D.Float(p1x,p1y);
        Point2D p01=new Point2D.Float(p2x,p2y);
        //
        Point2D p10=new Point2D.Float(p5x,p5y);
        Point2D p11=new Point2D.Float(p6x,p6y);
        Point2D p20=new Point2D.Float(p7x,p7y);
        Point2D p21=new Point2D.Float(p8x,p8y);
        //
        Point2D p30=new Point2D.Float(p3x,p3y);
        Point2D p31=new Point2D.Float(p4x,p4y);
        
		List<Pair<Point2D, Point2D>> bandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();		
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p00, p01));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p10, p11));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p20, p21));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p30, p31));

		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		bandRenderer.renderComplexBand(gl, bandConnectionPoints, false,bColor.getRGB(), 0.5f);		
		gl.glDisable(GL.GL_STENCIL_TEST);
		
		gl.glTranslatef(0f, 0f, -4f);
		
//        //Debug
//        gl.glBegin(GL2.GL_LINES);
//    		gl.glColor4f(1f, 0f, 0f, 1.0f);
//    		gl.glVertex3f( p1x, p1y, 4.0f);
//    		gl.glVertex3f( p2x, p2y, 4.0f);              
//    		
//    		gl.glColor4f(0f, 1f, 0f, 1.0f);
//    		gl.glVertex3f( p3x, p3y, 4.0f);
//    		gl.glVertex3f( p4x, p4y, 4.0f);              
//        gl.glEnd();
//        
//        gl.glPointSize(5);
//        gl.glBegin(GL2.GL_POINTS);       		
//    		//
//			gl.glColor4f(1f, 0f, 0f, 1.0f);
//			gl.glVertex3d( p00.getX(), p00.getY(), 4.0f);
//			gl.glVertex3d( p01.getX(), p01.getY(), 4.0f);              
//
//			gl.glColor4f(0f, 1f, 0f, 1.0f);
//			gl.glVertex3d( p10.getX(), p10.getY(), 4.0f);
//			gl.glVertex3d( p11.getX(), p11.getY(), 4.0f);  
//			
//			gl.glColor4f(0f, 0f, 1f, 1.0f);
//			gl.glVertex3d( p20.getX(), p20.getY(), 4.0f);
//			gl.glVertex3d( p21.getX(), p21.getY(), 4.0f);  
//			
//			gl.glColor4f(1f, 1f, 0f, 1.0f);
//			gl.glVertex3d( p30.getX(), p30.getY(), 4.0f);
//			gl.glVertex3d( p31.getX(), p31.getY(), 4.0f);  
//			//	
//        gl.glEnd();
//        gl.glPointSize(1);		


	}
	
	@Override
	protected void onMouseOver(Pick pick) {
		// TODO: reset fade timer
	}

	@Override
	protected void onClicked(Pick pick) {
		if (!isPathLink) {
			boolean promoted1 = promote(info1);
			boolean promoted2 = promote(info2);
			view.clearSelectedPortalLinks();
			view.setCurrentPortalVertexRep(null);
			if (promoted1) {
				addSelectedLinks(vertexRep2, info1.pathway);
			} else if (promoted2) {
				addSelectedLinks(vertexRep1, info2.pathway);
			} else {
				view.addSelectedPortalLink(vertexRep1, vertexRep2);
			}
			ShowNodeContextEvent event = new ShowNodeContextEvent(vertexRep1);
			event.setEventSpace(view.getPathEventSpace());
			EventPublisher.INSTANCE.triggerEvent(event);
		}
	}

	private void addSelectedLinks(PathwayVertexRep vertexRep, PathwayGraph pathway) {
		Set<PathwayVertexRep> vertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(vertexRep, pathway);
		for (PathwayVertexRep v : vertexReps) {
			view.addSelectedPortalLink(vertexRep, v);
		}
	}

	private boolean promote(PathwayMultiFormInfo info) {
		if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL3
				|| info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL4) {
			info.multiFormRenderer.setActive(info.embeddingIDToRendererIDs.get(EEmbeddingID.PATHWAY_LEVEL2).get(0));
			info.age = GLSubGraph.currentPathwayAge--;
			view.lastUsedRenderer = info.multiFormRenderer;
			return true;
		} else if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
			view.lastUsedLevel1Renderer = info.multiFormRenderer;
		}
		return false;
	}
}