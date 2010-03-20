package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.Set;

import javax.swing.text.ZoneView;

import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class FishEyeSpacingCalculator extends ASpacingCalculator {

	private float finalSize = 0;
	float level1Size = 0;
	int spread = 3;

	public FishEyeSpacingCalculator(GLHeatMap heatMap, float y,
			float contentElements) {
		super(heatMap, y, contentElements);

	}

	@Override
	public void calculateFieldHeights() {

		Set<Integer> zoomedElements = heatMap.getZoomedElements();
		float baseSize = (y - (zoomedElements.size() * HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT));

		ContentVirtualArray contentVA = heatMap.getContentVA();

		int level1Elements = 0;
		Integer[] zoomedArray = new Integer[zoomedElements.size()];
		zoomedElements.toArray(zoomedArray);
		for (int count = 0; count < zoomedArray.length; count++) {
			int zoomedElementID = zoomedArray[count];
			int elementsForThisSelection = spread * 2;

			int indexOfZoomedElement = contentVA.indexOf(zoomedElementID);
			for (int limitCount = 1; limitCount <= spread; limitCount++) {
				if (indexOfZoomedElement - limitCount < 0)
					elementsForThisSelection--;
				if (indexOfZoomedElement + limitCount >= contentElements)
					elementsForThisSelection--;
			}

			for (int innerCount = count + 1; innerCount < zoomedArray.length; innerCount++) {

				int indexOfInnerZoomedElement = contentVA
						.indexOf(zoomedArray[innerCount]);
				int difference = Math.abs(indexOfInnerZoomedElement
						- indexOfZoomedElement);
				if (difference <= spread * 2) {
					elementsForThisSelection -= spread * 2 - difference + 1;
				}
			}

			level1Elements += elementsForThisSelection;

		}

		float nrRemainingElements = contentElements - zoomedElements.size()
				- level1Elements;

		finalSize = (2 * baseSize - level1Elements
				* HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT)
				/ (level1Elements + 2 * nrRemainingElements);

		level1Size = (finalSize + HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT) / 2;

	}

	@Override
	public float getFieldHeight(int contentID) {
		if (heatMap.getZoomedElements().contains(contentID))
			return HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
		else {
			ContentVirtualArray contentVA = heatMap.getContentVA();
			for (int selectedContentID : heatMap.getZoomedElements()) {
				int selectedContentIndex = contentVA.indexOf(selectedContentID);
				for (int count = 1; count <= spread; count++) {
					if ((selectedContentIndex - count >= 0 && contentID == contentVA
							.get(selectedContentIndex - count)))
						return level1Size;
					else if (selectedContentIndex < contentElements - count
							&& contentID == contentVA.get(selectedContentIndex
									+ count))
						return level1Size;
				}
			}
		}

		return finalSize;
	}

}
