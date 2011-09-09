package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.Set;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class FishEyeSpacingCalculator extends ASpacingCalculator {

	private float finalSize = 0;
	float level1Size = 0;
	int spread = 3;

	public FishEyeSpacingCalculator(GLHeatMap heatMap, float y, float contentElements,
			float minSelectedFieldHeight) {
		super(heatMap, y, contentElements);
		setMinSelectedFieldHeight(minSelectedFieldHeight);
	}

	@Override
	public void calculateFieldHeights() {

		spread = (int) (y / (minSelectedFieldHeight * 3));

		Set<Integer> zoomedElements = heatMap.getZoomedElements();
		float baseSize = (y - (zoomedElements.size() * minSelectedFieldHeight));

		RecordVirtualArray recordVA = heatMap.getRecordVA();

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

				int indexOfInnerZoomedElement = recordVA
						.indexOf(zoomedArray[innerCount]);
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

		finalSize = (2 * baseSize - level1Elements * minSelectedFieldHeight)
				/ (level1Elements + 2 * nrRemainingElements);

		level1Size = (finalSize + minSelectedFieldHeight) / 2;

	}

	@Override
	public float getFieldHeight(int recordID) {
		if (heatMap.getZoomedElements().contains(recordID))
			return minSelectedFieldHeight;
		else {
			RecordVirtualArray recordVA = heatMap.getRecordVA();
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
