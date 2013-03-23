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

import java.awt.geom.Line2D;
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
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
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
		if(loc1.getX()>loc2.getX() && loc1.getX()+loc1.getWidth()>loc2.getX()+loc2.getWidth()){
			this.loc2 = loc1;
			this.loc1 = loc2;
		}else{
			this.loc1 = loc1;
			this.loc2 = loc2;			
		}
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
				
				//renderStraightLink(g.gl, false);
				renderStraightLinkStableWidth_left2right(g.gl, false);
//				Vec2f stub1End = new Vec2f((float) loc1.getCenterX() - 60 * direction.x() * stubSize,
//						(float) loc1.getCenterY() - 60 * direction.y() * stubSize);
//				 g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y());
				//drawStub(g.gl, (float) loc1.getCenterX(), (float) loc1.getCenterY(), stub1End.x(), stub1End.y(),
				//		(float) loc1.getHeight(), (float) loc1.getHeight());
				// drawLink(g.gl,
				// (float) loc1.getCenterX(), (float) loc1.getCenterY(),
				// (float) loc2.getCenterX(),(float) loc2.getCenterY(),
				// (float) loc1.getHeight(),(float) loc2.getHeight());
			}
			if (!isLocation2Window) {
				
//				Vec2f stub2End = new Vec2f((float) loc2.getCenterX() + 20 * direction.x() * stubSize,
//						(float) loc2.getCenterY() + 20 * direction.y() * stubSize);
//				g.drawLine((float) loc2.getCenterX(), (float) loc2.getCenterY(), stub2End.x(), stub2End.y());
			}
		} else {
			//renderStraightLink(g.gl, true);
			renderStraightLinkStableWidth_left2right(g.gl, true);
//			g.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), (float) loc2.getCenterX(),
//					(float) loc2.getCenterY());
		}

		g.lineWidth(1);
//		if (!isLocation1Window)
//			g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
//		if (!isLocation2Window)
//			g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
//		g.lineWidth(1);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
		// // g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
		g.incZ(-0.5f);

	}
	

	protected void renderStraightLinkStableWidth_left2right(GL2 gl, boolean showConnection)
	{
		 float stubDistance=0.0f;
		 float bandWidth=0.0f;
		 float stubConnectionPoint1_X=0.0f;	
		 float stubConnectionPoint1_Y=0.0f;
		 float stubConnectionPoint2_X=0.0f;
		 float stubConnectionPoint2_Y=0.0f;
//		 float stubConnectionPoint3_X=0.0f;	
//		 float stubConnectionPoint3_Y=0.0f;
//		 float stubConnectionPoint4_X=0.0f;
//		 float stubConnectionPoint4_Y=0.0f;
		 float xS = 0.0f;
		 float yS = 0.0f;
		 float xE = 0.0f;
		 float yE = 0.0f;
		 		 
		 Color c = new Color("078600");
		 
		 float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, .4f };	
		 float[] bandColor2 = PortalRenderStyle.DEFAULT_PORTAL_COLOR.getRGBA(); //c.getRGBA();
		 //bandColor2[3]=0.4f;
		 
		 float borderOpacity=0.7f;
		 Vec2f dirNorm = null;
		 Point2D intersectionPoint =null;
