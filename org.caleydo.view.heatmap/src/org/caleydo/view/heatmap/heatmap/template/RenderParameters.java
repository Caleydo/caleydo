package org.caleydo.view.heatmap.heatmap.template;

class RenderParameters {

	boolean scaleX = true;
	boolean scaleY = true;
	float transformX = 0;
	float transformY = 0;
	float transformScaledX = 0;
	float transformScaledY = 0;

	/** use the remaining space in X, invalidates sizeX */
	boolean grabX = false;
	/** use the remaining space in Y */
	boolean grabY = false;
	float sizeX = 0;
	float sizeY = 0;

	float sizeScaledX = 0;
	float sizeScaledY = 0;

	void calculateScales(float totalWidth, float totalHeight) {
		if (scaleX) {
			sizeScaledX = sizeX * totalWidth;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;
		} else {
			sizeScaledX = sizeX;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;

		}
		if (scaleY)
			sizeScaledY = sizeY * totalHeight;
		else
			sizeScaledY = sizeY;
	}

}
