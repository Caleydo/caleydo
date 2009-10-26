package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

public class DrawAbleHyperbolicLayoutConnector
	implements IDrawAbleConnection {

	private int iID;
	protected IDrawAbleNode iNodeA;
	protected IDrawAbleNode iNodeB;
	//private ITreeProjection treeProjector = null;
	private boolean bIsPickAble = true;
	FloatBuffer fbSplinePoints = null;
//	boolean bIsVisible = false;
	private boolean bHighlight;

	public DrawAbleHyperbolicLayoutConnector(IDrawAbleNode iNodeA, IDrawAbleNode iNodeB/*,
		ITreeProjection treeProjector*/) {
		this.iNodeA = iNodeA;
		this.iNodeB = iNodeB;
		this.iID = generateID();
		//this.treeProjector = treeProjector;
	}

	@Override
	public final int getID() {
		return iID;
	}

	@Override
	public final int compareTo(IDrawAbleConnection conn) {
		return iID - conn.getID();
	}

	private int generateID() {
		int left = iNodeA.hashCode();
		int right = iNodeB.hashCode();
		return ((left << 12) | (left >> (32 - 12))) ^ right;
	}
	
	@Override
	public final boolean isVisible() {
//		return iNodeA.isVisible() && iNodeB.isVisible();
		return iNodeA.isVisible() || iNodeB.isVisible();
	}

	@Override
	public void setHighlight(boolean b) {
		this.bHighlight = b;
	}

	@Override
	public void updateConnection(ITreeProjection treeProjector){
		
//		if(!iNodeA.isVisible()){
//			calculateSplinePoints(treeProjector);
//		}
		calculateSplinePoints(treeProjector);

	}
	
	private final Vec3f[] findClosestCorrespondendingPoints() {
		float fMin = Float.MAX_VALUE;
		Vec3f foundA = null;
		Vec3f foundB = null;
		float ft;
		
		for (Vec3f pointA : iNodeA.getConnectionPoints())
			for (Vec3f pointB : iNodeB.getConnectionPoints())
				if ((ft =
					(float) Math.sqrt(Math.pow(pointA.x() - pointB.x(), 2)
						+ Math.pow(pointA.y() - pointB.y(), 2))) < fMin) {
					foundA = pointA;
					foundB = pointB;
					fMin = ft;
				

			
			
		}
		Vec3f[] vaPoints = { foundA, foundB };
		return vaPoints;
	}

	private final Vec3f[] findClosestCorrespondingPointsOfOriginalPosition() {
		float fMin = Float.MAX_VALUE;
		Vec3f foundA = null;
		Vec3f foundB = null;
		float ft;
		for (Vec3f pointA : iNodeA.getConnectionPointsOfOriginalPosition())
			for (Vec3f pointB : iNodeB.getConnectionPointsOfOriginalPosition())
				if ((ft =
					(float) Math.sqrt(Math.pow(pointA.x() - pointB.x(), 2)
						+ Math.pow(pointA.y() - pointB.y(), 2))) < fMin) {
					foundA = pointA;
					foundB = pointB;
					fMin = ft;
				}
		Vec3f[] vaPoints = { foundA, foundB };
		return vaPoints;
	}

	private final void calculateSplinePoints(ITreeProjection treeProjector) {
		Vec3f[] endPoints = findClosestCorrespondendingPoints();
		// first point
		Vec3f pStartP = endPoints[0];
		// last point
		Vec3f pEndP = endPoints[1];
		
//		if(!iNodeA.isVisible() && iNodeB.isVisible()){
//			Vec3f h= pStartP;
//			pStartP = new Vec3f(pEndP);
//			pEndP = h;
//			pEndP = foundInterceptPointWithBorder(pStartP, pEndP, treeProjector);
//		}
//		if(!iNodeB.isVisible() && iNodeA.isVisible()){
//			Vec3f h = pEndP;
//			pEndP = new Vec3f(pStartP);
//			pStartP = h;
//			pStartP = foundInterceptPointWithBorder(pStartP, pEndP, treeProjector);
//		}
		
//		float fLineToCenter1 = treeProjector.getLineFromPointToCenter(pStartP.x(), pStartP.y());
//		float fLineToCenter2 = treeProjector.getLineFromPointToCenter(pEndP.x(), pEndP.y());
//		float fRadius = treeProjector.getProjectedLineFromCenterToBorder();
		
		
		// vector from start to end
		// Vec3f vSE = new Vec3f(pEndP.x() - pStartP.x(), pEndP.y() - pStartP.y(), pEndP.z() - pStartP.z());
		// base point ... middle of vec start -> end
		Vec3f pSpP1 = getSplinePoint();
		// new Vec3f
		// (pStartP.x() + vSE.x() / 2.0f, pStartP.y() + vSE.y() / 2.0f, pStartP.z() + vSE.z()
		// / 2.0f);
		// if projection exists, use it, else calculate Spline Point in the middle of the line
		if (treeProjector != null) {
			// pStartP = treeProjector.projectCoordinates(pStartP);
			pSpP1 = treeProjector.projectCoordinates(pSpP1);
			// pEndP = treeProjector.projectCoordinates(pEndP);
		}
		// else{
		// pSpP1.set
		// (pStartP.x() + vSE.x() / 2.0f, pStartP.y() + vSE.y() / 2.0f, pStartP.z() + vSE.z()
		// / 2.0f);
		// }
		fbSplinePoints = FloatBuffer.allocate(3 * 3);
		float[] fA =
			{ pStartP.x(), pStartP.y(), pStartP.z(), pSpP1.x(), pSpP1.y(), pSpP1.z(), pEndP.x(), pEndP.y(),
					pEndP.z() };
		fbSplinePoints.put(fA);
		fbSplinePoints.rewind();
		// this.bIsPickAble = iNodeA.isPickAble() && iNodeB.isPickAble();
	}

	@Override
	public void draw(GL gl) {
//		calculateSplinePoints();
		if (bHighlight) {
			gl.glColor4fv(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_COLORSHEME_HL, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_THICKNESS_HL);
		}
		else {
			gl.glColor4fv(HyperbolicRenderStyle.DA_HB_CONNECTION_COLORSHEME, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_HB_CONNECTION_THICKNESS);
		}
		fbSplinePoints.rewind();
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, fbSplinePoints);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i <= HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS; i++)
			gl.glEvalCoord1f((float) i / (float) HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS);
		gl.glEnd();
	}

	private Vec3f getSplinePoint() {
		Vec3f[] endPoints = findClosestCorrespondingPointsOfOriginalPosition();

		Vec3f pStartP = endPoints[0];
		// last point
		Vec3f pEndP = endPoints[1];
		Vec3f vSE = new Vec3f(pEndP.x() - pStartP.x(), pEndP.y() - pStartP.y(), pEndP.z() - pStartP.z());
		// base point ... middle of vec start -> end

		Vec3f pSpP1 =
			new Vec3f(pStartP.x() + vSE.x() / 2.0f, pStartP.y() + vSE.y() / 2.0f, pStartP.z() + vSE.z()
				/ 2.0f);

		return pSpP1;

	}

	@Override
	public final boolean isPickAble() {
		return bIsPickAble;
	}

	@Override
	public final IDrawAbleNode[] getConnectedNodes() {
		IDrawAbleNode[] nodes = new IDrawAbleNode[2];
		if (iNodeA.getID() < iNodeB.getID()) {
			nodes[0] = iNodeA;
			nodes[1] = iNodeB;
		}
		else {
			nodes[0] = iNodeB;
			nodes[1] = iNodeA;
		}

		return nodes;
	}
	
	private Vec3f foundInterceptPointWithBorder(Vec3f point1, Vec3f point2, ITreeProjection treeProjector){
		Vec3f center = treeProjector.getCenterPoint();
		Vec3f vec1 = center.minus(point1);
		Vec3f vec1n = new Vec3f(vec1);
		vec1n.normalize();
		Vec3f vec2 = center.minus(point2);
		Vec3f vec2n = new Vec3f(vec2);
		vec2n.normalize();
		Vec3f vec12 = point1.minus(point2);
		Vec3f vec12n = new Vec3f(vec12);
		vec12n.normalize();
		float lenthV0 = (float)Math.cos(vec1n.length()) * vec1.length();
		Vec3f vec02 = new Vec3f(vec2n);
		vec02.scale(vec2.length() - lenthV0);
		float alpha = (vec02.length()/vec12.length());
		Vec3f vecr2 = new Vec3f(vec2);
		vecr2.scale(vec2.length() - treeProjector.getProjectedLineFromCenterToBorder());
		Vec3f vecr1 = new Vec3f(vec12n);
		vecr1.scale(vec12.length() - vecr2.length()*(1/alpha));
		return vecr1;
	}

	// @Override
	// public final void place(List<Vec3f> lPoints) {
	// this.lPoints = lPoints;
	// }

	// protected float fRed = 0;
	// protected float fGreen = 0;
	// protected float fBlue = 0;
	// protected float fAlpha = 1;
	// protected float fThickness = 0.1f;

	// @Override
	// public final void setHighlight(boolean b) {
	// this.bHighlight = b;
	// }

	// @Override
	// public final boolean isHighlighted(){
	// return this.bHighlight;
	// }
	// protected abstract void switchColorMapping(boolean b);

	// @Override
	// public final void setConnectionAlpha(float fAlpha) {
	// this.fAlpha = fAlpha;
	// }
	//
	// @Override
	// public final void setConnectionColor3f(float fRed, float fGreen, float fBlue) {
	// this.fRed = fRed;
	// this.fGreen = fGreen;
	// this.fBlue = fBlue;
	// }

	// private boolean bHighlight;

	// @Override
	// public abstract void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);



}
//public DrawAbleHyperbolicLayoutConnector(IDrawAbleNode iNodeA, IDrawAbleNode iNodeB) {
//if (iNodeA.getID() > iNodeB.getID()) {
//	this.iNodeA = iNodeA;
//	this.iNodeB = iNodeB;
//}
//else {
//	this.iNodeA = iNodeB;
//	this.iNodeB = iNodeA;
//}
//generateID();
//this.treeProjector = null;
//// isPickable = in
//calculateSplinePoints();
//}
//if (this.iNodeA.isVisible() && this.iNodeB.isVisible())
//this.bIsVisible = true;
//else
//this.bIsVisible = false;