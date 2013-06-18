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
package org.caleydo.core.util.color;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;

/**
 * utility class
 * 
 * @author Samuel Gratzl
 * 
 */
public class ColorPalettes {

	static ColorMapper asColorMapper(List<Color> colors, EColorSchemeType type) {
		List<ColorMarkerPoint> colorMarkerPoints = new ArrayList<>(colors.size());

		int colorCount = 0;
		// double mappingValueDistance = 1d / (colors.size() - 1);
		// float nextMappingValue = 0;

		float spread = 0.00001f;
		// if (type == EColorSchemeType.QUALITATIVE)
		// spread = 1.0f / colors.size();
		for (Color color : colors) {
			float value = (float) colorCount / (float) (colors.size() - 1);
			ColorMarkerPoint point = new ColorMarkerPoint(value, color);

			// set spread only for first and last
			if (colorCount == 0 || (type == EColorSchemeType.QUALITATIVE && colorCount != colors.size() - 1)) {
				point.setRightSpread(spread);
			}
			if (colorCount == colors.size() - 1) {
				point.setLeftSpread(spread);

			}
			colorMarkerPoints.add(point);
			// nextMappingValue += mappingValueDistance;
			colorCount++;
		}

		return new ColorMapper(colorMarkerPoints);
	}
}
