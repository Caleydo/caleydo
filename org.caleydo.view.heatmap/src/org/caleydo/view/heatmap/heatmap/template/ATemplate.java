package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;
import java.util.Collections;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	ArrayList<RenderParameters> verticalSpaceAllocations;

	protected TemplateRenderer templateRenderer;

	public ATemplate() {
		verticalSpaceAllocations = new ArrayList<RenderParameters>();
		// horizontalElements = new ArrayList<RenderParameters>();
	}

	public abstract void setParameters();

	public void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	public void calculateScales(float totalWidth, float totalHeight) {

		for (RenderParameters element : verticalSpaceAllocations) {
			if (!element.scaleY)
				totalHeight -= element.sizeY;
		}

		float yOffset = 0;
//		Collections.reverse(verticalSpaceAllocations);
		
		for(int count = verticalSpaceAllocations.size() -1; count >= 0; count--)
		{
//		for (RenderParameters element : verticalSpaceAllocations) {
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
	
	void add(RenderParameters element)
	{
		verticalSpaceAllocations.add(element);
	}
	
	
}
