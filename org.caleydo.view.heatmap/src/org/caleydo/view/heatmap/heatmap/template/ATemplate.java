package org.caleydo.view.heatmap.heatmap.template;

import java.util.ArrayList;

import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.renderer.CaptionCageRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.ContentSelectionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.HeatMapRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageCaptionRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.StorageSelectionRenderer;

public abstract class ATemplate {

	// super(heatMap);
	// initRenderers();

	ArrayList<RenderParameters> verticalSpaceAllocations;

	protected TemplateRenderer templateRenderer;

	private float yOverhead;

	protected float minSelectedFieldHeight = 0.1f;
	protected float fontScaling = HeatMapRenderStyle.SMALL_FONT_SCALING_FACTOR;

	protected boolean isActive;

	protected HeatMapRenderer heatMapRenderer;
	protected ContentCaptionRenderer contentCaptionRenderer;
	protected StorageCaptionRenderer storageCaptionRenderer;
	protected ContentSelectionRenderer contentSelectionRenderer;
	protected StorageSelectionRenderer storageSelectionRenderer;
	protected CaptionCageRenderer captionCageRenderer;

	public ATemplate() {
		verticalSpaceAllocations = new ArrayList<RenderParameters>();
		// horizontalElements = new ArrayList<RenderParameters>();
	}

	void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
		heatMapRenderer = new HeatMapRenderer(templateRenderer.heatMap);
		contentCaptionRenderer = new ContentCaptionRenderer(templateRenderer.heatMap);
		storageCaptionRenderer = new StorageCaptionRenderer(templateRenderer.heatMap);
		contentSelectionRenderer = new ContentSelectionRenderer(templateRenderer.heatMap);
		storageSelectionRenderer = new StorageSelectionRenderer(templateRenderer.heatMap);
		captionCageRenderer = new CaptionCageRenderer(
				templateRenderer.heatMap);
		
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
			else if (!parameter.isBackground)
				usedSizeY += parameter.sizeY;

			if (parameter instanceof Row) {
				Row row = (Row) parameter;
				float usedSizeX = 0;
				RenderParameters greedyHorizontalElement = null;

				for (RenderParameters rowElement : row) {
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
			RenderParameters element = verticalSpaceAllocations.get(count);
			element.transformScaledY = yOffset;
			if (element instanceof Row) {
				float xOffset = 0;
				Row row = (Row) element;
				for (RenderParameters rowElement : row) {
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
	void add(RenderParameters element) {
		verticalSpaceAllocations.add(element);
	}

	public void recalculateSpacings() {
		verticalSpaceAllocations.clear();
		if (templateRenderer != null)
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

	public float getMinSelectedFieldHeight() {
		return minSelectedFieldHeight;
	}
	
	public float getFontScalingFactor(){
		return fontScaling;
	}
}
