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
	private ColoredConnectionBandRenderer bandRenderer = null;

	public LinkRenderer(GLSubGraph view, boolean drawLink, Rectangle2D loc1, Rectangle2D loc2,
			PathwayMultiFormInfo info1, PathwayMultiFormInfo info2, float stubSize, boolean isLocation1Window,
			boolean isLocation2Window, boolean isContextLink, boolean isPathLink, PathwayVertexRep vertexRep1,
			PathwayVertexRep vertexRep2, ColoredConnectionBandRenderer newBandRenderer) {
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
			g.color(PortalRenderStyle.PATH_LINK_COLOR);
		} else if (isContextLink) {
			g.color(PortalRenderStyle.CONTEXT_PORTAL_COLOR);
		} else {
			g.color(PortalRenderStyle.DEFAULT_PORTAL_COLOR);
		}
		g.lineWidth(2);

		if (!drawLink) {
			Vec2f direction = new Vec2f((float) loc1.getCenterX() - (float) loc2.getCenterX(),
					(float) loc1.getCenterY() - (float) loc2.getCenterY());
			direction.normalize();
			if (!isLocation1Window) {
				Vec2f stub1End = new Vec2f((float) loc1.getCenterX() - 60 * direction.x() * stubSize,
						(float) loc1.getCenterY() - 60 * direction.y() * stubSize);
				 g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y());
				//drawStub(g.gl, (float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y(),
				//		(float) loc1.getHeight(), (float) loc1.getHeight());
				// drawLink(g.gl,
				// (float) loc1.getCenterX(), (float) loc1.getCenterY(),
				// (float) loc2.getCenterX(),(float) loc2.getCenterY(),
				// (float) loc1.getHeight(),(float) loc2.getHeight());
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
		
		g.lineWidth(1);
//		g.gl.glEnable(GL2.GL_BLEND);
//		g.gl.glEnable(GL2.GL_LINE_SMOOTH);
//		g.gl.glBlendFunc(GL2.GL_SRC_ALPHA_SATURATE, GL2.GL_ONE) ;
//		if (!isLocation1Window)
//			g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
//		if (!isLocation2Window)
//			g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
//		g.lineWidth(1);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
		g.incZ(-0.5f);
	}

	private Vec2f rotateVec2(Vec2f vec, float angle) {
		Vec2f val = new Vec2f();
		val.setX((float) ((vec.get(0) * Math.cos(angle)) - (vec.get(1) * Math.sin(angle))));
		val.setY((float) ((vec.get(0) * Math.sin(angle)) + (vec.get(1) * Math.cos(angle))));
		return val;
	}

	public void drawLink(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth) {

	}

	// /////////////////////
	// /////////////////////
	public void drawStub(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth) {

		//drawStubFromCenter(gl, xS,yS, xE, yE, startWidth, endWidth);
		drawStubRBorder(gl, xS,yS, xE, yE, startWidth, endWidth);
		//drawStubAsFreeFormRound(gl, xS,yS, xE, yE, startWidth, endWidth);
	}

	
	private void  drawStubRBorder(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth)
	{
		
		// START POINTS
		//float glxS = xS;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
		//float glyS = yS;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));
		
//		float glxS = (float)loc1.getX()+(float)loc1.getWidth();;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
//		float glyS = (float)loc1.getY()+((float)loc1.getHeight())/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));


		
		float glxS = (float)loc1.getX()+(float)loc1.getWidth();
		float glyS = (float)loc1.getY()+((float)loc1.getHeight()/2.0f);


		Vec2f dirNorm = new Vec2f(xE - glxS, yE - glyS);
		float length = (float)loc1.getWidth()/1.5f;//dirNorm.length();
		dirNorm.normalize();
		Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
		normalVec.normalize();
		// BAND WIDTH START
		float glOffsetX = normalVec.get(0) * startWidth/1.5f;//Math.abs(p2x-p1x)/2.0f; //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * Math.abs(p2x-p1x)  ));
		float glOffsetY = normalVec.get(1) * startWidth/1.5f;//Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * Math.abs(p2y-p1y)  ));
		// BAND WIDTH END
//		float glEndOffsetX = pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * endWidth));
//		float glEndOffsetY = pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * endWidth));
		//
//		float p1x = glxS - (glStartOffsetX);//		
//		float p1y = glyS - (glStartOffsetY);//
//		float p2x = glxS + (glStartOffsetX);//
//		float p2y = glyS + (glStartOffsetY);//
//		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//		
//		float p2y = (float)loc1.getY();//
//		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//
//		float p1y = (float)loc1.getY()+((float)loc1.getHeight());//		
//		float p1x = glxS - (glStartOffsetX);//		
//		float p1y = glyS - (glStartOffsetY);//
//		float p2x = glxS + (glStartOffsetX);//
//		float p2y = glyS + (glStartOffsetY);//
		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//		
		float p1y = (float)loc1.getY();//
		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//
		float p2y = (float)loc1.getY()+((float)loc1.getHeight());//
		// END POINTS
		float glxE = glxS + (dirNorm.get(0) * (length));//pxlConverter.getGLWidthForPixelWidth(Math.round(xE));
		float glyE = glyS + (dirNorm.get(1) * (length));//pxlConverter.getGLHeightForPixelHeight(Math.round(yE));
		
		float p3x = glxS+ (dirNorm.get(0) * (length))- (glOffsetX /1.0f);//
		float p3y = glyS+ (dirNorm.get(1) * (length))- (glOffsetY /1.0f);//
		float p4x = glxS+ (dirNorm.get(0) * (length))+ (glOffsetX /1.0f);//
		float p4y = glyS+ (dirNorm.get(1) * (length))+ (glOffsetY /1.0f);//
		
		// MID POINTS
		float p5x = glxS + (dirNorm.get(0) * (length/ 3.0f))- (glOffsetX /1.0f);//
		float p5y = glyS + (dirNorm.get(1) * (length / 3.0f))- (glOffsetY /1.0f);//		
		float p6x = glxS + (dirNorm.get(0) * (length / 3.0f))+ (glOffsetX /1.0f);//
		float p6y = glyS + (dirNorm.get(1) * (length / 3.0f))+ (glOffsetY /1.0f);//
		//
		float p7x =  glxS + (2f * dirNorm.get(0) * (length / 3.0f))- (glOffsetX /1.0f);//
		float p7y =  glyS + (2f * dirNorm.get(1) * (length / 3.0f))- (glOffsetY /1.0f);//
		float p8x =  glxS + (2f * dirNorm.get(0) * (length / 3.0f))+ (glOffsetX /1.0f);//
		float p8y =  glyS + (2f * dirNorm.get(1) * (length / 3.0f))+ (glOffsetY /1.0f);//

		gl.glTranslatef(0f, 0f, 4f);

		// float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, 1 };
		// float[] rightTopPos = new float[] { p3x, p3y };
		// float[] rightBottomPos = new float[] { p4x, p4y };
		// float[] leftTopPos = new float[] { p1x, p1y };
		// float[] leftBottomPos = new float[] { p2x, p2y };
		// float offsetX = 0.0f;//this.x * 0.4f;
		// bandRenderer.renderSingleBand(gl,
		// leftTopPos, leftBottomPos,
		// rightTopPos, rightBottomPos,
		// false, offsetX, 0,bandColor);
		//
		Point2D p00 = new Point2D.Float(p1x, p1y);
		Point2D p01 = new Point2D.Float(p2x, p2y);
		//
		Point2D p10 = new Point2D.Float(p5x, p5y);
		Point2D p11 = new Point2D.Float(p6x, p6y);
		Point2D p20 = new Point2D.Float(p7x, p7y);
		Point2D p21 = new Point2D.Float(p8x, p8y);
		//
		Point2D p30 = new Point2D.Float(p3x, p3y);
		Point2D p31 = new Point2D.Float(p4x, p4y);

		List<Pair<Point2D, Point2D>> bandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p00, p01));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p10, p11));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p20, p21));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p30, p31));

		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glColorMask(false, false, false, false);		
		gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
		//generate stencil mask			
		double rx=loc1.getX();
		double ry=loc1.getY();
		double rz=5.0;
		double rw=loc1.getWidth();
		double rh=loc1.getHeight();		
		gl.glBegin(GL2.GL_POLYGON );
			gl.glVertex3d(rx, ry, rz);
			gl.glVertex3d(rx + rw, ry, rz);
			gl.glVertex3d(rx + rw, ry + rh, rz);
			gl.glVertex3d(rx, ry + rh, rz);
		gl.glEnd();
		
		//render stub
		Color bColor = new Color(0.4f, 0.4f, 0.4f, 1f);
		gl.glColorMask(true, true, true, true);		
		gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		bandRenderer.renderComplexBand(gl, bandConnectionPoints, false, bColor.getRGBA(), 0.5f,true);
		float rgb[] = bColor.getRGB();
		 gl.glBegin(GL2.GL_LINES);
		 	//gl.glColor4f(rgb[0],rgb[1], rgb[2], 1.0f);
		 	gl.glColor4f(1f,0f, 0f, 1.0f);
			 gl.glVertex3f( glxS, glyS, 10.0f);
			 gl.glVertex3f( glxE, glyE, 10.0f);
		 gl.glEnd();
		gl.glEnable(GL.GL_DEPTH_TEST);	
		gl.glDisable(GL.GL_STENCIL_TEST);
