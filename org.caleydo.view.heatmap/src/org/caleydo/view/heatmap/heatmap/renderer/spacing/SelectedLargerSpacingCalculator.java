package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class SelectedLargerSpacingCalculator extends ASpacingCalculator {

	private float selectedFieldHeight;
	private float normalFieldHeight;

	public SelectedLargerSpacingCalculator(GLHeatMap heatMap, float y,
			float contentElements) {
		super(heatMap, y, contentElements);
	}

	@Override
	public void calculateFieldHeights() {
		selectedFieldHeight = minSelectedFieldHeight;
		int nrZoomedElements = heatMap.getZoomedElements().size();
		normalFieldHeight = (y - (nrZoomedElements * selectedFieldHeight))
				/ (contentElements - nrZoomedElements);
	}

	@Override
	public float getFieldHeight(int recordID) {

		if (heatMap.getContentSelectionManager().checkStatus(SelectionType.SELECTION,
				recordID)
				|| heatMap.getContentSelectionManager().checkStatus(
						SelectionType.MOUSE_OVER, recordID)) {
			return selectedFieldHeight;
		}
		return normalFieldHeight;
	}

}