//		 Point2D intersectionPoint2 =null;
		 float z=1.0f;
		//////////////////
		boolean fade=false;
		fade=true;
		
		boolean showCenterLine=false;
		//showCenterLine=true;

		
		stubDistance=(float)loc1.getWidth()/2.0f;
		float stubLenght=stubDistance;
		if(fade)
			stubLenght=stubLenght*2.0f;

		
		bandWidth=(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/4.0f;

		xS = (float)loc1.getX()+(float)loc1.getWidth();
		yS = (float)loc1.getY()+(float)loc1.getHeight()/2.0f;
		xE = (float)loc2.getX();
		yE = (float)loc2.getY()+(float)loc2.getHeight()/2.0f;
		
		dirNorm = new Vec2f(xE - xS, yE - yS);
		dirNorm.normalize();
		Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
		float glBandWidthOffsetX = normalVec.get(0) * bandWidth/2.0f;//Math.abs(p2x-p1x)/2.0f; //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * Math.abs(p2x-p1x)  ));
		float glBandWidthOffsetY = normalVec.get(1) * bandWidth/2.0f;//Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * Math.abs(p2y-p1y)  ));

//		final Line2D.Double line1=new Line2D.Double(xS,yS, xS+dirNorm.get(0)*stubDistance*4.0f,yS+dirNorm.get(1)*stubDistance*4.0f);
//		final Line2D.Double line2=new Line2D.Double(xS+stubDistance,yS-10.0f*stubDistance, xS+stubDistance,yS+10.0f*stubDistance);		
//		intersectionPoint= getIntersection(line1, line2);	
//		stubConnectionPoint1_X=(float)intersectionPoint.getX();
//		stubConnectionPoint2_Y=(float)intersectionPoint.getY()-bandWidth/2.0f;
//		stubConnectionPoint2_X=(float)intersectionPoint.getX();
//		stubConnectionPoint1_Y=(float)intersectionPoint.getY()+bandWidth/2.0f;
		
	

		
//		final Line2D.Double line3=new Line2D.Double(xE,yE, xS-dirNorm.get(0)*stubDistance*4.0f,yE-dirNorm.get(1)*stubDistance*4.0f);
//		final Line2D.Double line4=new Line2D.Double(xE-stubDistance,yE+10.0f*stubDistance, xE-stubDistance,yE-10.0f*stubDistance);		
//		intersectionPoint2= getIntersection(line1, line2);
//		stubConnectionPoint3_X=(float)intersectionPoint2.getX();
//		stubConnectionPoint4_Y=(float)intersectionPoint2.getY()-bandWidth/2.0f;
//		stubConnectionPoint4_X=(float)intersectionPoint2.getX();
//		stubConnectionPoint3_Y=(float)intersectionPoint2.getY()+bandWidth/2.0f;

		// init rendering 		
		gl.glLineWidth(1);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);	
		
		// render stub
		//left stub
		float p00X=xS;
		float p00Y=yS-(float)loc1.getHeight()/2.0f;//(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/2.0f;
		float p01X=xS;
		float p01Y=yS+(float)loc1.getHeight()/2.0f;//(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/2.0f;
		stubConnectionPoint1_X= xS + (dirNorm.get(0) * (stubLenght))-glBandWidthOffsetX;//;
		stubConnectionPoint1_Y= yS + (dirNorm.get(1) * (stubLenght))-glBandWidthOffsetY;//;
		stubConnectionPoint2_X= xS + (dirNorm.get(0) * (stubLenght))+glBandWidthOffsetX;//;
		stubConnectionPoint2_Y= yS + (dirNorm.get(1) * (stubLenght))+glBandWidthOffsetY;//;	
		//right stub
		float stubConnectorWidth=(((float)loc1.getHeight()*2.0f < (float)loc2.getHeight()) ? (float)loc1.getHeight() : (float)loc2.getHeight());
		float p10X=xE;
		float p10Y=yE- stubConnectorWidth/2.0f;;//(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/2.0f;
		float p11X=xE;
		float p11Y=yE+stubConnectorWidth/2.0f;//(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/2.0f;
		
		float stubConnectionPoint3_X=0f;
		float stubConnectionPoint3_Y=0f;
		float stubConnectionPoint4_X=0f;
		float stubConnectionPoint4_Y=0f;
		boolean renderWindowStub=true;
		if(this.info2.window.getAbsoluteLocation().get(0)==loc2.getX()){
			if(this.view.containsWindowStub(this.info2))
				renderWindowStub=false;
			
		//	bandColor = new float[] { 1.0f, 0.f, 0f, 1f };
			Vec2f dirToWindowCenter = new Vec2f(xE - this.info1.window.getAbsoluteLocation().get(0)-this.info1.window.getSize().get(0)/2.0f, yE - (this.info1.window.getAbsoluteLocation().get(1)+this.info1.window.getSize().get(1)/2.0f));
			dirToWindowCenter.normalize();
			Vec2f normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth/2.0f;//Math.abs(p2x-p1x)/2.0f; //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0) * Math.abs(p2x-p1x)  ));
			float glBandWidthOffsetY_CenterVec= normalVecCenterVec.get(1) * bandWidth/2.0f;//Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1) * Math.abs(p2y-p1y)  ));

			 stubConnectionPoint3_X= xE - (dirToWindowCenter.get(0) * (stubLenght))-glBandWidthOffsetX_CenterVec;//;
			 stubConnectionPoint3_Y= yE - (dirToWindowCenter.get(1) * (stubLenght))-glBandWidthOffsetY_CenterVec;//;
			 stubConnectionPoint4_X= xE - (dirToWindowCenter.get(0) * (stubLenght))+glBandWidthOffsetX_CenterVec;//;
			 stubConnectionPoint4_Y= yE - (dirToWindowCenter.get(1) * (stubLenght))+glBandWidthOffsetY_CenterVec;//;
			

				


		}else{
		 stubConnectionPoint3_X= xE - (dirNorm.get(0) * (stubLenght))-glBandWidthOffsetX;//;
		 stubConnectionPoint3_Y= yE - (dirNorm.get(1) * (stubLenght))-glBandWidthOffsetY;//;
		 stubConnectionPoint4_X= xE - (dirNorm.get(0) * (stubLenght))+glBandWidthOffsetX;//;
		 stubConnectionPoint4_Y= yE - (dirNorm.get(1) * (stubLenght))+glBandWidthOffsetY;//;
		}
		if(!showConnection){
    	   if(fade)
    	   {//////////// left 
	           gl.glBegin(GL2.GL_LINES);
		       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
		       		gl.glVertex3f(p00X,p00Y,z); 	
			    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);				    	
			    	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);		   
		    		    	
			    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);			    			    		
			    	gl.glVertex3f(p01X,p01Y,z);
			    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);
			    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);	    		    

			    	if(showCenterLine){
				    	gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
				    	gl.glVertex3f(xS, yS,z);
				    	gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
				    	gl.glVertex3f(xS + (dirNorm.get(0) * (stubLenght)),yS + (dirNorm.get(1) * (stubLenght)),z);
			    	}
		    	gl.glEnd();			
		        gl.glBegin(GL2.GL_QUADS);
		        	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
		    		gl.glVertex3f(p00X,p00Y,z);
		    		gl.glVertex3f(p01X,p01Y,z);
		    		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);
		    		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		    		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		        gl.glEnd();
		        
		        //////////// right
		        if(renderWindowStub){		        		
		           gl.glBegin(GL2.GL_LINES);
			       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
			       		gl.glVertex3f(p10X,p10Y,z); 	
				    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);				    	
				    	gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);		   
	//		    		    	
				    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);			    			    		
				    	gl.glVertex3f(p11X,p11Y,z);
				    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);
				    	gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);	    		    
	
				    	if(showCenterLine){
					    	gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
					    	gl.glVertex3f(xE, yE,z);
					    	gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
					    	gl.glVertex3f(xE - (dirNorm.get(0) * (stubLenght)),yE - (dirNorm.get(1) * (stubLenght)),z);
				    	}
			    	gl.glEnd();			
			        gl.glBegin(GL2.GL_QUADS);
			        	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
			    		gl.glVertex3f(p10X,p10Y,z);
			    		gl.glVertex3f(p11X,p11Y,z);
			    		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);
			    		
			    		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
			    		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
			        gl.glEnd();
		        }
	    	}
