package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	public static final float SPACING = 0.01f;
	// protected ArrayList<LayoutParameters> rendererParameters;

	protected ArrayList<LayoutParameters> verticalSpaceAllocations;

	// protected TemplateRenderer templateRenderer;

	private float yOverhead;

	protected float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	protected boolean isActive;

	public ATemplate() {
		// rendererParameters = new ArrayList<LayoutParameters>(15);
		verticalSpaceAllocations = new ArrayList<LayoutParameters>(15);
		// horizontalElements = new ArrayList<LayoutParameters>();
		// this.templateRenderer = templateRenderer;
	}

	public ArrayList<LayoutParameters> getRenderParameters() {
		return verticalSpaceAllocations;
	}

	// public void setTemplateRenderer(TemplateRenderer templateRenderer) {
	// this.templateRenderer = templateRenderer;
	//
	// }

	abstract public void setParameters();

	public void calculateScales(float totalWidth, float totalHeight) {

		for (LayoutParameters element : verticalSpaceAllocations) {
			if (!element.scaleY)
				totalHeight -= element.sizeY;
		}

		// take care of greedy elements in x and y
		LayoutParameters greedyVerticalElement = null;
		float usedSizeY = 0;
		for (LayoutParameters parameter : verticalSpaceAllocations) {
			if (parameter.grabY)
				greedyVerticalElement = parameter;
			else if (!parameter.isBackground)
				usedSizeY += parameter.sizeY;

			if (parameter instanceof Row) {
				Row row = (Row) parameter;
				float usedSizeX = 0;
				LayoutParameters greedyHorizontalElement = null;

				for (LayoutParameters rowElement : row) {
					if (rowElement.grabX)
						greedyHorizontalElement = rowElement;
					else if (!rowElement.isBackground)
						usedSizeX += rowElement.sizeX;
				}
				if (greedyHorizontalElement != null)
					greedyHorizontalElement.sizeX = 1 - usedSizeX;
			}
		}

		// calculate the actual spacings and offsets
		float yOffset = 0;
		if (greedyVerticalElement != null)
			greedyVerticalElement.sizeY = 1 - usedSizeY;

		// here we assume that the greedy element is also the "central" one
		yOverhead = usedSizeY;

		for (int count = verticalSpaceAllocations.size() - 1; count >= 0; count--) {
			LayoutParameters element = verticalSpaceAllocations.get(count);
			element.transformScaledY = yOffset;
			if (element instanceof Row) {
				float xOffset = 0;
				Row row = (Row) element;
				for (LayoutParameters rowElement : row) {
					row.sizeY = rowElement.sizeY;
					// rowElement.sizeY = row.sizeY;
					rowElement.transformScaledX = xOffset;
					rowElement.calculateScales(totalWidth, totalHeight);
					rowElement.transformScaledY = row.transformScaledY;
					if (!rowElement.isBackground)
						xOffset += rowElement.sizeScaledX;
				}
			}
			element.calculateScales(totalWidth, totalHeight);
			if (!element.isBackground)
				yOffset += element.sizeScaledY;
		}
	}

	/**
	 * Add a vertical render element
	 * 
	 * @param element
	 */
	public void addRenderElement(LayoutParameters element) {
		verticalSpaceAllocations.add(element);
	}

	public void recalculateSpacings() {
		verticalSpaceAllocations.clear();
		setParameters();
	}

	public float getYOverhead() {
		return yOverhead;
	}

	public void setActive(boolean isActive) {
		if (this.isActive != isActive) {
			this.isActive = isActive;
			recalculateSpacings();
		}
	}

	public float getFontScalingFactor() {
		return fontScaling;
	}
}
