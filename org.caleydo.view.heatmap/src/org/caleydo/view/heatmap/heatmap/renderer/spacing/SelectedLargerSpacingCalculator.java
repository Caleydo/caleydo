package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class SelectedLargerSpacingCalculator extends ASpacingCalculator {

	private float selectedFieldHeight;
	private float normalFieldHeight;

	public SelectedLargerSpacingCalculator(GLHeatMap heatMap, float y,
			float contentElements) {
		super(heatMap, y, contentElements);
	}

	public void calculateFieldHeights() {
		selectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
		int nrZoomedElements = heatMap.getZoomedElements().size();
		normalFieldHeight = (y - (nrZoomedElements * selectedFieldHeight))
				/ (contentElements - nrZoomedElements);
	}

	@Override
	public float getFieldHeight(int contentID) {

		if (heatMap.getContentSelectionManager().checkStatus(
				SelectionType.SELECTION, contentID)
				|| heatMap.getContentSelectionManager().checkStatus(
						SelectionType.MOUSE_OVER, contentID)) {
			return selectedFieldHeight;
		}
		return normalFieldHeight;
	}


}