//    	   else{ // NO FADING
//	   			//stub
//		           gl.glBegin(GL2.GL_LINES);
//			       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
//			       		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
//				    	gl.glVertex3f(p00X,p00Y,z);
//				    		    	
//				    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);				    				    	
//				    	gl.glVertex3f(p01X,p01Y,z);	    	
//				    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);	  
//				    	
//				    	if(showCenterLine){
//					    	gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
//					    	gl.glVertex3f(xS, yS,z);
//					    	gl.glVertex3f(xS + (dirNorm.get(0) * (stubDistance)),yS + (dirNorm.get(1) * (stubDistance)),z);
//				    	}
//			    	gl.glEnd();
//				
//			        gl.glBegin(GL2.GL_QUADS);
//			        gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
//			    		gl.glVertex3f(p00X,p00Y,z);
//			    		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
//			    		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
//			    		gl.glVertex3f(p01X,p01Y,z);
//			    		 //gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);	
//			        	
//			        	
//			        gl.glEnd();
//    	   }
		}
		else{
			//stub
	           gl.glBegin(GL2.GL_LINES);
	       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
	       		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    	gl.glVertex3f(p00X,p00Y,z);		    		    	
		    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);			    			    
		    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    		
		    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		    	gl.glVertex3f(p01X,p01Y,z);
		    	