//		//DEBUG


		
		gl.glPointSize(3);
		 gl.glBegin(GL2.GL_POINTS);
			 gl.glColor4f(1f, 0f, 0f, 1.0f);
			 gl.glVertex3f( p1x, p1y, 5.0f);
			 gl.glVertex3f( p2x, p2y, 5.0f);
			 
			 gl.glColor4f(0f, 1f, 0f, 1.0f);
			 gl.glVertex3f( p3x, p3y, 5.0f);
			 gl.glVertex3f( p4x, p4y, 5.0f);

			 gl.glColor4f(0f, 0f, 1f, 1.0f);
			 gl.glVertex3f( p5x, p5y, 5.0f);
			 gl.glVertex3f( p6x, p6y, 5.0f);

			 
			 gl.glColor4f(1f, 0f, 1f, 1.0f);
			 gl.glVertex3f( p7x, p7y, 5.0f);
			 gl.glVertex3f( p8x, p8y, 5.0f);
			 
			 gl.glColor4f(0f, 1f, 1f, 1.0f);
			 gl.glVertex3f( glxS, glyS, 5.0f);
			 gl.glVertex3f( glxE, glyE, 5.0f);
			 
		 gl.glEnd();
		 gl.glPointSize(1);
		 
		//clean up
		
			
		gl.glTranslatef(0f, 0f, -4f);
	}

	
	
