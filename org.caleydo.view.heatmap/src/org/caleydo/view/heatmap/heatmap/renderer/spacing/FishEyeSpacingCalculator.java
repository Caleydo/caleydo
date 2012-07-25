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
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.Set;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class FishEyeSpacingCalculator extends ASpacingCalculator {

	private float finalSize = 0;
	float level1Size = 0;
	int spread = 3;

	public FishEyeSpacingCalculator(GLHeatMap heatMap, float y, float contentElements,
			int minSelectedFieldHeight) {
		super(heatMap, y, contentElements);
		setMinSelectedFieldHeight(minSelectedFieldHeight);
	}

	@Override
	public void calculateFieldHeights() {
		float glMinSelectedFieldHeight = heatMap.getPixelGLConverter()
				.getGLHeightForPixelHeight(minSelectedFieldHeight);
		spread = (int) (y / (glMinSelectedFieldHeight * 3));

		Set<Integer> zoomedElements = heatMap.getZoomedElements();
		float baseSize = (y - (zoomedElements.size() * glMinSelectedFieldHeight));

		RecordVirtualArray recordVA = heatMap.getDataContainer().getRecordPerspective()
				.getVirtualArray();

		int level1Elements = 0;
		Integer[] zoomedArray = new Integer[zoomedElements.size()];
		zoomedElements.toArray(zoomedArray);
		for (int count = 0; count < zoomedArray.length; count++) {
			int zoomedElementID = zoomedArray[count];
			int elementsForThisSelection = spread * 2;

			int indexOfZoomedElement = recordVA.indexOf(zoomedElementID);
			for (int limitCount = 1; limitCount <= spread; limitCount++) {
				if (indexOfZoomedElement - limitCount < 0)
					elementsForThisSelection--;
				if (indexOfZoomedElement + limitCount >= recordElements)
					elementsForThisSelection--;
			}

			for (int innerCount = count + 1; innerCount < zoomedArray.length; innerCount++) {

				int indexOfInnerZoomedElement = recordVA.indexOf(zoomedArray[innerCount]);
				int difference = Math.abs(indexOfInnerZoomedElement
						- indexOfZoomedElement);
				if (difference <= spread * 2) {
					elementsForThisSelection -= spread * 2 - difference + 1;
				}
			}

			level1Elements += elementsForThisSelection;

		}

		float nrRemainingElements = recordElements - zoomedElements.size()
				- level1Elements;

		finalSize = (2 * baseSize - level1Elements * glMinSelectedFieldHeight)
				/ (level1Elements + 2 * nrRemainingElements);

		level1Size = (finalSize + glMinSelectedFieldHeight) / 2;

	}

	@Override
	public float getFieldHeight(int recordID) {
		if (heatMap.getZoomedElements().contains(recordID))
			return heatMap.getPixelGLConverter().getGLHeightForPixelHeight(minSelectedFieldHeight);
		else {
			RecordVirtualArray recordVA = heatMap.getDataContainer()
					.getRecordPerspective().getVirtualArray();
			for (int selectedContentID : heatMap.getZoomedElements()) {
				int selectedContentIndex = recordVA.indexOf(selectedContentID);
				for (int count = 1; count <= spread; count++) {
					if ((selectedContentIndex - count >= 0 && recordID == recordVA
							.get(selectedContentIndex - count)))
						return level1Size;
					else if (selectedContentIndex < recordElements - count
							&& recordID == recordVA.get(selectedContentIndex + count))
						return level1Size;
				}
			}
		}

		return finalSize;
	}

}
