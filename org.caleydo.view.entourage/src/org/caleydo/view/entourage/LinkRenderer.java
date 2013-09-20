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

import gleem.linalg.Vec2f;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.entourage.GLEntourage.PathwayMultiFormInfo;

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
	protected final GLEntourage view;

	public LinkRenderer(GLEntourage view, boolean drawLink, Rectangle2D loc1, Rectangle2D loc2,
			PathwayMultiFormInfo info1, PathwayMultiFormInfo info2, float stubSize, boolean isLocation1Window,
			boolean isLocation2Window, boolean isContextLink, boolean isPathLink, PathwayVertexRep vertexRep1,
			PathwayVertexRep vertexRep2, ColoredConnectionBandRenderer newBandRenderer) {
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.drawLink = drawLink;
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
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
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
			// info.age = GLSubGraph.currentPathwayAge--;
			view.lastUsedRenderer = info.multiFormRenderer;
			return true;
		} else if (info.getCurrentEmbeddingID() == EEmbeddingID.PATHWAY_LEVEL1) {
			view.lastUsedLevel1Renderer = info.multiFormRenderer;
		}
		return false;
	}

	// ////////////////////////////////////////
	protected float z = 2f;

	// /////
	public void render(GLGraphics g, float w, float h) {

		if (this.isLocation1Window && this.isLocation2Window)
			return;

		g.incZ(2.5f);
		// g.color(1, 1, 0, 1);
		// g.gl.glBegin(GL.GL_LINES);
		// g.gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
		// g.gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
		// g.gl.glEnd();
		if (isPathLink) {
			g.color(PortalRenderStyle.PATH_LINK_COLOR);
			bandColor = SelectionType.SELECTION.getColor().getRGB();
			// bandColor = PortalRenderStyle.PATH_LINK_COLOR.getRGB(); //c.getRGBA();
		} else if (isContextLink) {
			bandColor = PortalRenderStyle.CONTEXT_PORTAL_COLOR.getRGB(); // c.getRGBA();
			g.color(PortalRenderStyle.CONTEXT_PORTAL_COLOR);
		} else {
			// bandColor = PortalRenderStyle.DEFAULT_PORTAL_COLOR.getRGB(); //c.getRGBA();
			// g.color(PortalRenderStyle.DEFAULT_PORTAL_COLOR);
		}
		//
		//
		if (drawLink) {
			renderStubs(g.gl, false);
			connectStubs(g.gl);
		} else { // {
			renderStubs(g.gl, true);
		}
		g.lineWidth(1);
		g.incZ(-2.5f);
	}

	// ////////////////////////////////////////////////////////////////////

	protected float outlineOpacity = 0.7f;
	protected float stubConnectionPoint1_X = 0.0f;
	protected float stubConnectionPoint1_Y = 0.0f;
	protected float stubConnectionPoint2_X = 0.0f;
	protected float stubConnectionPoint2_Y = 0.0f;
	protected float stubConnectionPoint3_X = 0.0f;
	protected float stubConnectionPoint3_Y = 0.0f;
	protected float stubConnectionPoint4_X = 0.0f;
	protected float stubConnectionPoint4_Y = 0.0f;
	protected float xS = 0.0f;
	protected float yS = 0.0f;
	protected float xE = 0.0f;
	protected float yE = 0.0f;
	protected float bandWidth = 0.0f;
	protected float stubLength = 0.0f;
	protected float fadeToOpacity = 0.0f;
	protected float linkOpacity = 0.4f;
	protected float[] bandColor = new float[] { .4f, .4f, .4f };
	protected boolean isAngleTooSmall = false;

	protected float getAngle(float sx, float sy, float ex, float ey) {
		float hLine_startX = sx;
		float hLine_endX = ey + (float) loc1.getWidth();
		Vec2f hline = new Vec2f(hLine_startX - hLine_endX, 0);

		float dir_startX = sx;
		float dir_startY = sy;
		float dir_endX = ex;
		float dir_endY = ey;
		Vec2f dir = new Vec2f(dir_startX - dir_endX, dir_startY - dir_endY);
		;

		hline.normalize();
		dir.normalize();

		float cosAngle = hline.dot(dir);
		return cosAngle;
	}

	protected void renderStubs(GL2 gl, boolean fade) {

		bandWidth = Math.min((float) loc1.getHeight(), (float) loc2.getHeight()) / 4.0f;
		stubLength = Math.min((float) loc1.getWidth(), (float) loc2.getWidth());
		// // prepare rendering
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		// //

		// node highlights
		// gl.glLineWidth(3f);
		// gl.glColor3fv(bandColor, 0);
		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3d(loc1.getX(), loc1.getY(), 1);
		// gl.glVertex3d(loc1.getX() + loc1.getWidth(), loc1.getY(), 1);
		// gl.glVertex3d(loc1.getX() + loc1.getWidth(), loc1.getY() + loc1.getHeight(), 1);
		// gl.glVertex3d(loc1.getX(), loc1.getY() + loc1.getHeight(), 1);
		// gl.glEnd();
		//
		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3d(loc2.getX(), loc2.getY(), 1);
		// gl.glVertex3d(loc2.getX() + loc2.getWidth(), loc2.getY(), 1);
		// gl.glVertex3d(loc2.getX() + loc2.getWidth(), loc2.getY() + loc2.getHeight(), 1);
		// gl.glVertex3d(loc2.getX(), loc2.getY() + loc2.getHeight(), 1);
		// gl.glEnd();

		if (this.isPathLink)
			linkOpacity = 0.5f;
		if (fade)
			fadeToOpacity = 0.0f;
		else
			fadeToOpacity = linkOpacity;
		// if(loc1.getX()<loc2.getX()){
		// renderStubRightSide(gl, loc1, loc2, isLocation1Window, info1, info2);
		// renderStubLeftSide(gl, loc2, loc1, isLocation2Window, info2, info1);
		// }else{
		// renderStubRightSide(gl, loc2, loc1, isLocation2Window, info2, info1);
		// renderStubLeftSide(gl, loc1, loc2, isLocation1Window, info1, info2);
		// }
		float angleThreshold = 0.8f;
		// //////////////////////////////////
		int whichConnector = 1;
		boolean isLocationAWithinBoundsofB = false;
		if (loc1.getX() < loc2.getX() && loc1.getX() + loc1.getWidth() < loc2.getX()) {
			// RIGHT / LEFT CONNECTOR
			whichConnector = 2;
		} else {
			if (loc2.getX() < loc1.getX() && loc2.getX() + loc2.getWidth() < loc1.getX()) {
				whichConnector = 1;
				// LEFT / RIGHT CONNECTOR
			} else {
				if ((Math.abs(loc1.getX() - loc2.getX())) > Math.abs(loc1.getX() + loc1.getWidth() - loc2.getX()
						+ loc2.getWidth())) {
					whichConnector = 4;// //Left/Left

				} else {
					whichConnector = 3; // //Right/Right
				}
			}
		}
		boolean renderSrcStub = true;
		if (isLocation1Window && !view.showSrcWindowLinks && !drawLink) {
			renderSrcStub = false;
		}

		float tmpR = bandColor[0];
		float tmpG = bandColor[1];
		float tmpB = bandColor[2];
		float angle = 0.0f;
		switch (whichConnector) {
		default:
		case 1:// Left/Right
			angle = getAngle((float) loc1.getX() + (float) loc1.getWidth(),
					(float) loc1.getY() + (float) loc1.getHeight() / 2.0f, (float) loc2.getX(), (float) loc2.getY()
							+ (float) loc2.getHeight() / 2.0f);
			if (angle < angleThreshold && angle > -angleThreshold) {
				isAngleTooSmall = true;
			}
			if (renderSrcStub) {
				renderStubLeftSide(gl, loc1, loc2, isLocation1Window, info1, info2, true);
			}
			renderStubRightSide(gl, loc2, loc1, isLocation2Window, info2, info1, false);
			break;
		case 2:// Right/Left
			angle = getAngle((float) loc1.getX(), (float) loc1.getY() + (float) loc1.getHeight() / 2.0f,
					(float) loc2.getX() + (float) loc2.getWidth(), (float) loc2.getY() + (float) loc2.getHeight()
							/ 2.0f);
			if (angle < angleThreshold && angle > -angleThreshold) {
				isAngleTooSmall = true;
			}
			if (renderSrcStub) {
				renderStubRightSide(gl, loc1, loc2, isLocation1Window, info1, info2, true);
			}
			renderStubLeftSide(gl, loc2, loc1, isLocation2Window, info2, info1, false);
			break;
		case 3:// Right/Right
			angle = getAngle((float) loc1.getX() + (float) loc1.getWidth(),
					(float) loc1.getY() + (float) loc1.getHeight() / 2.0f,
					(float) loc2.getX() + (float) loc2.getWidth(), (float) loc2.getY() + (float) loc2.getHeight()
							/ 2.0f);
			if (angle < angleThreshold && angle > -angleThreshold) {
				isAngleTooSmall = true;
			}
			renderStubRight2RightSide(gl, loc2, loc1, isLocation2Window, info2, info1, true);
			if (renderSrcStub) {
				renderStubRight2RightSide(gl, loc1, loc2, isLocation1Window, info1, info2, false);
			}
			break;
		case 4:// Left/Left
			angle = getAngle((float) loc1.getX(), (float) loc1.getY() + (float) loc1.getHeight() / 2.0f,
					(float) loc2.getX(), (float) loc2.getY() + (float) loc2.getHeight() / 2.0f);
			if (angle < angleThreshold && angle > -angleThreshold) {

				isAngleTooSmall = true;
			}

			renderStubLeft2LeftSide(gl, loc2, loc1, isLocation2Window, info2, info1, true);

			if (renderSrcStub) {
				renderStubLeft2LeftSide(gl, loc1, loc2, isLocation1Window, info1, info2, false);
			}
			break;
		}

		// // clean up
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
	}

	protected void renderLeftOffsetStub(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		xS = (float) loc.getX() - ((float) loc.getWidth() - (float) loc.getWidth() * 1.25f);
		xE = (float) locTarget.getX() - ((float) locTarget.getWidth() - (float) locTarget.getWidth() * 1.25f);
		if (locTarget.getY() > loc.getY()) { // top tp bottom
			yS = (float) loc.getY() + (float) loc.getHeight();
			yE = (float) locTarget.getY();
		} else {// bottom to top
			yS = (float) loc.getY();
			yE = (float) locTarget.getY() + (float) locTarget.getHeight();
		}

		//
		// float red=0.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=1.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];
		boolean renderStub = true;
		float stubConnectionPointS_X = 0.0f;
		float stubConnectionPointS_Y = 0.0f;
		float stubConnectionPointE_X = 0.0f;
		float stubConnectionPointE_Y = 0.0f;
		if (isWindow) { // && !this.drawLink){
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			// Pair<PathwayMultiFormInfo,PathwayMultiFormInfo> windowPairRev = new Pair<PathwayMultiFormInfo,
			// PathwayMultiFormInfo>(infoTarget,info);
			if (this.view.containsWindowsStub(windowPair)) {
				renderStub = false;
			}

			xS = (float) loc.getX() + (float) loc.getHeight();// *1.25f;
			xE = (float) locTarget.getX() + (float) locTarget.getWidth() * 1.25f;
			// gl.glColor4f(1, 1, 0, 1);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
			// gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
			// gl.glEnd();
			//
			Vec2f dirToWindowCenter = null;
			if (this.drawLink) {
				// float
				// windowCenterX=(infoTarget.window.getAbsoluteLocation().get(0)+(infoTarget.window.getSize().get(0)/2.0f));
				// float
				// windowCenterY=(infoTarget.window.getAbsoluteLocation().get(1)+(infoTarget.window.getSize().get(1)/2.0f));
				// dirToWindowCenter = new Vec2f(windowCenterX -xS ,
				// windowCenterY -yS);
				Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
				dirToWindowCenter = dirNorm;
			} else {
				float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize()
						.get(0) / 2.0f));
				float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize()
						.get(1) / 2.0f));
				dirToWindowCenter = new Vec2f(windowCenterX - xS, windowCenterY - yS);
			}
			dirToWindowCenter.normalize();
			Vec2f normalVecCenterVec = null;
			if (locTarget.getY() > loc.getY()) {
				normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) -Math.PI / 2f);
			} else {
				normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			}
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			stubConnectionPointS_X = xS + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointS_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			stubConnectionPointE_X = xS + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointE_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;

		} else {
			//
			// Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			// gl.glLineWidth(3);
			//
			// gl.glBegin(GL2.GL_LINES);
			// gl.glColor4f(1f, 0f, 0f, 1f);
			// gl.glVertex3f(xS,yS,z);
			// gl.glVertex3f(xS+dirNorm.get(0)*30f,yS+dirNorm.get(1)*30f,z);
			// gl.glEnd();
			// gl.glLineWidth(1);

			Vec2f normalVec = null;
			if (locTarget.getY() > loc.getY()) {
				normalVec = rotateVec2(dirNorm, (float) -Math.PI / 2f);
			} else {
				normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			}
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																			// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																			// * Math.abs(p2x-p1x) ));
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																			// * Math.abs(p2y-p1y) ));

			stubConnectionPointS_X = xS + (dirNorm.get(0) * (stubLength * 0.75f)) - glBandWidthOffsetX;// ;
			stubConnectionPointS_Y = yS + (dirNorm.get(1) * (stubLength * 0.75f)) - glBandWidthOffsetY;// ;
			stubConnectionPointE_X = xS + (dirNorm.get(0) * (stubLength * 0.75f)) + glBandWidthOffsetX;// ;
			stubConnectionPointE_Y = yS + (dirNorm.get(1) * (stubLength * 0.75f)) + glBandWidthOffsetY;// ;
		}

		// float p00X=xS-(float)loc.getHeight()/2.0f;
		// float p00Y=yS;//-(float)loc.getHeight()/2.0f;
		// float p01X=xS+(float)loc.getHeight()/2.0f;
		// float p01Y=yS;//+(float)loc.getHeight()/2.0f;

		// if(locTarget.getY()<loc.getY()){ //top tp bottom
		// yS = (float)loc.getY()+(float)loc.getHeight();
		// yE = (float)locTarget.getY();
		// }else{//bottom to top
		// yS = (float)loc.getY();
		// yE = (float)locTarget.getY()+(float)locTarget.getHeight();
		// }

		float p00X = xS - (float) loc.getHeight() / 2.0f;
		float p00Y = yS;// -(float)loc.getHeight()/2.0f;
		float p01X = xS + (float) loc.getHeight() / 2.0f;
		float p01Y = yS;// +(float)loc.getHeight()/2.0f;

		if (fadeToOpacity < linkOpacity && !renderStub)
			return;

		// if(locTarget.getY()>loc.getY()){ //top to bottom
		// // float tmp = p00X;
		// // p00X=p01X;
		// // p01X=p00X;
		// // tmp = p00Y;
		// // p00Y=p01Y;
		// // p01Y=p00Y;
		// float tmp=stubConnectionPointE_X;
		// stubConnectionPointE_X=stubConnectionPointS_X;
		// stubConnectionPointS_X=stubConnectionPointE_X;
		//
		// tmp=stubConnectionPointE_Y;
		// stubConnectionPointE_Y=stubConnectionPointS_Y;
		// stubConnectionPointS_Y=stubConnectionPointE_Y;
		// }
		//

		float byS = p01Y + (float) loc.getHeight();
		float byE = p00Y + (float) loc.getHeight();

		if (locTarget.getY() > loc.getY()) { // top tp bottom
			yS = (float) loc.getY() + (float) loc.getHeight();
			yE = (float) locTarget.getY();
		} else {// bottom to top
			yS = (float) loc.getY();
			yE = (float) locTarget.getY() + (float) locTarget.getHeight();
		}
		if (true) {
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(red, green, blue, this.outlineOpacity);
			gl.glVertex3f(p00X, p00Y, z);
			gl.glColor4f(red, green, blue, fadeToOpacity);
			gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

			gl.glColor4f(red, green, blue, this.outlineOpacity);
			gl.glVertex3f(p01X, p01Y, z);
			gl.glColor4f(red, green, blue, fadeToOpacity);
			gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);

			// gl.glColor4f(red, green, blue,outlineOpacity);
			// gl.glColor4f(0f, 0f, 0f,1f);
			// gl.glVertex3f(p01X, byS,z);
			// gl.glVertex3f(p01X, byE,z);
			//
			// gl.glVertex3f(p00X, byS,z);
			// gl.glVertex3f(p00X, byE,z);

			gl.glEnd();

			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(red, green, blue, linkOpacity);
			gl.glVertex3f(p00X, p00Y, z);
			gl.glVertex3f(p01X, p01Y, z);
			gl.glColor4f(red, green, blue, fadeToOpacity);
			gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
			gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

			// gl.glColor4f(red, green, blue,linkOpacity);
			// //
			// //gl.glColor4f(red, green, blue,fadeToOpacity);
			// gl.glVertex3f(p01X, byE,z);
			// gl.glVertex3f(p00X, byE,z);
			// gl.glVertex3f(p00X,p00Y,z);
			// gl.glVertex3f(p01X,p01Y,z);
			gl.glEnd();
		}
		if (start) {
			// if(locTarget.getY()>loc.getY()){
			// stubConnectionPoint2_X=stubConnectionPointS_X;
			// stubConnectionPoint2_Y=stubConnectionPointS_Y;
			// stubConnectionPoint1_X=stubConnectionPointE_X;
			// stubConnectionPoint1_Y=stubConnectionPointE_Y;
			// }else{
			stubConnectionPoint1_X = stubConnectionPointS_X;
			stubConnectionPoint1_Y = stubConnectionPointS_Y;
			stubConnectionPoint2_X = stubConnectionPointE_X;
			stubConnectionPoint2_Y = stubConnectionPointE_Y;
			// }
		} else {
			// if(locTarget.getY()<loc.getY()){
			stubConnectionPoint3_X = stubConnectionPointS_X;
			stubConnectionPoint3_Y = stubConnectionPointS_Y;
			stubConnectionPoint4_X = stubConnectionPointE_X;
			stubConnectionPoint4_Y = stubConnectionPointE_Y;
			// }else{
			// stubConnectionPoint4_X=stubConnectionPointS_X;
			// stubConnectionPoint4_Y=stubConnectionPointS_Y;
			// stubConnectionPoint3_X=stubConnectionPointE_X;
			// stubConnectionPoint3_Y=stubConnectionPointE_Y;
			// }
		}
	}

	protected void renderRightOffsetStub(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		// System.out.println("renderRightOffsetStub");
		xS = (float) loc.getX() + (float) loc.getWidth() - ((float) loc.getWidth() * 0.25f);
		xE = (float) locTarget.getX() + (float) locTarget.getWidth() - ((float) locTarget.getWidth() * 0.25f);
		if (locTarget.getY() > loc.getY()) { // top tp bottom
			yS = (float) loc.getY() + (float) loc.getHeight();
			yE = (float) locTarget.getY();
		} else {// bottom to top
			yS = (float) loc.getY();
			yE = (float) locTarget.getY() + (float) locTarget.getHeight();
		}

		//
		// float red=0.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=1.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];
		boolean renderStub = true;
		float stubConnectionPointS_X = 0.0f;
		float stubConnectionPointS_Y = 0.0f;
		float stubConnectionPointE_X = 0.0f;
		float stubConnectionPointE_Y = 0.0f;
		if (isWindow) {
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			if (this.view.containsWindowsStub(windowPair))
				renderStub = false;

			xS = (float) loc.getX() + (float) loc.getWidth() - (float) loc.getHeight();// *1.25f;
			xE = (float) locTarget.getX() + (float) locTarget.getWidth() * 1.25f;
			// gl.glColor4f(1, 1, 0, 1);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
			// gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
			// gl.glEnd();
			//
			float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize().get(0) / 2.0f));
			float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize().get(1) / 2.0f));
			Vec2f dirToWindowCenter = new Vec2f(windowCenterX - xS, windowCenterY - yS);
			dirToWindowCenter.normalize();

			if (this.drawLink) {
				// float
				// windowCenterX=(infoTarget.window.getAbsoluteLocation().get(0)+(infoTarget.window.getSize().get(0)/2.0f));
				// float
				// windowCenterY=(infoTarget.window.getAbsoluteLocation().get(1)+(infoTarget.window.getSize().get(1)/2.0f));
				// dirToWindowCenter = new Vec2f(windowCenterX -xS ,
				// windowCenterY -yS);
				Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
				dirNorm.normalize();
				dirToWindowCenter = dirNorm;

			}
			Vec2f normalVecCenterVec = null;
			if (locTarget.getY() > loc.getY()) {
				normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) -Math.PI / 2f);
			} else {
				normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			}
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			stubConnectionPointS_X = xS + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointS_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			stubConnectionPointE_X = xS + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointE_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;

		} else {
			//
			// Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			// gl.glLineWidth(3);
			//
			// gl.glBegin(GL2.GL_LINES);
			// gl.glColor4f(1f, 0f, 0f, 1f);
			// gl.glVertex3f(xS,yS,z);
			// gl.glVertex3f(xS+dirNorm.get(0)*30f,yS+dirNorm.get(1)*30f,z);
			// gl.glEnd();
			// gl.glLineWidth(1);

			Vec2f normalVec = null;
			if (locTarget.getY() > loc.getY()) {
				normalVec = rotateVec2(dirNorm, (float) -Math.PI / 2f);
			} else {
				normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			}
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																			// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																			// * Math.abs(p2x-p1x) ));
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																			// * Math.abs(p2y-p1y) ));

			stubConnectionPointS_X = xS + (dirNorm.get(0) * (stubLength * 0.75f)) - glBandWidthOffsetX;// ;
			stubConnectionPointS_Y = yS + (dirNorm.get(1) * (stubLength * 0.75f)) - glBandWidthOffsetY;// ;
			stubConnectionPointE_X = xS + (dirNorm.get(0) * (stubLength * 0.75f)) + glBandWidthOffsetX;// ;
			stubConnectionPointE_Y = yS + (dirNorm.get(1) * (stubLength * 0.75f)) + glBandWidthOffsetY;// ;
		}

		// float p00X=xS-(float)loc.getHeight()/2.0f;
		// float p00Y=yS;//-(float)loc.getHeight()/2.0f;
		// float p01X=xS+(float)loc.getHeight()/2.0f;
		// float p01Y=yS;//+(float)loc.getHeight()/2.0f;

		// if(locTarget.getY()<loc.getY()){ //top tp bottom
		// yS = (float)loc.getY()+(float)loc.getHeight();
		// yE = (float)locTarget.getY();
		// }else{//bottom to top
		// yS = (float)loc.getY();
		// yE = (float)locTarget.getY()+(float)locTarget.getHeight();
		// }

		float p00X = xS - (float) loc.getHeight() / 2.0f;
		float p00Y = yS;// -(float)loc.getHeight()/2.0f;
		float p01X = xS + (float) loc.getHeight() / 2.0f;
		float p01Y = yS;// +(float)loc.getHeight()/2.0f;

		// if(fadeToOpacity<linkOpacity && !renderStub)return;
		if (fadeToOpacity < linkOpacity && !renderStub)
			return;

		// if(locTarget.getY()>loc.getY()){ //top to bottom
		// // float tmp = p00X;
		// // p00X=p01X;
		// // p01X=p00X;
		// // tmp = p00Y;
		// // p00Y=p01Y;
		// // p01Y=p00Y;
		// float tmp=stubConnectionPointE_X;
		// stubConnectionPointE_X=stubConnectionPointS_X;
		// stubConnectionPointS_X=stubConnectionPointE_X;
		//
		// tmp=stubConnectionPointE_Y;
		// stubConnectionPointE_Y=stubConnectionPointS_Y;
		// stubConnectionPointS_Y=stubConnectionPointE_Y;
		// }
		//

		float byS = p01Y + (float) loc.getHeight();
		float byE = p00Y + (float) loc.getHeight();

		if (locTarget.getY() > loc.getY()) { // top tp bottom
			yS = (float) loc.getY() + (float) loc.getHeight();
			yE = (float) locTarget.getY();
		} else {// bottom to top
			yS = (float) loc.getY();
			yE = (float) locTarget.getY() + (float) locTarget.getHeight();
		}

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);

		// gl.glColor4f(red, green, blue,outlineOpacity);
		// gl.glColor4f(0f, 0f, 0f,1f);
		// gl.glVertex3f(p01X, byS,z);
		// gl.glVertex3f(p01X, byE,z);
		//
		// gl.glVertex3f(p00X, byS,z);
		// gl.glVertex3f(p00X, byE,z);

		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(red, green, blue, linkOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

		// gl.glColor4f(red, green, blue,linkOpacity);
		// //
		// //gl.glColor4f(red, green, blue,fadeToOpacity);
		// gl.glVertex3f(p01X, byE,z);
		// gl.glVertex3f(p00X, byE,z);
		// gl.glVertex3f(p00X,p00Y,z);
		// gl.glVertex3f(p01X,p01Y,z);
		gl.glEnd();

		if (start) {
			// if(locTarget.getY()>loc.getY()){
			// stubConnectionPoint2_X=stubConnectionPointS_X;
			// stubConnectionPoint2_Y=stubConnectionPointS_Y;
			// stubConnectionPoint1_X=stubConnectionPointE_X;
			// stubConnectionPoint1_Y=stubConnectionPointE_Y;
			// }else{
			stubConnectionPoint1_X = stubConnectionPointS_X;
			stubConnectionPoint1_Y = stubConnectionPointS_Y;
			stubConnectionPoint2_X = stubConnectionPointE_X;
			stubConnectionPoint2_Y = stubConnectionPointE_Y;
			// }
		} else {
			// if(locTarget.getY()<loc.getY()){
			stubConnectionPoint3_X = stubConnectionPointS_X;
			stubConnectionPoint3_Y = stubConnectionPointS_Y;
			stubConnectionPoint4_X = stubConnectionPointE_X;
			stubConnectionPoint4_Y = stubConnectionPointE_Y;
			// }else{
			// stubConnectionPoint4_X=stubConnectionPointS_X;
			// stubConnectionPoint4_Y=stubConnectionPointS_Y;
			// stubConnectionPoint3_X=stubConnectionPointE_X;
			// stubConnectionPoint3_Y=stubConnectionPointE_Y;
			// }
		}
	}

	// /////////////////////////////////////////
	// ///////////////////////////////////////
	// ////////////////////////////// Right2Right
	// ///////////////////////////////////////
	protected void renderStubRight2RightSide(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		if (this.isAngleTooSmall || isWindow) {
			renderRightOffsetStub(gl, loc, locTarget, isWindow, info, infoTarget, start);
			return;
		}
		xS = (float) loc.getX() + (float) loc.getWidth();
		yS = (float) loc.getY() + (float) loc.getHeight() / 2.0f;
		xE = (float) locTarget.getX() + (float) locTarget.getWidth();
		yE = (float) locTarget.getY() + (float) locTarget.getHeight() / 2.0f;
		//
		// float red=0.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=1.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];
		boolean renderStub = true;
		float stubConnectionPointS_X = 0.0f;
		float stubConnectionPointS_Y = 0.0f;
		float stubConnectionPointE_X = 0.0f;
		float stubConnectionPointE_Y = 0.0f;
		float p00X = xS;
		float p00Y = yS - (float) loc.getHeight() / 2.0f;
		float p01X = xS;
		float p01Y = yS + (float) loc.getHeight() / 2.0f;

		if (isWindow) {
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			if (this.view.containsWindowsStub(windowPair))
				renderStub = false;
			// gl.glColor4f(1, 1, 0, 1);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
			// gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
			// gl.glEnd();
			//
			float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize().get(0) / 2.0f));
			float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize().get(1) / 2.0f));
			Vec2f dirToWindowCenter = new Vec2f(windowCenterX - xS, windowCenterY - yS);
			dirToWindowCenter.normalize();

			if (this.drawLink) {
				Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
				dirNorm.normalize();
				dirToWindowCenter = dirNorm;
			}

			Vec2f normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			stubConnectionPointE_X = xS + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointE_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			stubConnectionPointS_X = xS + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointS_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;

			// float tmp=p00X;//
			// p00X=p01X;//
			// p01X=tmp;//=yS-(float)loc.getHeight()/2.0f;
			// tmp=p00Y;//=xS;
			// p00Y=p01Y;//=yS+(float)loc.getHeight()/2.0f;
			// p01Y=tmp;
		} else {
			//
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																			// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																			// * Math.abs(p2x-p1x) ));
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																			// * Math.abs(p2y-p1y) ));

			stubConnectionPointS_X = xS + (dirNorm.get(0) * (stubLength)) - glBandWidthOffsetX;// ;
			stubConnectionPointS_Y = yS + (dirNorm.get(1) * (stubLength)) - glBandWidthOffsetY;// ;
			stubConnectionPointE_X = xS + (dirNorm.get(0) * (stubLength)) + glBandWidthOffsetX;// ;
			stubConnectionPointE_Y = yS + (dirNorm.get(1) * (stubLength)) + glBandWidthOffsetY;// ;
		}

		if (fadeToOpacity < linkOpacity && !renderStub)
			return;

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(red, green, blue, linkOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);
		gl.glEnd();

		// bandColor[0]=0f;
		// bandColor[1]=1f;
		// bandColor[2]=0f;

		if (start) {
			stubConnectionPoint1_X = stubConnectionPointS_X;
			stubConnectionPoint1_Y = stubConnectionPointS_Y;
			stubConnectionPoint2_X = stubConnectionPointE_X;
			stubConnectionPoint2_Y = stubConnectionPointE_Y;
		} else {

			stubConnectionPoint3_X = stubConnectionPointS_X;
			stubConnectionPoint3_Y = stubConnectionPointS_Y;
			stubConnectionPoint4_X = stubConnectionPointE_X;
			stubConnectionPoint4_Y = stubConnectionPointE_Y;
		}
	}

	// /////////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////// Right2Left
	// ///////////////////////////////////////
	protected void renderStubRightSide(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		if (this.isAngleTooSmall) {
			renderRightOffsetStub(gl, loc, locTarget, isWindow, info, infoTarget, start);
			return;
		}
		xS = (float) loc.getX() + (float) loc.getWidth();
		yS = (float) loc.getY() + (float) loc.getHeight() / 2.0f;
		xE = (float) locTarget.getX();
		yE = (float) locTarget.getY() + (float) locTarget.getHeight() / 2.0f;
		//
		// float red=1.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=0.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];
		boolean renderStub = true;
		if (isWindow && !this.drawLink) {
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			if (this.view.containsWindowsStubRightSide(windowPair))
				renderStub = false;
			// gl.glColor4f(1, 1, 0, 1);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
			// gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
			// gl.glEnd();
			//
			float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize().get(0) / 2.0f));
			float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize().get(1) / 2.0f));
			Vec2f dirToWindowCenter = new Vec2f(windowCenterX - xS, windowCenterY - yS);
			dirToWindowCenter.normalize();
			Vec2f normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			stubConnectionPoint1_X = xS + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPoint1_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			stubConnectionPoint2_X = xS + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPoint2_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;

			// gl.glBegin(GL2.GL_LINES);
			// gl.glColor4f(0f, 0f, 1f, 1f);
			// gl.glVertex3f(xS,yS,z);
			// gl.glVertex3f(windowCenterX,windowCenterY ,z);
			// gl.glEnd();

		} else {
			//
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																			// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																			// * Math.abs(p2x-p1x) ));
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																			// * Math.abs(p2y-p1y) ));

			stubConnectionPoint1_X = xS + (dirNorm.get(0) * (stubLength)) - glBandWidthOffsetX;// ;
			stubConnectionPoint1_Y = yS + (dirNorm.get(1) * (stubLength)) - glBandWidthOffsetY;// ;
			stubConnectionPoint2_X = xS + (dirNorm.get(0) * (stubLength)) + glBandWidthOffsetX;// ;
			stubConnectionPoint2_Y = yS + (dirNorm.get(1) * (stubLength)) + glBandWidthOffsetY;// ;
		}
		float p00X = xS;
		float p00Y = yS - (float) loc.getHeight() / 2.0f;
		float p01X = xS;
		float p01Y = yS + (float) loc.getHeight() / 2.0f;

		if (fadeToOpacity < linkOpacity && !renderStub)
			return;

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y, z);

		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y, z);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(red, green, blue, linkOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y, z);
		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y, z);
		gl.glEnd();
		// gl.glDisable(GL2.GL_BLEND);
		if (isWindow) {
			float zn = 0.35f;
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(red, green, blue, linkOpacity);
			// gl.glColor4f(1f, 0f,0f,1f);
			gl.glVertex3f(p00X, p00Y, zn);
			gl.glVertex3f(p01X, p01Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p01X - (float) loc.getHeight() / 1.0f, p01Y, zn);
			gl.glVertex3f(p00X - (float) loc.getHeight() / 1.0f, p00Y, zn);
			gl.glEnd();
			zn = 2f;
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(red, green, blue, this.outlineOpacity);
			// gl.glColor4f(1f, 0f,0f,1f);
			gl.glVertex3f(p01X, p01Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p01X - (float) loc.getHeight() * .5f, p01Y, zn);
			gl.glColor4f(red, green, blue, this.outlineOpacity);
			gl.glVertex3f(p00X, p00Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p00X - (float) loc.getHeight() * .5f, p00Y, zn);
			gl.glEnd();
		}
		// gl.glEnable(GL2.GL_BLEND);

	}

	// /////////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////// Left2Right
	// ///////////////////////////////////////
	protected void renderStubLeft2LeftSide(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		if (this.isAngleTooSmall || isWindow) {
			renderLeftOffsetStub(gl, loc, locTarget, isWindow, info, infoTarget, start);
			return;
		}
		xS = (float) loc.getX();
		yS = (float) loc.getY() + (float) loc.getHeight() / 2.0f;
		xE = (float) locTarget.getX();
		yE = (float) locTarget.getY() + (float) locTarget.getHeight() / 2.0f;
		//
		// float red=0.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=1.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];

		float stubConnectionPointS_X = 0.0f;
		float stubConnectionPointS_Y = 0.0f;
		float stubConnectionPointE_X = 0.0f;
		float stubConnectionPointE_Y = 0.0f;

		boolean renderStub = true;
		if (isWindow && !this.drawLink) {
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			if (this.view.containsWindowsStubRightSide(windowPair))
				renderStub = false;
			// gl.glColor4f(1, 1, 0, 1);
			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f((float) loc1.getCenterX(), (float) loc1.getCenterY(),z);
			// gl.glVertex3f((float) loc2.getCenterX(), (float) loc2.getCenterY(),z);
			// gl.glEnd();

			float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize().get(0) / 2.0f));
			float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize().get(1) / 2.0f));
			Vec2f dirToWindowCenter = new Vec2f(windowCenterX - xS, windowCenterY - yS);
			dirToWindowCenter.normalize();
			Vec2f normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			stubConnectionPointS_X = xS + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointS_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			stubConnectionPointE_X = xS + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPointE_Y = yS + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;

			// gl.glBegin(GL2.GL_LINES);
			// gl.glColor4f(0f, 1f, 0f, 1f);
			// gl.glVertex3f(xS,yS,z);
			// gl.glVertex3f(windowCenterX,windowCenterY ,z);
			// gl.glEnd();
			//
		} else {
			//
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																			// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																			// * Math.abs(p2x-p1x) ));
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																			// * Math.abs(p2y-p1y) ));

			stubConnectionPointS_X = xS + (dirNorm.get(0) * (stubLength)) - glBandWidthOffsetX;// ;
			stubConnectionPointS_Y = yS + (dirNorm.get(1) * (stubLength)) - glBandWidthOffsetY;// ;
			stubConnectionPointE_X = xS + (dirNorm.get(0) * (stubLength)) + glBandWidthOffsetX;// ;
			stubConnectionPointE_Y = yS + (dirNorm.get(1) * (stubLength)) + glBandWidthOffsetY;// ;
		}
		float p00X = xS;
		float p00Y = yS - (float) loc.getHeight() / 2.0f;
		float p01X = xS;
		float p01Y = yS + (float) loc.getHeight() / 2.0f;

		if (fadeToOpacity < linkOpacity && !renderStub)
			return;

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);

		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(red, green, blue, linkOpacity);
		gl.glVertex3f(p00X, p00Y, z);
		gl.glVertex3f(p01X, p01Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPointE_X, stubConnectionPointE_Y, z);
		gl.glVertex3f(stubConnectionPointS_X, stubConnectionPointS_Y, z);
		gl.glEnd();

		if (start) {
			stubConnectionPoint1_X = stubConnectionPointS_X;
			stubConnectionPoint1_Y = stubConnectionPointS_Y;
			stubConnectionPoint2_X = stubConnectionPointE_X;
			stubConnectionPoint2_Y = stubConnectionPointE_Y;
		} else {
			stubConnectionPoint3_X = stubConnectionPointS_X;
			stubConnectionPoint3_Y = stubConnectionPointS_Y;
			stubConnectionPoint4_X = stubConnectionPointE_X;
			stubConnectionPoint4_Y = stubConnectionPointE_Y;
		}

	}

	protected void renderStubLeftSide(GL2 gl, Rectangle2D loc, Rectangle2D locTarget, boolean isWindow,
			PathwayMultiFormInfo info, PathwayMultiFormInfo infoTarget, boolean start) {
		if (this.isAngleTooSmall) {
			renderLeftOffsetStub(gl, loc, locTarget, isWindow, info, infoTarget, start);
			return;
		}
		xS = (float) locTarget.getX() + (float) locTarget.getWidth();
		yS = (float) locTarget.getY() + (float) locTarget.getHeight() / 2.0f;

		xE = (float) loc.getX();
		yE = (float) loc.getY() + (float) loc.getHeight() / 2.0f;

		// float red=0.0f;//bandColor[0]
		// float green=0.0f;//bandColor[1]
		// float blue=1.0f;//bandColor[2]
		float red = bandColor[0];
		float green = bandColor[1];
		float blue = bandColor[2];

		boolean renderStub = true;
		if (isWindow && !this.drawLink) {
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPair = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					info, infoTarget);
			Pair<PathwayMultiFormInfo, PathwayMultiFormInfo> windowPairRev = new Pair<PathwayMultiFormInfo, PathwayMultiFormInfo>(
					infoTarget, info);

			// xE = (float)loc.getX()+(float)locTarget.getHeight()/2.0f;;
			// z=0.5f;
			// red=0.0f;//bandColor[0]
			// green=0.0f;//bandColor[1]
			// blue=1.0f;//bandColor[2]

			if (this.view.containsWindowsStub(windowPair))// ||this.view.containsWindowsStub(windowPairRev)){
				renderStub = false;

			float windowCenterX = (infoTarget.window.getAbsoluteLocation().get(0) + (infoTarget.window.getSize().get(0) / 2.0f));
			float windowCenterY = (infoTarget.window.getAbsoluteLocation().get(1) + (infoTarget.window.getSize().get(1) / 2.0f));
			Vec2f dirToWindowCenter = null;

			dirToWindowCenter = new Vec2f(windowCenterX - xE, windowCenterY - yE);

			dirToWindowCenter.normalize();
			Vec2f normalVecCenterVec = rotateVec2(dirToWindowCenter, (float) Math.PI / 2f);
			float glBandWidthOffsetX_CenterVec = normalVecCenterVec.get(0) * bandWidth / 2.0f;// Math.abs(p2x-p1x)/2.0f;
																								// //pxlConverter.getGLWidthForPixelWidth(Math.round(normalVec.get(0)
																								// * Math.abs(p2x-p1x)
																								// ));
			float glBandWidthOffsetY_CenterVec = normalVecCenterVec.get(1) * bandWidth / 2.0f;// Math.abs(p2y-p1y)/2.0f;//pxlConverter.getGLHeightForPixelHeight(Math.round(normalVec.get(1)
																								// * Math.abs(p2y-p1y)
																								// ));

			// stubConnectionPoint3_X= xE - (dirToWindowCenter.get(0) *
			// (stubLength));//-glBandWidthOffsetX_CenterVec;//;
			stubConnectionPoint4_X = xE + (dirToWindowCenter.get(0) * (stubLength)) - glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPoint4_Y = yE + (dirToWindowCenter.get(1) * (stubLength)) - glBandWidthOffsetY_CenterVec;// ;
			// stubConnectionPoint4_X= xE - (dirToWindowCenter.get(0) *
			// (stubLength));//+glBandWidthOffsetX_CenterVec;//;
			stubConnectionPoint3_X = xE + (dirToWindowCenter.get(0) * (stubLength)) + glBandWidthOffsetX_CenterVec;// ;
			stubConnectionPoint3_Y = yE + (dirToWindowCenter.get(1) * (stubLength)) + glBandWidthOffsetY_CenterVec;// ;
			//
			// gl.glBegin(GL2.GL_LINES);
			// gl.glColor4f(1f, 0f, 0f, 1f);
			// gl.glVertex3f(xE,yE,z);
			// gl.glVertex3f(windowCenterX,windowCenterY ,z);
			// gl.glEnd();

		} else {
			Vec2f dirNorm = new Vec2f(xE - xS, yE - yS);
			dirNorm.normalize();
			Vec2f normalVec = rotateVec2(dirNorm, (float) Math.PI / 2f);
			float glBandWidthOffsetX = normalVec.get(0) * bandWidth / 2.0f;
			float glBandWidthOffsetY = normalVec.get(1) * bandWidth / 2.0f;
			stubConnectionPoint3_X = xE - (dirNorm.get(0) * (stubLength)) - glBandWidthOffsetX;// ;
			stubConnectionPoint3_Y = yE - (dirNorm.get(1) * (stubLength)) - glBandWidthOffsetY;// ;
			stubConnectionPoint4_X = xE - (dirNorm.get(0) * (stubLength)) + glBandWidthOffsetX;// ;
			stubConnectionPoint4_Y = yE - (dirNorm.get(1) * (stubLength)) + glBandWidthOffsetY;// ;
		}

		float stubConnectorWidth = (float) loc.getHeight(); // (((float)loc1.getHeight()*2.0f < (float)loc2.getHeight())
															// ? (float)loc1.getHeight() : (float)loc2.getHeight());
		float p10X = xE;
		float p10Y = yE - stubConnectorWidth / 2.0f;
		float p11X = xE;
		float p11Y = yE + stubConnectorWidth / 2.0f;

		if (fadeToOpacity < linkOpacity && !renderStub)
			return;
		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p10X, p10Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y, z);

		gl.glColor4f(red, green, blue, this.outlineOpacity);
		gl.glVertex3f(p11X, p11Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y, z);
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(red, green, blue, linkOpacity);
		gl.glVertex3f(p10X, p10Y, z);
		gl.glVertex3f(p11X, p11Y, z);
		gl.glColor4f(red, green, blue, fadeToOpacity);
		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y, z);
		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y, z);
		gl.glEnd();
		if (isWindow) {
			float zn = 0.35f;
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(red, green, blue, linkOpacity);
			// gl.glColor4f(1f, 0f,0f,1f);
			gl.glVertex3f(p10X, p10Y, zn);
			gl.glVertex3f(p11X, p11Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p11X + (float) loc.getHeight() * 1.5f, p11Y, zn);
			gl.glVertex3f(p10X + (float) loc.getHeight() * 1.5f, p10Y, zn);
			gl.glEnd();
			zn = 2f;
			gl.glBegin(GL2.GL_LINES);
			gl.glColor4f(red, green, blue, this.outlineOpacity);
			// gl.glColor4f(1f, 0f,0f,1f);

			gl.glVertex3f(p11X, p11Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p11X + (float) loc.getHeight() * .5f, p11Y, zn);
			gl.glColor4f(red, green, blue, this.outlineOpacity);
			gl.glVertex3f(p10X, p10Y, zn);
			gl.glColor4f(red, green, blue, 0.0f);
			gl.glVertex3f(p10X + (float) loc.getHeight() * .5f, p10Y, zn);
			gl.glEnd();
		}
	}

	// /////
	protected void connectStubs(GL2 gl) {
		// outline
		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], this.outlineOpacity);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y, z);
		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y, z);
		//
		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y, z);
		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y, z);
		gl.glEnd();

		gl.glColor4f(bandColor[0], bandColor[1], bandColor[2], linkOpacity);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(stubConnectionPoint1_X, stubConnectionPoint1_Y, z);
		gl.glVertex3f(stubConnectionPoint3_X, stubConnectionPoint3_Y, z);
		gl.glVertex3f(stubConnectionPoint4_X, stubConnectionPoint4_Y, z);
		gl.glVertex3f(stubConnectionPoint2_X, stubConnectionPoint2_Y, z);
		gl.glEnd();
	}

	// ///
	protected Vec2f rotateVec2(Vec2f vec, float angle) {
		Vec2f val = new Vec2f();
		val.setX((float) ((vec.get(0) * Math.cos(angle)) - (vec.get(1) * Math.sin(angle))));
		val.setY((float) ((vec.get(0) * Math.sin(angle)) + (vec.get(1) * Math.cos(angle))));
		return val;
	}
}