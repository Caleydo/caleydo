package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.IDType;
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

	protected HashMap<IDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>> hashIDTypeToViewToPointLists;

	protected int activeViewID = -1;

	/**
	 * Constructor.
	 */
	public AGLConnectionLineRenderer() {

		connectedElementRepManager =
			GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();

		hashIDTypeToViewToPointLists = new HashMap<IDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>>();
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
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 1.5f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
		gl.glEnd();

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}

	/**
	 * Sets the activeViewID needed for animated lines
	 * 
	 * @param viewID
	 */
	public void setActiveViewID(int viewID) {
		activeViewID = viewID;
	}

	/**
	 * Depth-sorts the given set of points according to their z-value.
	 * 
	 * @param points
	 *            Specifies the given set of points to be depth-sorted.
	 * @return The depth-sorted set of points.
	 */
	protected ArrayList<Vec3f> depthSort(final ArrayList<Vec3f> points) {
		ArrayList<Vec3f> sortedPoints = new ArrayList<Vec3f>();
		boolean foundSpot = false;

		for (Vec3f point : points) {
			foundSpot = false;
			for (int i = 0; i < sortedPoints.size(); i++)
				if (point.z() >= sortedPoints.get(i).z()) {
					sortedPoints.add(i, point);
					foundSpot = true;
					break;
				}
			if (foundSpot == false)
				sortedPoints.add(point);
		}
		// System.out.println("Points:");
		// for(Vec3f point : points)
		// System.out.println(point.z());
		// System.out.println("Sorted Points:");
		// for(Vec3f sorted : sortedPoints)
		// System.out.println(sorted.z());
		// System.out.println("----------------------------------------");
		return sortedPoints;
	}

}
