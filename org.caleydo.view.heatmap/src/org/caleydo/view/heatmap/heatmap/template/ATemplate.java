package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	ArrayList<RenderParameters> verticalSpaceAllocations;

	protected TemplateRenderer templateRenderer;

	private float yOverhead;

	public ATemplate() {
		verticalSpaceAllocations = new ArrayList<RenderParameters>();
		// horizontalElements = new ArrayList<RenderParameters>();
	}

	void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	abstract void setParameters();

	void calculateScales(float totalWidth, float totalHeight) {

		for (RenderParameters element : verticalSpaceAllocations) {
			if (!element.scaleY)
				totalHeight -= element.sizeY;
		}

		// take care of greedy elements in x and y
		RenderParameters greedyVerticalElement = null;
		float usedSizeY = 0;
		for (RenderParameters parameter : verticalSpaceAllocations) {
			if (parameter.grabY)
				greedyVerticalElement = parameter;
			else
				usedSizeY += parameter.sizeY;

			if (parameter instanceof Row) {
				Row row = (Row) parameter;
				float usedSizeX = 0;
				RenderParameters greedyHorizontalElement = null;

				for (RenderParameters rowElement : row) {
					if (rowElement.grabX)
						greedyHorizontalElement = rowElement;
					else
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
			RenderParameters element = verticalSpaceAllocations.get(count);
			element.transformScaledY = yOffset;
			if (element instanceof Row) {
				float xOffset = 0;
				Row row = (Row) element;
				for (RenderParameters rowElement : row) {
					row.sizeY = rowElement.sizeY;
					rowElement.transformScaledX = xOffset;
					rowElement.calculateScales(totalWidth, totalHeight);
					rowElement.transformScaledY = row.transformScaledY;
					xOffset += rowElement.sizeScaledX;
				}
			}
			element.calculateScales(totalWidth, totalHeight);
			yOffset += element.sizeScaledY;
		}
	}

	void add(RenderParameters element) {
		verticalSpaceAllocations.add(element);
	}

	public void recalculateSpacings() {
		setParameters();
	}

	public float getYOverhead() {
		return yOverhead;
	}

}
