/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * The ConnectionLineRenderer is responsible for drawing lines along specified
 * control points with different attributes (e.g. arrows).
 * 
 * @author Christian
 * 
 */
public class ConnectionLineRenderer {

	/**
	 * The list of {@link IConnectionLineAttributeRenderer} objects responsible
	 * for rendering the line attributes.
	 */
	private List<IConnectionLineAttributeRenderer> attributeRenderers = new ArrayList<IConnectionLineAttributeRenderer>();

	/**
	 * Determines whether the connection line is stippled or not.
	 */
	private boolean isLineStippled = false;

	public void renderLine(GL2 gl, List<Vec3f> linePoints) {

		gl.glColor3f(0, 0, 0);
		if (isLineStippled) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(1, (short) 255);
		}
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Vec3f point : linePoints) {
			gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		if (isLineStippled) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}

		for (IConnectionLineAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl, linePoints);
		}
	}

	/**
	 * Adds the specified attribute renderer to this connection line.
	 * 
	 * @param attributeRenderer
	 */
	public void addAttributeRenderer(IConnectionLineAttributeRenderer attributeRenderer) {
		if (attributeRenderers == null)
			attributeRenderers = new ArrayList<IConnectionLineAttributeRenderer>();

		attributeRenderers.add(attributeRenderer);
	}

	/**
	 * @return the attributeRenderers, see {@link #attributeRenderers}
	 */
	public List<IConnectionLineAttributeRenderer> getAttributeRenderers() {
		return attributeRenderers;
	}

	/**
	 * @param attributeRenderers
	 *            setter, see {@link #attributeRenderers}
	 */
	public void setAttributeRenderers(
			List<IConnectionLineAttributeRenderer> attributeRenderers) {
		this.attributeRenderers = attributeRenderers;
	}
	
	/**
	 * @param isLineStippled setter, see {@link #isLineStippled}
	 */
	public void setLineStippled(boolean isLineStippled) {
		this.isLineStippled = isLineStippled;
	}
	
	/**
	 * @return the isLineStippled, see {@link #isLineStippled}
	 */
	public boolean isLineStippled() {
		return isLineStippled;
	}
}