//	private void  drawStubRBorder(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth)
//	{
//		
//		// START POINTS
//		//float glxS = xS;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
//		//float glyS = yS;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));
//		
////		float glxS = (float)loc1.getX()+(float)loc1.getWidth();;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
////		float glyS = (float)loc1.getY()+((float)loc1.getHeight())/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));
//
//
//		
//		float glxS = (float)loc1.getX()+(float)loc1.getWidth();
//		float glyS = (float)loc1.getY()+((float)loc1.getHeight()/2.0f);
//
//
//		Vec2f dirNorm = new Vec2f(xE - glxS, yE - glyS);
//		float length = (float)loc1.getWidth();//dirNorm.length();
//		dirNorm.normalize();
//		Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
//		// BAND WIDTH START
//		float glStartOffsetX = normalVec.get(0) * startWidth/2.0f;//Math.abs(p2x-p1x)/2.0f; //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * Math.abs(p2x-p1x)  ));
//		float glStartOffsetY = normalVec.get(1) * startWidth/2.0f;//Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * Math.abs(p2y-p1y)  ));
//		// BAND WIDTH END
////		float glEndOffsetX = pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * endWidth));
////		float glEndOffsetY = pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * endWidth));
//		//
////		float p1x = glxS - (glStartOffsetX);//		
////		float p1y = glyS - (glStartOffsetY);//
////		float p2x = glxS + (glStartOffsetX);//
////		float p2y = glyS + (glStartOffsetY);//
////		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//		
////		float p2y = (float)loc1.getY();//
////		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//
////		float p1y = (float)loc1.getY()+((float)loc1.getHeight());//		
////		float p1x = glxS - (glStartOffsetX);//		
////		float p1y = glyS - (glStartOffsetY);//
////		float p2x = glxS + (glStartOffsetX);//
////		float p2y = glyS + (glStartOffsetY);//
//		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//		
//		float p1y = (float)loc1.getY();//
//		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//
//		float p2y = (float)loc1.getY()+((float)loc1.getHeight());//
//		// END POINTS
//		float glxE = glxS + (dirNorm.get(0) * (length));//pxlConverter.getGLWidthForPixelWidth(Math.round(xE));
//		float glyE = glyS + (dirNorm.get(1) * (length));//pxlConverter.getGLHeightForPixelHeight(Math.round(yE));
//		float p3x = p1x+ (dirNorm.get(0) * (length));// + (glStartOffsetX /1.0f);//
//		float p3y = p1y+ (dirNorm.get(1) * (length));// - (glStartOffsetY /1.0f);//
//		float p4x = p2x+ (dirNorm.get(0) * (length));// + (glStartOffsetX /1.0f);//
//		float p4y = p2y+ (dirNorm.get(1) * (length));// + (glStartOffsetY /1.0f);//
//		// MID POINTS
//		float p5x = p1x + (dirNorm.get(0) * (length/ 3.0f));// - (glStartOffsetX / 1.f);//
//		float p5y = p1y + (dirNorm.get(1) * (length / 3.0f));// - (glStartOffsetY / 1.f);//
//		
//		float p6x = p2x + (dirNorm.get(0) * (length / 3.0f));// + (glStartOffsetX / 1f);//
//		float p6y = p2y + (dirNorm.get(1) * (length / 3.0f));// + (glStartOffsetY / 1f);//
//		//
//		float p7x =  p1x + (2f * dirNorm.get(0) * (length / 3.0f));// - (glStartOffsetX / 1f);//
//		float p7y =  p1y + (2f * dirNorm.get(1) * (length / 3.0f));// - (glStartOffsetY / 1f);//
//		float p8x =  p2x + (2f * dirNorm.get(0) * (length / 3.0f));// + (glStartOffsetX / 1.0f);//
//		float p8y =  p2y + (2f * dirNorm.get(1) * (length / 3.0f));// + (glStartOffsetY / 1.0f);//
//
//		gl.glTranslatef(0f, 0f, 4f);
//
//		// float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, 1 };
//		// float[] rightTopPos = new float[] { p3x, p3y };
//		// float[] rightBottomPos = new float[] { p4x, p4y };
//		// float[] leftTopPos = new float[] { p1x, p1y };
//		// float[] leftBottomPos = new float[] { p2x, p2y };
//		// float offsetX = 0.0f;//this.x * 0.4f;
//		// bandRenderer.renderSingleBand(gl,
//		// leftTopPos, leftBottomPos,
//		// rightTopPos, rightBottomPos,
//		// false, offsetX, 0,bandColor);
//		//
//		Point2D p00 = new Point2D.Float(p1x, p1y);
//		Point2D p01 = new Point2D.Float(p2x, p2y);
//		//
//		Point2D p10 = new Point2D.Float(p5x, p5y);
//		Point2D p11 = new Point2D.Float(p6x, p6y);
//		Point2D p20 = new Point2D.Float(p7x, p7y);
//		Point2D p21 = new Point2D.Float(p8x, p8y);
//		//
//		Point2D p30 = new Point2D.Float(p3x, p3y);
//		Point2D p31 = new Point2D.Float(p4x, p4y);
//
//		List<Pair<Point2D, Point2D>> bandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
//		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p00, p01));
//		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p10, p11));
//		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p20, p21));
//		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p30, p31));
//
//		gl.glEnable(GL.GL_STENCIL_TEST);
//		gl.glDisable(GL.GL_DEPTH_TEST);
//		gl.glColorMask(false, false, false, false);		
//		gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
//		gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
//		//generate stencil mask			
//		double rx=loc1.getX();
//		double ry=loc1.getY();
//		double rz=5.0;
//		double rw=loc1.getWidth();
//		double rh=loc1.getHeight();		
//		gl.glBegin(GL2.GL_POLYGON );
//			gl.glVertex3d(rx, ry, rz);
//			gl.glVertex3d(rx + rw, ry, rz);
//			gl.glVertex3d(rx + rw, ry + rh, rz);
//			gl.glVertex3d(rx, ry + rh, rz);
//		gl.glEnd();
//		
//		//render stub
//		Color bColor = new Color(0.4f, 0.4f, 0.4f, 1f);
//		gl.glColorMask(true, true, true, true);		
//		gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
//		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
//		bandRenderer.renderComplexBand(gl, bandConnectionPoints, false, bColor.getRGBA(), 0.5f,true);
////		//DEBUG
//		float rgb[] = bColor.getRGB();
//		 gl.glBegin(GL2.GL_LINES);
//		 gl.glColor4f(rgb[0],rgb[1], rgb[2], 1.0f);
//		 gl.glVertex3f( glxS, glyS, 4.0f);
//		 gl.glVertex3f( glxE, glyE, 4.0f);
//		 gl.glEnd();
//
//		
//		gl.glPointSize(3);
//		 gl.glBegin(GL2.GL_POINTS);
//			 gl.glColor4f(1f, 0f, 0f, 1.0f);
//			 gl.glVertex3f( p1x, p1y, 5.0f);
//			 gl.glVertex3f( p2x, p2y, 5.0f);
//			 
//			 gl.glColor4f(0f, 1f, 0f, 1.0f);
//			 gl.glVertex3f( p3x, p3y, 5.0f);
//			 gl.glVertex3f( p4x, p4y, 5.0f);
//
//			 gl.glColor4f(0f, 0f, 1f, 1.0f);
//			 gl.glVertex3f( p5x, p5y, 5.0f);
//			 gl.glVertex3f( p6x, p6y, 5.0f);
//
//			 
//			 gl.glColor4f(1f, 0f, 1f, 1.0f);
//			 gl.glVertex3f( p7x, p7y, 5.0f);
//			 gl.glVertex3f( p8x, p8y, 5.0f);
//
//		 gl.glEnd();
//		 gl.glPointSize(1);
//		 
//		//clean up
//		gl.glDisable(GL.GL_STENCIL_TEST);
//		gl.glEnable(GL.GL_DEPTH_TEST);		
//		gl.glTranslatef(0f, 0f, -4f);
//	}

	private void  drawStubFromCenter(GL2 gl, float xS, float yS, float xE, float yE, float startWidth, float endWidth)
	{
		
		// START POINTS
		float glxS = xS;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
		float glyS = yS;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));
		
