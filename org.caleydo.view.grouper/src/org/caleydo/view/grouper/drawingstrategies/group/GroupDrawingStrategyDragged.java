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

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupDrawingStrategyDragged extends AGroupDrawingStrategyRectangular {

	private GrouperRenderStyle renderStyle;

	public GroupDrawingStrategyDragged(GrouperRenderStyle renderStyle) {
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {
		// Use drawDraggedGroup Method

	}

	public void drawDraggedGroup(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fGroupColor[] = renderStyle.getGroupColorForLevel(groupRepresentation
				.getHierarchyLevel());
		float fColor[] = { fGroupColor[0], fGroupColor[1], fGroupColor[2], 0.5f };
		drawDragged(gl, groupRepresentation, fMouseCoordinateX, fMouseCoordinateY,
				fDraggingStartMouseCoordinateX, fDraggingStartMouseCoordinateY, fColor);

	}

	public void drawDraggedLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fColor[] = { GrouperRenderStyle.TEXT_BG_COLOR[0],
				GrouperRenderStyle.TEXT_BG_COLOR[1], GrouperRenderStyle.TEXT_BG_COLOR[2],
				0.5f };
		drawDragged(gl, groupRepresentation, fMouseCoordinateX, fMouseCoordinateY,
				fDraggingStartMouseCoordinateX, fDraggingStartMouseCoordinateY, fColor);

	}

	private void drawDragged(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY,
			float fColor[]) {

		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		Vec3f vecRealGroupPosition = groupRepresentation.getDraggingStartPosition();
		Vec3f vecScaledRealGroupPosition = getScaledPosition(gl, vecRealGroupPosition,
				groupRepresentation.getHierarchyPosition());

		float fRealRelDraggingPosX = vecScaledRealGroupPosition.x()
				- fDraggingStartMouseCoordinateX;
		float fRealRelDraggingPosY = vecScaledRealGroupPosition.y()
				- fDraggingStartMouseCoordinateY;

		Vec3f vecPosition = new Vec3f(fMouseCoordinateX + fRealRelDraggingPosX,
				fMouseCoordinateY + fRealRelDraggingPosY, 0.2f);
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);

		gl.glColor4fv(fColor, 0);

		beginGUIElement(gl, vecPosition);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight,
				vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);

		gl.glPopAttrib();
	}

	@Override
	public void drawAsLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {
		// Use drawDraggedLeaf Method

	}

}
