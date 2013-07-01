/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