//		    	//gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
//		    	gl.glVertex3f(xS, yS,z);
//		    	//gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
//		    	gl.glVertex3f((float)intersectionPoint.getX(),(float)intersectionPoint.getY(),z);	  
		    	gl.glEnd();
			
		        gl.glBegin(GL2.GL_QUADS);
		        gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
		        gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		    		gl.glVertex3f(p00X,p00Y,z);
		    		gl.glVertex3f(p01X,p01Y,z);
		    		 //gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);	
		        	
		        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		        gl.glEnd();
		        //////////// right 
		           gl.glBegin(GL2.GL_LINES);
			       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
			       		gl.glVertex3f(p10X,p10Y,z); 	
				    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);				    	
				    	gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);		   
//			    		    	
				    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);			    			    		
				    	gl.glVertex3f(p11X,p11Y,z);
				    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);
				    	gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);	    		    

				    	if(showCenterLine){
					    	gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
					    	gl.glVertex3f(xE, yE,z);
					    	gl.glVertex3f(xE - (dirNorm.get(0) * (stubLenght)),yE - (dirNorm.get(1) * (stubLenght)),z);
				    	}
			    	gl.glEnd();			
			        gl.glBegin(GL2.GL_QUADS);
			        	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
			    		gl.glVertex3f(p10X,p10Y,z);
			    		gl.glVertex3f(p11X,p11Y,z);			    				    
			    		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
			    		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
			        gl.glEnd();
			//outline
			gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 1.0f);	
	        gl.glBegin(GL2.GL_LINES);
	        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);	
	        	gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
			        
//		    	//gl.glVertex3f((float)loc2.getX(),(float)loc2.getY(),z);	    	
    
//		    	//gl.glVertex3f((float)loc2.getX(),(float)loc2.getY()+bandWidth/2.0f,z);
//		    	
		    	gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
		    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
	    	gl.glEnd();
			
	    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], bandColor[3]);	
	        gl.glBegin(GL2.GL_QUADS);
	        	
	        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);	        	  	
	        	gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
	        	gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
//	        	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY(),z);
//	        	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY()+(bandWidth/2.0f),z);
	        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		    gl.glEnd();
		}
			
			
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	}

	
	
