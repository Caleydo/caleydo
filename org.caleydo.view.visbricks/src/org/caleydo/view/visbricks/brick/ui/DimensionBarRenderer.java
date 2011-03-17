package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Renders the dimension bar, which indicates, which storages are currently
 * shown within the brick.
 * 
 * @author Christian Partl
 * 
 */
public class DimensionBarRenderer extends LayoutRenderer {

	private GLBrick brick;

	public DimensionBarRenderer(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void render(GL2 gl) {

		StorageVirtualArray overallStorageVA = brick.getDataDomain()
				.getStorageVA(Set.STORAGE);
		StorageVirtualArray storageVA = brick.getStorageVA();

		if (overallStorageVA == null || storageVA == null)
			return;

		int totalNumStorages = overallStorageVA.size();

		float elementWidth = x / (float) totalNumStorages;

		for (int i = 0; i < totalNumStorages; i++) {
			float[] baseColor;
			float colorOffset;
			if (storageVA.contains(overallStorageVA.get(i))) {
				baseColor = new float[] { 0.6f, 0.6f, 0.6f, 1f };
				colorOffset = -0.25f;
			} else {
				baseColor = new float[] { 0.3f, 0.3f, 0.3f, 1f };
				colorOffset = 0.25f;
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(baseColor[0] + colorOffset,
					baseColor[1] + colorOffset, baseColor[2] + colorOffset);
			gl.glVertex3f(i * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f((i + 1) * elementWidth, 0, 0);
			gl.glColor3f(baseColor[0] - colorOffset,
					baseColor[1] - colorOffset, baseColor[2] - colorOffset);
			gl.glVertex3f((i + 1) * elementWidth, y, 0);
			gl.glColor3f(baseColor[0], baseColor[1], baseColor[2]);
			gl.glVertex3f(i * elementWidth, y, 0);
			gl.glEnd();

		}

		// for (int i = 0; i < totalNumStorages; i++) {
		// float[] baseColor;
		// if (storageVA.contains(overallStorageVA.get(i))) {
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
