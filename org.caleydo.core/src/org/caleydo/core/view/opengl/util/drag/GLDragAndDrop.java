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
package org.caleydo.core.view.opengl.util.drag;

import java.awt.Point;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;

/**
 * Object stores the view that is currently dragged until it is dropped.
 * 
 * @author Marc Streit
 */
public class GLDragAndDrop {
	private int iDragObjectId = -1;

	private boolean bDragActionRunning = false;

	private float[] fArCurrentMousePos;

	public GLDragAndDrop() {
		fArCurrentMousePos = new float[2];
	}

	public int getDraggedObjectedId() {
		return iDragObjectId;
	}

	public void startDragAction(final int iDragObjectId) {
		bDragActionRunning = true;
		this.iDragObjectId = iDragObjectId;
	}

	public void stopDragAction() {
		bDragActionRunning = false;
		iDragObjectId = -1;
	}

	public boolean isDragActionRunning() {
		return bDragActionRunning;
	}

	public void setCurrentMousePos(final GL2 gl, final Point currentMousePos) {
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL2 y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - currentMousePos.y - 1;

		GLU glu = new GLU();
		glu.gluUnProject(currentMousePos.x, realy, 0.0, //
			mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);

		fArCurrentMousePos[0] = (float) wcoord[0];
		fArCurrentMousePos[1] = (float) wcoord[1];
	}

	public void renderDragThumbnailTexture(final GL2 gl, boolean bIsZoomedIn) {
		float fOffset = 0.02f;

		float fZ = 6;
		if (bIsZoomedIn == true)
			fZ = 2;

		gl.glPushMatrix();
		gl.glTranslatef(fArCurrentMousePos[0] + fOffset, fArCurrentMousePos[1] + fOffset, fZ);
		gl.glScalef(0.05f, 0.05f, 0.05f);

		AGLView glView = RemoteElementManager.get().getItem(iDragObjectId).getGLView();

		if (glView != null) {
			glView.displayRemote(gl);
		}

		gl.glPopMatrix();
	}
}