//		float glxS = (float)loc1.getX()+(float)loc1.getWidth();;//xlConverter.getGLWidthForPixelWidth(Math.round(xS));
//		float glyS = (float)loc1.getY()+((float)loc1.getHeight())/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(yS));


		
//		float glxS = (float)loc1.getX()+(float)loc1.getWidth();
//		float glyS = (float)loc1.getY()+((float)loc1.getHeight()/2.0f);


		Vec2f dirNorm = new Vec2f(xE - glxS, yE - glyS);
		float length = (float)loc1.getWidth();//dirNorm.length();
		dirNorm.normalize();
		Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
		// BAND WIDTH START
		float glStartOffsetX = normalVec.get(0) * startWidth/2.0f;//Math.abs(p2x-p1x)/2.0f; //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * Math.abs(p2x-p1x)  ));
		float glStartOffsetY = normalVec.get(1) * startWidth/2.0f;//Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * Math.abs(p2y-p1y)  ));
		// BAND WIDTH END
//		float glEndOffsetX = pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * endWidth));
//		float glEndOffsetY = pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * endWidth));
		//
//		float p1x = glxS - (glStartOffsetX);//		
//		float p1y = glyS - (glStartOffsetY);//
//		float p2x = glxS + (glStartOffsetX);//
//		float p2y = glyS + (glStartOffsetY);//
//		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//		
//		float p2y = (float)loc1.getY();//
//		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//
//		float p1y = (float)loc1.getY()+((float)loc1.getHeight());//		
		float p1x = glxS - (glStartOffsetX);//		
		float p1y = glyS - (glStartOffsetY);//
		float p2x = glxS + (glStartOffsetX);//
		float p2y = glyS + (glStartOffsetY);//
