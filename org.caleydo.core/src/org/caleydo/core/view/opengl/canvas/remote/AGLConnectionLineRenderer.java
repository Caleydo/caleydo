package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

/**
 * Class is responsible for rendering and drawing of connection lines (resp. planes) between views in the
 * bucket setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLConnectionLineRenderer {

	protected ConnectedElementRepresentationManager connectedElementRepManager;

	protected boolean bEnableRendering = true;

	protected EnumMap<EIDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>> hashIDTypeToViewToPointLists;

	/**
	 * Constructor.
	 */
	public AGLConnectionLineRenderer() {

		connectedElementRepManager =
			GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();

		hashIDTypeToViewToPointLists = new EnumMap<EIDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>>(EIDType.class);
	}

	public void enableRendering(final boolean bEnableRendering) {
		this.bEnableRendering = bEnableRendering;
	}

	public void init(final GL gl) {
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
	}

	public void render(final GL gl) {

		if (connectedElementRepManager.getOccuringIDTypes().size() == 0 || bEnableRendering == false)
			return;
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		renderConnectionLines(gl);
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	protected abstract void renderConnectionLines(final GL gl);
	
	/**
	 * Render straight connection lines.
	 * 
	 * @param gl
	 * @param vecSrcPoint
	 * @param vecDestPoint
	 * @param iNumberOfLines
	 * @param fArColor
	 */
	protected void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
		final int iNumberOfLines, float[] fArColor) {
		// Line shadow
		// gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
		// gl.glColor4f(28/255f, 122/255f, 254/255f, 1f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 1.5f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x()-1.5f, vecSrcPoint.y()-1.5f, vecSrcPoint.z() - 0.001f);
		gl.glVertex3f(vecDestPoint.x()-1.5f, vecDestPoint.y()-1.5f, vecDestPoint.z() - 0.001f);
		gl.glEnd();

		// gl.glColor4fv(fArColor, 0);

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		// gl.glColor4f(254/255f, 160/255f, 28/255f, 1f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x()-1.5f, vecSrcPoint.y()-1.5f, vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x()-1.5f, vecDestPoint.y()-1.5f, vecDestPoint.z());
		gl.glEnd();
	}
}
