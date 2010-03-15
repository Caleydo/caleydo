package org.caleydo.view.heatmap.heatmap;

public class ContentSpacing {
	float selectedFieldHeight;
	float normalFieldHeight;
	float fieldWidth;

	public void calculateContentSpacing(int contentElements,
			int storageElements, float x, float y) {
		selectedFieldHeight = y / contentElements;
		normalFieldHeight = selectedFieldHeight;

		fieldWidth = x / storageElements;
	}

	public float getSelectedFieldHeight() {
		return selectedFieldHeight;
	}

	public float getNormalFieldHeight() {
		return normalFieldHeight;
	}

	public float getFieldWidth() {
		return fieldWidth;
	}
}
