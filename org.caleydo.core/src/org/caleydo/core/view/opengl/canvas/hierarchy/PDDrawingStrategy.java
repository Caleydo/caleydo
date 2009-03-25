package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class PDDrawingStrategy {

	public static int NUM_SLICES_DEFAULT = 100;

	protected int iNumSlicesPerFullDisc;

	public PDDrawingStrategy() {
		iNumSlicesPerFullDisc = NUM_SLICES_DEFAULT;
	}

	public abstract void drawPartialDisk(GL gl, GLU glu, float fWidth, float fInnerRadius, float fStartAngle,
		float fAngle);

	public abstract void drawFullCircle(GL gl, GLU glu, float fRadius);

	public int getNumSlicesPerFullDisc() {
		return iNumSlicesPerFullDisc;
	}

	public void setNumSlicesPerFullDisc(int iNumSlicesPerFullDisc) {
		this.iNumSlicesPerFullDisc = iNumSlicesPerFullDisc;
	}

}
