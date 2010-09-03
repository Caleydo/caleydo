package org.caleydo.view.grouper;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Grouper render style, specifies different style attributes of objects in
 * Grouper.
 * 
 * @author Christian
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

	private ArrayList<float[]> alGroupLevelColors;

	/**
	 * Constructor.
	 * 
	 * @param viewFrustum
	 *            View frustum.
	 */
	public GrouperRenderStyle(IViewFrustum viewFrustum) {

		super(viewFrustum);

		alGroupLevelColors = new ArrayList<float[]>();
		float fArGroupColor[] = new float[4];
		fArGroupColor[0] = 215 / 255f;
		fArGroupColor[1] = 48 / 255f;
		fArGroupColor[2] = 39 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 252 / 255f;
		fArGroupColor[1] = 141 / 255f;
		fArGroupColor[2] = 89 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 254 / 255f;
		fArGroupColor[1] = 224 / 255f;
		fArGroupColor[2] = 139 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 1f;
		fArGroupColor[1] = 1f;
		fArGroupColor[2] = 191 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 217 / 255f;
		fArGroupColor[1] = 239 / 255f;
		fArGroupColor[2] = 139 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 145 / 255f;
		fArGroupColor[1] = 207 / 255f;
		fArGroupColor[2] = 96 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);

		fArGroupColor = new float[4];
		fArGroupColor[0] = 26 / 255f;
		fArGroupColor[1] = 152 / 255f;
		fArGroupColor[2] = 80 / 255f;
		fArGroupColor[3] = 0.8f;
		alGroupLevelColors.add(fArGroupColor);
	}

	/**
	 * @return List of colors for each hierarchy level of groups.
	 */
	public ArrayList<float[]> getGroupLevelColors() {
		return alGroupLevelColors;
	}

	/**
	 * Sets the list of colors for each hierarchy level of groups.
	 * 
	 * @param alGroupLevelColors
	 *            List of colors for each hierarchy level of groups
	 */
	public void setGroupLevelColors(ArrayList<float[]> alGroupLevelColors) {
		this.alGroupLevelColors = alGroupLevelColors;
	}

	/**
	 * Gets the color for a group with the specified group level.
	 * 
	 * @param iLevel
	 *            Level for which the group color should be retrieved.
	 * @return Color as float array of length 4.
	 */
	public float[] getGroupColorForLevel(int iLevel) {

		while (iLevel >= alGroupLevelColors.size()) {
			iLevel -= alGroupLevelColors.size();
		}

		if (iLevel < 0) {
			iLevel = 0;
		}

		return alGroupLevelColors.get(iLevel);
	}

}
