/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class FishEyeSpacingCalculator extends ASpacingCalculator {

	private float finalSize = 0;
	private float level1Size = 0;
	private int spread = 3;

	public FishEyeSpacingCalculator(GLHeatMap heatMap, float height, int numElements,
			int minSelectedFieldHeight) {
		super(heatMap, height, numElements);
		setMinSelectedFieldHeight(minSelectedFieldHeight);
	}

	@Override
	public void calculateFieldHeights() {
		float glMinSelectedFieldHeight = heatMap.getPixelGLConverter()
				.getGLHeightForPixelHeight(minSelectedFieldHeight);
		spread = (int) (height / (glMinSelectedFieldHeight * 3));

		Set<Integer> zoomedElements = heatMap.getZoomedElements();
		float baseSize = (height - (zoomedElements.size() * glMinSelectedFieldHeight));

		VirtualArray recordVA = heatMap.getTablePerspective().getRecordPerspective()
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
				if (indexOfZoomedElement + limitCount >= numElements)
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

		float nrRemainingElements = numElements - zoomedElements.size()
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
			VirtualArray recordVA = heatMap.getTablePerspective()
					.getRecordPerspective().getVirtualArray();
			for (int selectedContentID : heatMap.getZoomedElements()) {
				int selectedContentIndex = recordVA.indexOf(selectedContentID);
				for (int count = 1; count <= spread; count++) {
					if ((selectedContentIndex - count >= 0 && recordID == recordVA
							.get(selectedContentIndex - count)))
						return level1Size;
					else if (selectedContentIndex < numElements - count
							&& recordID == recordVA.get(selectedContentIndex + count))
						return level1Size;
				}
			}
		}

		return finalSize;
	}

}
