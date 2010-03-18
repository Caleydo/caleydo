package org.caleydo.view.heatmap.heatmap.renderer;

import java.util.ArrayList;

public class ContentSpacing {
	float selectedFieldHeight;
	float normalFieldHeight;
	float fieldWidth;

	ArrayList<Float> yDistances;

	public ContentSpacing() {
		yDistances = new ArrayList<Float>();
	}

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
