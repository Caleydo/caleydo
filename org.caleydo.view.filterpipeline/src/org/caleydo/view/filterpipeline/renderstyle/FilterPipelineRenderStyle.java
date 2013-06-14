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
package org.caleydo.view.filterpipeline.renderstyle;

import java.util.ArrayList;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Render style.
 *
 * @author Thomas Geymayer
 */
public class FilterPipelineRenderStyle extends GeneralRenderStyle {
	public final float DRAG_LINE_WIDTH = 2;
	public final float FILTER_SPACING_BOTTOM = .2f;
	public final float FILTER_SPACING_TOP = .2f;

	public final Color BACKGROUND_COLOR = Color.WHITE;
	public final Color DRAG_LINE_COLOR = new Color(.2f, .2f, .2f, 1);
	public final Color DRAG_OVER_COLOR = new Color(.9f, .4f, .5f, .8f);

	public final Color FILTER_PASSED_ALL_COLOR = new Color(.2f, 0.8f, .3f, 0.5f);
	public final Color FILTER_OR_COLOR = new Color(255 / 255.f, 255 / 255.f, 179 / 255.f, 1);
	public final Color FILTER_COLOR = new Color(041 / 255.f, 211 / 255.f, 199 / 255.f, 1);
	// public final float[] FILTER_COLOR_UNCERTAINTY = {141/255.f, 150f,
	// 199/255.f, 1};
	public final Color FILTER_COLOR_UNCERTAINTY = new Color(0.7f, 0.7f, 0.7f, 1f);
	public final Color FILTER_BORDER_COLOR = new Color(0.2f, 0.2f, 0.2f, 1);

	private final static float[][] DATA_UNCERTAIN = { { 179 / 255f, 88 / 255f, 6 / 255f, 1f },
			{ 224 / 255f, 130 / 255f, 20 / 255f, 1f }, { 253 / 255f, 184 / 255f, 99 / 255f, 1f },
			{ 254 / 255f, 224 / 255f, 182 / 255f, 1f }, };

	private ArrayList<float[]> filterColors = new ArrayList<float[]>();

	public FilterPipelineRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);

		filterColors.add(new float[] { 1.f, 0.4f, 0.3f, 1 });
		filterColors.add(new float[] { 0.6f, 0.4f, 1.f, 1 });
		filterColors.add(new float[] { 0.6f, 1.f, 0.3f, 1 });
		// filterColors.add(FILTER_COLOR);
	}

	public ViewFrustum getViewFrustum() {
		return viewFrustum;
	}

	public Color getFilterColor(int filterId) {
		return new Color(filterColors.get(filterId % filterColors.size()));
	}

	public Color getFilterColorDrag(int filterId) {
		float[] color = filterColors.get(filterId % filterColors.size()).clone();
		color[3] = 0.5f;
		return new Color(color);
	}

	public float[] getFilterColorCombined(int filterId) {
		float[] color = filterColors.get(filterId % filterColors.size()).clone();
		color[3] = 0.4f;
		return color;
	}

	public Color getColorSubfilterOutput(int filterId) {
		float[] color = filterColors.get(filterId % filterColors.size()).clone();
		color[3] = 0.5f;
		return new Color(color);
	}

	public Color getColorSubfilterOutputBorder(int filterId) {
		Color color = getColorSubfilterOutput(filterId);
		color = new Color(color.r, color.g, color.b, 1f);
		return (color);
	}

	public static Color getUncertaintyColor(int level) {
		int l = level % DATA_UNCERTAIN.length;
		return new Color(DATA_UNCERTAIN[l]);
	}
}