//	if((float)loc1.getX()>(float)loc2.getX()){
//		Rectangle2D tmp=loc1;
//		loc1=loc2;
//		loc2=tmp;
//	}
//	
	protected void renderStraightLink(GL2 gl, boolean showConnection)
	{
		 float stubDistance=0.0f;
		 float bandWidth=0.0f;
		 float stubConnectionPoint1_X=0.0f;	
		 float stubConnectionPoint1_Y=0.0f;
		 float stubConnectionPoint2_X=0.0f;
		 float stubConnectionPoint2_Y=0.0f;
		 float stubConnectionPoint3_X=0.0f;	
		 float stubConnectionPoint3_Y=0.0f;
		 float stubConnectionPoint4_X=0.0f;
		 float stubConnectionPoint4_Y=0.0f;
		 float xS = 0.0f;
		 float yS = 0.0f;
		 float xE = 0.0f;
		 float yE = 0.0f;
		 float[] bandColor = new float[] { 0.4f, 0.4f, 0.4f, .4f };	
		 float borderOpacity=0.7f;
		 Vec2f dirNorm = null;
		 Point2D intersectionPoint =null;
		 Point2D intersectionPoint2 =null;
		 float z=1.0f;
		//////////////////
		stubDistance=(float)loc1.getWidth()/2.0f;
		bandWidth=(Math.min((float)loc1.getHeight(),(float)loc2.getHeight()))/2.0f;

		xS = (float)loc1.getX()+(float)loc1.getWidth();
		yS = (float)loc1.getY()+(float)loc1.getHeight()/2.0f;
		xE = (float)loc2.getX();
		yE = (float)loc2.getY()+(float)loc2.getHeight()/2.0f;
		
		dirNorm = new Vec2f(xE - xS, yE - yS);
		dirNorm.normalize();
		
		final Line2D.Double line1=new Line2D.Double(xS,yS, xS+dirNorm.get(0)*stubDistance*4.0f,yS+dirNorm.get(1)*stubDistance*4.0f);
		final Line2D.Double line2=new Line2D.Double(xS+stubDistance,yS-10.0f*stubDistance, xS+stubDistance,yS+10.0f*stubDistance);		
		intersectionPoint= getIntersection(line1, line2);
	
		stubConnectionPoint1_X=(float)intersectionPoint.getX();
		stubConnectionPoint2_Y=(float)intersectionPoint.getY()-bandWidth/2.0f;
		stubConnectionPoint2_X=(float)intersectionPoint.getX();
		stubConnectionPoint1_Y=(float)intersectionPoint.getY()+bandWidth/2.0f;
		
		final Line2D.Double line3=new Line2D.Double(xE,yE, xS-dirNorm.get(0)*stubDistance*4.0f,yE-dirNorm.get(1)*stubDistance*4.0f);
		final Line2D.Double line4=new Line2D.Double(xE-stubDistance,yE+10.0f*stubDistance, xE-stubDistance,yE-10.0f*stubDistance);		
		intersectionPoint2= getIntersection(line1, line2);
		stubConnectionPoint3_X=(float)intersectionPoint2.getX();
		stubConnectionPoint4_Y=(float)intersectionPoint2.getY()-bandWidth/2.0f;
		stubConnectionPoint4_X=(float)intersectionPoint2.getX();
		stubConnectionPoint3_Y=(float)intersectionPoint2.getY()+bandWidth/2.0f;

		// init rendering 		
		gl.glLineWidth(1);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);	
		
		// render stub
		float p00X=xS;
		float p00Y=yS-bandWidth/2.0f;
		float p01X=xS;
		float p01Y=yS+bandWidth/2.0f;
		
		

    		
		//boolean fade=false;
		boolean fade=true;

       if(!showConnection){
    	   if(fade){
	           gl.glBegin(GL2.GL_LINES);
	       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
		    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    	gl.glVertex3f(p00X,p00Y,z);
		    		    	
		    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
		    	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    	gl.glVertex3f(p01X,p01Y,z);	    	
		    		    		    
		    	gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
		    	gl.glVertex3f(xS, yS,z);
		    	gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
		    	gl.glVertex3f((float)intersectionPoint.getX(),(float)intersectionPoint.getY(),z);	  
		    	gl.glEnd();
			
		        gl.glBegin(GL2.GL_QUADS);
		        gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
		    		gl.glVertex3f(p00X,p00Y,z);
		    		gl.glVertex3f(p01X,p01Y,z);
		    		 gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);	
		        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		        gl.glEnd();
	    	   }else{
	   			//stub
		           gl.glBegin(GL2.GL_LINES);
		       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
			    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
			    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
			    	gl.glVertex3f(p00X,p00Y,z);
			    		    	
			    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
			    	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
			    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
			    	gl.glVertex3f(p01X,p01Y,z);	    	
			    		    		    
			    	//gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
			    	//gl.glVertex3f(xS, yS,z);
			    	//gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
			    	//gl.glVertex3f((float)intersectionPoint.getX(),(float)intersectionPoint.getY(),z);	  
			    	gl.glEnd();
				
			        gl.glBegin(GL2.GL_QUADS);
			        gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
			    		gl.glVertex3f(p00X,p00Y,z);
			    		gl.glVertex3f(p01X,p01Y,z);
			    		 //gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);	
			        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
			        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
			        gl.glEnd();
    	   }
		}else{
			//stub
	           gl.glBegin(GL2.GL_LINES);
	       		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
		    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    	gl.glVertex3f(p00X,p00Y,z);
		    		    	
		    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.7f);	
		    	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		    	//gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 0.0f);	
		    	gl.glVertex3f(p01X,p01Y,z);	    	
		    		    		    
		    	//gl.glColor4f(1.0f, 0.f, 0.0f, 1.0f);
		    	gl.glVertex3f(xS, yS,z);
		    	//gl.glColor4f(1.0f, 0.f, 0.0f, 0.0f);
		    	gl.glVertex3f((float)intersectionPoint.getX(),(float)intersectionPoint.getY(),z);	  
		    	gl.glEnd();
			
		        gl.glBegin(GL2.GL_QUADS);
		        gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],bandColor[3]);
		    		gl.glVertex3f(p00X,p00Y,z);
		    		gl.glVertex3f(p01X,p01Y,z);
		    		 //gl.glColor4f(bandColor[0], bandColor[1], bandColor[2],0.0f);	
		        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
		        gl.glEnd();
			//outline
			gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], 1.0f);	
	        gl.glBegin(GL2.GL_LINES);
		    	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
	        	//gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
		    	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY(),z);	    	
		    	//gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
		    	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
		    	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY()+bandWidth,z);	    	
	    	gl.glEnd();
			
	    	gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], bandColor[3]);	
	        gl.glBegin(GL2.GL_QUADS);
	        	gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y,z);
	        	gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y,z);
	        	  	
