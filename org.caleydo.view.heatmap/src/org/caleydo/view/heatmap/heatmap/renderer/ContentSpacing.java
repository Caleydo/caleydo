package org.caleydo.view.heatmap.heatmap.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ContentSpacing {
	private float selectedFieldHeight;
	private float normalFieldHeight;
	private float fieldWidth;

	private boolean useFishEye = false;

	ArrayList<Float> yDistances;
	private GLHeatMap heatMap;

	public ContentSpacing(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		yDistances = new ArrayList<Float>();
	}

	public void calculateContentSpacing(int contentElements,
			int storageElements, float x, float y) {
		fieldWidth = x / storageElements;

		if (y / contentElements > HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT) {
			useFishEye = false;
			selectedFieldHeight = y / contentElements;
			normalFieldHeight = selectedFieldHeight;

		} else {
			useFishEye = true;
			selectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
			int nrZoomedElements = getZoomedElements().size();
			normalFieldHeight = (y - (nrZoomedElements * selectedFieldHeight))
					/ (contentElements - nrZoomedElements);
		}

	}

	public Set<Integer> getZoomedElements() {
		Set<Integer> zoomedElements = new HashSet<Integer>(heatMap
				.getContentSelectionManager().getElements(
						SelectionType.MOUSE_OVER));
		zoomedElements.addAll(heatMap.getContentSelectionManager().getElements(
				SelectionType.SELECTION));
		Iterator<Integer> elementIterator = zoomedElements.iterator();
		while (elementIterator.hasNext()) {
			if (heatMap.getContentVA().containsElement(elementIterator.next()) == 0)
				elementIterator.remove();
		}
		return zoomedElements;
	}

	// public float getSelectedFieldHeight() {
	// return selectedFieldHeight;
	// }
	//
	// public float getNormalFieldHeight() {
	// return normalFieldHeight;
	// }

	public float getFieldHeight(int contentID) {
		;
		if (heatMap.getContentSelectionManager().checkStatus(
				SelectionType.SELECTION, contentID)
				|| heatMap.getContentSelectionManager().checkStatus(
						SelectionType.MOUSE_OVER, contentID)) {
			return selectedFieldHeight;
		}
		return normalFieldHeight;
	}

	public float getFieldWidth() {
		return fieldWidth;
	}

	public boolean isUseFishEye() {
		return useFishEye;
	}
}
