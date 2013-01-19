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
package org.caleydo.view.grouper.drawingstrategies.group;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;

import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupDrawingStrategyNormal extends AGroupDrawingStrategyRectangular {

	private PickingManager pickingManager;
	private GrouperRenderStyle renderStyle;
	private int viewID;
	private String dimensionPrespectiveID;

	public GroupDrawingStrategyNormal(String dimensionPerspectiveID,
			PickingManager pickingManager, int viewID, GrouperRenderStyle renderStyle) {
		this.pickingManager = pickingManager;
		this.viewID = viewID;
		this.renderStyle = renderStyle;
		this.dimensionPrespectiveID = dimensionPerspectiveID;
	}

	@Override
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(viewID,
				PickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glColor4fv(renderStyle.getGroupColorForLevel(groupRepresentation
				.getHierarchyLevel()), 0);

		// gl.glColor4f(0.74f, 0.11f, 0.18f, 0.2f);

		drawGroupRectangular(gl, groupRepresentation, textRenderer);

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(viewID,
				PickingType.GROUPER_COLLAPSE_BUTTON_SELECTION,
				groupRepresentation.getID()));

		drawCollapseButton(gl, groupRepresentation, textRenderer);

		gl.glPopName();

		gl.glPopAttrib();

		drawChildren(gl, groupRepresentation, textRenderer);

	}

	@Override
	public void drawAsLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(viewID,
				PickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		// gl.glColor4fv(GrouperRenderStyle.TEXT_BG_COLOR, 0);

		// Table table =
		// groupRepresentation.getClusterNode().getSubDataTable();
		// DimensionVirtualArray dimensionVA = table
		// .getDimensionPerspective(dimensionPrespectiveID).getVirtualArray();

		// boolean isNominal = false;
		// boolean isNumerical = false;
		// for (Integer dimensionID : dimensionVA) {

		// if (table.get(dimensionID) instanceof NominalDimension<?>) {
		// gl.glColor4f(116f / 255f, 196f / 255f, 118f / 255f, 1f);
		// isNominal = true;
		// } else {
		// gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		// isNumerical = true;
		// }
		// }
		// if (isNominal && isNumerical) {
		gl.glColor4f(1f, 0.6f, 0.6f, 1f);
		// }

		drawLeafRectangular(gl, groupRepresentation, textRenderer);

		gl.glPopAttrib();

		gl.glPopName();

	}
}
