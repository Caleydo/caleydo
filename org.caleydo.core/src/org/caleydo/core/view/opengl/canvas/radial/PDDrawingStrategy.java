package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class PDDrawingStrategy {

	public static int NUM_SLICES_DEFAULT = 100;

	protected int iNumSlicesPerFullDisc;

	public PDDrawingStrategy() {
		iNumSlicesPerFullDisc = NUM_SLICES_DEFAULT;
	}

	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	public int getNumSlicesPerFullDisc() {
		return iNumSlicesPerFullDisc;
	}

	public void setNumSlicesPerFullDisc(int iNumSlicesPerFullDisc) {
		this.iNumSlicesPerFullDisc = iNumSlicesPerFullDisc;
	}

}
