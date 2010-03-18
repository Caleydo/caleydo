package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class DetailToolBar extends ARenderer {

	GLHeatMap heatMap;
	PickingManager pickingManager;

	public DetailToolBar(GLHeatMap heatMap) {
		this.heatMap = heatMap;

	}

	@Override
	public void render(GL gl) {
		pickingManager = heatMap.getPickingManager();
		gl.glColor3f(0.0f, 0.0f, 1.0f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		float spacing = y / 10;
		float buttonSize = y - 2 * spacing;

		gl.glColor3f(1.0f, 0.0f, 1.0f);

		float sideSpacing = 2 * spacing;

		float buttonZ = 0.001f;

		if (heatMap.getContentSelectionManager().getNumberOfElements(
				GLHeatMap.SELECTION_HIDDEN) > 0) {

			gl.glTranslatef(sideSpacing, spacing, 0);
			gl.glPushName(pickingManager.getPickingID(heatMap.getID(),
					EPickingType.HEAT_MAP_HIDE_HIDDEN_ELEMENTS, 1));
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(0, 0, buttonZ);
			gl.glVertex3f(buttonSize, 0, buttonZ);
			gl.glVertex3f(buttonSize, buttonSize, buttonZ);
			gl.glVertex3f(0, buttonSize, buttonZ);
			gl.glEnd();
			gl.glPopName();

			gl.glTranslatef(-sideSpacing, -spacing, 0);
		}
		float secondButtonOffset = 2 * sideSpacing + buttonSize;
		gl.glTranslatef(secondButtonOffset, spacing, 0);
		// gl.glPushName(.getPickingID(heatMap.getID(),
		// EPickingType.HEAT_MAP_STORAGE_SELECTION, iStorageIndex));
		gl.glPushName(heatMap.getPickingManager().getPickingID(heatMap.getID(),
				EPickingType.HEAT_MAP_SHOW_CAPTIONS, 1));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, buttonZ);
		gl.glVertex3f(buttonSize, 0, buttonZ);
		gl.glVertex3f(buttonSize, buttonSize, buttonZ);
		gl.glVertex3f(0, buttonSize, buttonZ);
		gl.glEnd();
		gl.glPopName();

		gl.glTranslatef(-secondButtonOffset, -spacing, 0);

	}
}
