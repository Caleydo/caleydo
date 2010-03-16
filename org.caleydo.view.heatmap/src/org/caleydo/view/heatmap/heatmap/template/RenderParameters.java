package org.caleydo.view.heatmap.heatmap.template;

class RenderParameters {

	boolean useRenderer;
	float transformX = 0;
	float transformY = 0;
	float transformScaledX = 0;
	float transformScaledY = 0;
	float sizeX = 0;
	float sizeY = 0;

	float sizeScaledX = 0;
	float sizeScaledY = 0;

	void calculateScales(float totalWidth, float totalHeight) {
		sizeScaledX = sizeX * totalWidth;
		sizeScaledY = sizeY * totalHeight;
		transformScaledX = transformX * totalWidth;
		transformScaledY = transformY * totalHeight;

	}

}
