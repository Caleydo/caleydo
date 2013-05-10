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
package org.caleydo.view.stratomex.tourguide;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * @author Samuel Gratzl
 *
 */
public class Utils {
	public static final String ADD_PICKING_TYPE = "templateAdd";
	public static final String CONFIRM_PICKING_TYPE = "templateConfirm";
	public static final String CANCEL_PICKING_TYPE = "templateAbort";

	public static void renderAddButton(GL2 gl, float x, float y, float w, float h, AGLView view, int id) {
		renderButton(gl, x, y, w, h, view, ADD_PICKING_TYPE, id, "resources/icons/stratomex/template/add.png");
	}

	private static void renderButton(GL2 gl, float x, float y, float w, float h, AGLView view, String pickingType,
			int id, String texture) {
		id = view.getPickingManager().getPickingID(view.getID(), pickingType, id);
		// stratomex.addIDPickingTooltipListener("Add another column", pickingType, pickedObjectID)
		gl.glPushName(id);
		float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(32);
		float xi = x + w * 0.5f - wi * 0.5f;
		float yi = y + h * 0.5f - hi * 0.5f;
		Vec3f lowerLeftCorner = new Vec3f(xi, yi, 0.1f);
		Vec3f lowerRightCorner = new Vec3f(xi + wi, yi, 0.1f);
		Vec3f upperRightCorner = new Vec3f(xi + wi, yi + hi, 0.1f);
		Vec3f upperLeftCorner = new Vec3f(xi, yi + hi, 0.1f);

		view.getTextureManager().renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();
	}

}