//	        	gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y,z);
//	        	gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y,z);
	        	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY()+bandWidth,z);
	        	gl.glVertex3f((float)loc2.getX(),(float)loc2.getY(),z);
		    gl.glEnd();
		}
			
			
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	}

	
    public Point2D getIntersection(final Line2D.Double line1, final Line2D.Double line2) {
        final double x1,y1, x2,y2, x3,y3, x4,y4;
        x1 = line1.x1; y1 = line1.y1; x2 = line1.x2; y2 = line1.y2;
        x3 = line2.x1; y3 = line2.y1; x4 = line2.x2; y4 = line2.y2;
        //
        final double x = ((x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1)) / ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        final double y = ((y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)) / ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        //
        return new Point2D.Double(x, y);
    }
    
	protected Vec2f rotateVec2(Vec2f vec, float angle) {
		Vec2f val = new Vec2f();
		val.setX((float) ((vec.get(0) * Math.cos(angle)) - (vec.get(1) * Math.sin(angle))));
		val.setY((float) ((vec.get(0) * Math.sin(angle)) + (vec.get(1) * Math.cos(angle))));
		return val;
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
			// info.age = GLSubGraph.currentPathwayAge--;
			view.lastUsedRenderer = info.multiFormRenderer;
			return true;
		} else if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
			view.lastUsedLevel1Renderer = info.multiFormRenderer;
		}
		return false;
	}
}