package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Renders the dimension bar, which indicates, which dimensions are currently
 * shown within the brick.
 * 
 * @author Christian Partl
 * 
 */
public class DimensionBarRenderer extends LayoutRenderer {

	private DimensionVirtualArray overallDimensionVA;
	private DimensionVirtualArray dimensionVA;

	/**
	 * 
	 * @param overallDimensionVA
	 *            The va for the whole data-set
	 * @param dimensionVA
	 *            The va for this dimension group
	 */
	public DimensionBarRenderer(DimensionVirtualArray overallDimensionVA,
			DimensionVirtualArray dimensionVA) {
		this.overallDimensionVA = overallDimensionVA;
		this.dimensionVA = dimensionVA;
	}

	@Override
	public void render(GL2 gl) {

		// DimensionVirtualArray overallDimensionVA = brick.getDataDomain()
		// .getDimensionVA(Set.STORAGE);
		// DimensionVirtualArray dimensionVA = brick.getDimensionVA();

		if (overallDimensionVA == null || dimensionVA == null)
			return;

		int totalNumDimensions = overallDimensionVA.size();

		float elementWidth = x / (float) totalNumDimensions;

		for (int i = 0; i < totalNumDimensions; i++) {
			float[] baseColor;
			float colorOffset;
			if (dimensionVA.contains(overallDimensionVA.get(i))) {
				baseColor = new float[] { 0.6f, 0.6f, 0.6f, 1f };
				colorOffset = -0.25f;
			} else {
				baseColor = new float[] { 0.3f, 0.3f, 0.3f, 1f };
				colorOffset = 0.25f;
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(baseColor[0] + colorOffset, baseColor[1] + colorOffset,
					baseColor[2] + colorOffset);
			gl.glVertex3f(i * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f((i + 1) * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0] - colorOffset, baseColor[1] - colorOffset,
					baseColor[2] - colorOffset);
			gl.glVertex3f((i + 1) * elementWidth, y, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f(i * elementWidth, y, 0);
			gl.glEnd();

		}

		// for (int i = 0; i < totalNumDimensions; i++) {
		// float[] baseColor;
		// if (dimensionVA.contains(overallDimensionVA.get(i))) {
		// baseColor = SelectionType.SELECTION.getColor();
		// } else {
		// baseColor = new float[] { 0.3f, 0.3f, 0.3f, 1f };
		// }
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glColor3f(baseColor[0] - 0.2f, baseColor[1] - 0.2f,
		// baseColor[2] - 0.2f);
		// gl.glVertex3f(i * elementWidth, 0, 0);
		// gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
		// gl.glVertex3f((i + 1) * elementWidth, 0, 0);
		// gl.glColor3f(baseColor[0] + 0.2f, baseColor[1] + 0.2f,
		// baseColor[2] + 0.2f);
		// gl.glVertex3f((i + 1) * elementWidth, y, 0);
		// gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
		// gl.glVertex3f(i * elementWidth, y, 0);
		// gl.glEnd();
		// }

		gl.glLineWidth(1);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

	}

}
