package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

/**
 * Abstract of draw able node type. This type defines objects which are self drawing, which are held by nodes.
 * 
 * @author Georg Neubauer
 */
public abstract class ADrawAbleObject
	implements IDrawAbleObject {

	protected float fXCoord = 0;
	protected float fYCoord = 0;
	protected float fZCoord = 0;
	protected float fHeight = 0;
	protected float fWidth = 0;
//	private ITreeProjection projection;
	protected boolean bIsAbleToPick;

	@Override
	public final void place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fZCoord = fZCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
	}
	
	@Override
	public final void setPickAble(boolean bIsAbleToPick){
		this.bIsAbleToPick = bIsAbleToPick;
	}
	
//	@Override
//	public final void bla(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth, ITreeProjection projection) {
//		this.fXCoord = fXCoord;
//		this.fYCoord = fYCoord;
//		this.fZCoord = fZCoord;
//		this.fHeight = fHeight;
//		this.fWidth = fWidth;
//		this.projection = projection;
//	}
	
	

	// private boolean bHighlight;
	// @Override
	// public abstract ArrayList<Vec3f> draw(GL gl, boolean bHighlight);

	// protected float fRed = 0;
	// protected float fGreen = 0;
	// protected float fBlue = 0;
	// protected float fAlpha = 1;

	// {
	// //switchColorMapping(false);
	// return drawObject(gl);
	// }

	// @Override
	// public final ArrayList<Vec3f> drawHighlight(GL gl){
	// switchColorMapping(true);
	// return drawObject(gl);
	// }
	//
	// @Override
	// public final void setBgColor3f(float fRed, float fGreen, float fBlue) {
	// this.fRed = fRed;
	// this.fGreen = fGreen;
	// this.fBlue = fBlue;
	// }
	//	
	// @Override
	// public final void setAlpha(float fAlpha) {
	// this.fAlpha = fAlpha;
	// }

	// @Override
	// private abstract ArrayList<Vec3f> drawObject(GL gl);
	//	
	// /**
	// * Switch the colormapping b=true means highlight
	// *
	// * @param b
	// */
	// //protected abstract void switchColorMapping(boolean b);
	// public final void setHighlight(boolean b){
	// this.bHighlight = b;
	// }
	//	
	// public final boolean isHighlighted(){
	// return bHighlight;
	// }
}
