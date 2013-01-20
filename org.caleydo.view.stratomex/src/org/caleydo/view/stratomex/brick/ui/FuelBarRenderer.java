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
package org.caleydo.view.stratomex.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Renderer for a fuel bar.
 * 
 * @author Christian Partl
 * 
 */
public class FuelBarRenderer extends LayoutRenderer {

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

		float fuelWidth = (float) x / totalNumElements * currentNumElements;

		GLStratomex stratomex = brick.getBrickColumn().getStratomexView();

		gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.BRICK.name(), brick.getID()));
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

	}
	
	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
