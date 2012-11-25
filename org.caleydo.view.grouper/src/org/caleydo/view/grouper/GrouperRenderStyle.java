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
package org.caleydo.view.grouper;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Grouper render style, specifies different style attributes of objects in
 * Grouper.
 * 
 * @author Christian Partl
 */

public class GrouperRenderStyle extends GeneralRenderStyle {

	public static final float GUI_ELEMENT_MIN_SIZE = 140.0f;
	public static final float ELEMENT_LEFT_SPACING = 0.2f;
	public static final float ELEMENT_TOP_SPACING = 0.02f;
	public static final float ELEMENT_BOTTOM_SPACING = 0.02f;

	public static final float TEXT_SCALING = 0.003f;
	public static final float TEXT_SPACING = 0.025f;
	public static final float[] TEXT_COLOR = { 0.0f, 0.0f, 0.0f, 1f };
	public static final float[] TEXT_BG_COLOR = { 0.8f, 0.8f, 0.8f, 1f };

	private ArrayList<float[]> groupLevelColors;

	/**
	 * Constructor.
	 * 
	 * @param viewFrustum
	 *            View frustum.
	 */
	public GrouperRenderStyle(ViewFrustum viewFrustum) {

		super(viewFrustum);

		groupLevelColors = new ArrayList<float[]>();
		float fArGroupColor[] = new float[4];
		fArGroupColor[0] = 215 / 255f;
		fArGroupColor[1] = 48 / 255f;
		fArGroupColor[2] = 39 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 252 / 255f;
		fArGroupColor[1] = 141 / 255f;
		fArGroupColor[2] = 89 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 254 / 255f;
		fArGroupColor[1] = 224 / 255f;
		fArGroupColor[2] = 139 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 1f;
		fArGroupColor[1] = 1f;
		fArGroupColor[2] = 191 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 217 / 255f;
		fArGroupColor[1] = 239 / 255f;
		fArGroupColor[2] = 139 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 145 / 255f;
		fArGroupColor[1] = 207 / 255f;
		fArGroupColor[2] = 96 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 26 / 255f;
		fArGroupColor[1] = 152 / 255f;
		fArGroupColor[2] = 80 / 255f;
		fArGroupColor[3] = 0.8f;
		groupLevelColors.add(fArGroupColor);
	}

	/**
	 * @return List of colors for each hierarchy level of groups.
	 */
	public ArrayList<float[]> getGroupLevelColors() {
		return groupLevelColors;
	}

	/**
	 * Sets the list of colors for each hierarchy level of groups.
	 * 
	 * @param alGroupLevelColors
	 *            List of colors for each hierarchy level of groups
	 */
	public void setGroupLevelColors(ArrayList<float[]> alGroupLevelColors) {
		this.groupLevelColors = alGroupLevelColors;
	}

	/**
	 * Gets the color for a group with the specified group level.
	 * 
	 * @param iLevel
	 *            Level for which the group color should be retrieved.
	 * @return Color as float array of length 4.
	 */
	public float[] getGroupColorForLevel(int iLevel) {

		while (iLevel >= groupLevelColors.size()) {
			iLevel -= groupLevelColors.size();
		}

		if (iLevel < 0) {
			iLevel = 0;
		}

		return groupLevelColors.get(iLevel);
	}

}
