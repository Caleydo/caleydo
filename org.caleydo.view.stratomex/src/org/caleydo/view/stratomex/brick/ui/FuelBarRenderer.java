/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Renderer for a fuel bar.
 *
 * @author Christian Partl
 *
 */
public class FuelBarRenderer extends ALayoutRenderer {

	private GLBrick brick;

	public FuelBarRenderer(GLBrick brick) {
		this.brick = brick;

	}

	@Override
	public void renderContent(GL2 gl) {

		VirtualArray recordVA = brick.getTablePerspective().getRecordPerspective()
				.getVirtualArray();

		if (recordVA == null)
			return;

		VirtualArray setRecordVA = brick.getBrickColumn().getTablePerspective()
				.getRecordPerspective().getVirtualArray();

		if (setRecordVA == null)
			return;

		int totalNumElements = setRecordVA.size();

		int currentNumElements = recordVA.size();

		float fuelWidth = x / totalNumElements * currentNumElements;

		GLStratomex stratomex = brick.getBrickColumn().getStratomexView();

		gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.BRICK.name(), brick.getID()));
		gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.BRICK_PENETRATING.name(), brick.getID()));
		// gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
		// PickingType.BRICK, brick.getID()));
		gl.glBegin(GL2.GL_QUADS);

		// if (selectionManager.checkStatus(SelectionType.SELECTION,
		// brick.getGroup()
		// .getID()))
		// gl.glColor4fv(SelectionType.SELECTION.getColor(),0);
		// else
		gl.glColor3f(0.3f, 0.3f, 0.3f);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fuelWidth, 0, 0);
		// if (selectionManager.checkStatus(SelectionType.SELECTION, brick
		// .getTablePerspective().getRecordPerspective().getTreeRoot().getID())) {
		// float[] baseColor = SelectionType.SELECTION.getColor();
		//
		// gl.glColor3f(baseColor[0] + 0.3f, baseColor[1] + 0.3f, baseColor[2] +
		// 0.3f);
		// } else
		gl.glColor3f(1f, 1f, 1f);
		gl.glVertex3f(fuelWidth, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		// gl.glPopName();
		gl.glPopName();
		gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