//		float p1x = (float)loc1.getX()+(float)loc1.getWidth();//		
//		float p1y = (float)loc1.getY();//
//		float p2x = (float)loc1.getX()+(float)loc1.getWidth();//
//		float p2y = (float)loc1.getY()+((float)loc1.getHeight());//
		// END POINTS
		float glxE =  glxS + (dirNorm.get(0) * (length));//pxlConverter.getGLWidthForPixelWidth(Math.round(xE));
		float glyE = glyS + (dirNorm.get(1) * (length));//pxlConverter.getGLHeightForPixelHeight(Math.round(yE));
		float p3x = glxE - (glStartOffsetX /1.0f);//
		float p3y = glyE - (glStartOffsetY /1.0f);//
		float p4x = glxE + (glStartOffsetX /1.0f);//
		float p4y = glyE + (glStartOffsetY /1.0f);//
		// MID POINTS
		float p5x = glxS + (dirNorm.get(0) * (length / 3.0f)) - (glStartOffsetX / 1.f);//
		float p5y = glyS + (dirNorm.get(1) * (length / 3.0f)) - (glStartOffsetY / 1.f);//
		
		float p6x = glxS + (dirNorm.get(0) * (length / 3.0f)) + (glStartOffsetX / 1f);//
		float p6y = glyS + (dirNorm.get(1) * (length / 3.0f)) + (glStartOffsetY / 1f);//
		//
		float p7x = glxS + (2f * dirNorm.get(0) * (length / 3.0f)) - (glStartOffsetX / 1f);//
		float p7y = glyS + (2f * dirNorm.get(1) * (length / 3.0f)) - (glStartOffsetY / 1f);//
		float p8x = glxS + (2f * dirNorm.get(0) * (length / 3.0f)) + (glStartOffsetX / 1.0f);//
		float p8y = glyS + (2f * dirNorm.get(1) * (length / 3.0f)) + (glStartOffsetY / 1.0f);//

		gl.glTranslatef(0f, 0f, 4f);

		// float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, 1 };
		// float[] rightTopPos = new float[] { p3x, p3y };
		// float[] rightBottomPos = new float[] { p4x, p4y };
		// float[] leftTopPos = new float[] { p1x, p1y };
		// float[] leftBottomPos = new float[] { p2x, p2y };
		// float offsetX = 0.0f;//this.x * 0.4f;
		// bandRenderer.renderSingleBand(gl,
		// leftTopPos, leftBottomPos,
		// rightTopPos, rightBottomPos,
		// false, offsetX, 0,bandColor);
		//
		Point2D p00 = new Point2D.Float(p1x, p1y);
		Point2D p01 = new Point2D.Float(p2x, p2y);
		//
		Point2D p10 = new Point2D.Float(p5x, p5y);
		Point2D p11 = new Point2D.Float(p6x, p6y);
		Point2D p20 = new Point2D.Float(p7x, p7y);
		Point2D p21 = new Point2D.Float(p8x, p8y);
		//
		Point2D p30 = new Point2D.Float(p3x, p3y);
		Point2D p31 = new Point2D.Float(p4x, p4y);

		List<Pair<Point2D, Point2D>> bandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p00, p01));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p10, p11));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p20, p21));
		bandConnectionPoints.add(new Pair<Point2D, Point2D>(p30, p31));

		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glColorMask(false, false, false, false);		
		gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
		//generate stencil mask			
		double rx=loc1.getX();
		double ry=loc1.getY();
		double rz=5.0;
		double rw=loc1.getWidth();
		double rh=loc1.getHeight();		
		gl.glBegin(GL2.GL_POLYGON );
			gl.glVertex3d(rx, ry, rz);
			gl.glVertex3d(rx + rw, ry, rz);
			gl.glVertex3d(rx + rw, ry + rh, rz);
			gl.glVertex3d(rx, ry + rh, rz);
		gl.glEnd();
		
		//render stub
		Color bColor = new Color(0.4f, 0.4f, 0.4f, 0.4f);
		gl.glColorMask(true, true, true, true);		
		gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		bandRenderer.renderComplexBand(gl, bandConnectionPoints, false, bColor.getRGBA(), 0.5f,false);
