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
package org.caleydo.view.scatterplot;

public class ScatterPlotHelper {

	public static float getSpace(float x, float y) {
		return x * y;
	}

	public static boolean getCorrelation(float x, float y) {
		float fCorrelation = 1.3f;
		if ((x / y) > fCorrelation)
			return false;
		if ((y / x) > fCorrelation)
			return false;

		return true;
	}

	public static float[] getSelectionColor(int iColorNr) {

		float fBrighness = 0.9f;
		int iWhiteness = 20;

		int color = (iColorNr + 7) % 10;

		float[] fArMappingColor = new float[] { 0F, 0F, 0F, 0F };

		switch (color) {
		case 1:
			fArMappingColor = new float[] { 31, 120, 180, 1 };
			break;
		case 2:
			fArMappingColor = new float[] { 178, 223, 138, 1 };
			break;
		case 3:
			fArMappingColor = new float[] { 51, 160, 44, 1 };
			break;
		case 4:
			fArMappingColor = new float[] { 251, 154, 153, 1 };
			break;
		case 5:
			fArMappingColor = new float[] { 227, 26, 28, 1 };
			break;
		case 6:
			fArMappingColor = new float[] { 166, 206, 227, 1 };
			break;
		case 7:
			fArMappingColor = new float[] { 253, 191, 111, 1 };
			break;
		case 8:
			fArMappingColor = new float[] { 255, 127, 0, 1 };
			break;
		case 9:
			fArMappingColor = new float[] { 202, 178, 214, 1 };
			break;
		case 0:
			fArMappingColor = new float[] { 106, 61, 154, 1 };
			break;

		default:
			fArMappingColor = new float[] { 0, 0, 0, 1 };
		}

		for (int i = 0; i < 3; i++) {
			fArMappingColor[i] = fBrighness * ((fArMappingColor[i] - iWhiteness) / 255f);
			if (fArMappingColor[i] > 1)
				fArMappingColor[i] = 1;
			if (fArMappingColor[i] < 0)
				fArMappingColor[i] = 0;
		}
		return fArMappingColor;

	}

}
