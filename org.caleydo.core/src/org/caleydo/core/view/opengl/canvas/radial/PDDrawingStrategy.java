package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.PickingManager;

public abstract class PDDrawingStrategy {

	protected int iNumSlicesPerFullDisc;
	protected PickingManager pickingManager;
	protected int iViewID;

	public PDDrawingStrategy(PickingManager pickingManager, int iViewID) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;		
		iNumSlicesPerFullDisc = RadialHierarchyRenderStyle.NUM_SLICES_PER_FULL_DISC;
	}

	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	public int getNumSlicesPerFullDisc() {
		return iNumSlicesPerFullDisc;
	}

	public void setNumSlicesPerFullDisc(int iNumSlicesPerFullDisc) {
		this.iNumSlicesPerFullDisc = iNumSlicesPerFullDisc;
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public void setPickingManager(PickingManager pickingManager) {
		this.pickingManager = pickingManager;
	}

	public int getViewID() {
		return iViewID;
	}

	public void setViewID(int iViewID) {
		this.iViewID = iViewID;
	}
	
	

}