//		//DEBUG
		 gl.glBegin(GL2.GL_LINES);
		 gl.glColor4f(1f, 0f, 0f, 1.0f);
		 gl.glVertex3f( glxS, glyS, 4.0f);
		 gl.glVertex3f( glxE, glyE, 4.0f);
		 gl.glEnd();

		
//		gl.glPointSize(5);
//		 gl.glBegin(GL2.GL_POINTS);
//			 gl.glColor4f(1f, 0f, 0f, 1.0f);
//			 gl.glVertex3f( p1x, p1y, 5.0f);
//			 gl.glVertex3f( p2x, p2y, 5.0f);
//			 
//			 gl.glColor4f(0f, 1f, 0f, 1.0f);
//			 gl.glVertex3f( p3x, p3y, 5.0f);
//			 gl.glVertex3f( p4x, p4y, 5.0f);
//
//			 gl.glColor4f(0f, 0f, 1f, 1.0f);
//			 gl.glVertex3f( p5x, p5y, 5.0f);
//			 gl.glVertex3f( p6x, p6y, 5.0f);
//
//			 
//			 gl.glColor4f(1f, 0f, 1f, 1.0f);
//			 gl.glVertex3f( p7x, p7y, 5.0f);
//			 gl.glVertex3f( p8x, p8y, 5.0f);
//
//		 gl.glEnd();
//		 gl.glPointSize(5);
		 
		//clean up
		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glEnable(GL.GL_DEPTH_TEST);		
		gl.glTranslatef(0f, 0f, -4f);
	}
	
	//////////////

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