package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

public class TrippleBarPlotRenderer extends AContentRenderer {

	
	
	public TrippleBarPlotRenderer(GLHeatMap heatMap) {
		super(heatMap);

	}

	@Override
	public void updateSpacing(ElementLayout parameters) {

		AHeatMapTemplate heatMapTemplate = heatMap.getTemplate();

		int contentElements = heatMap.getContentVA().size();

		ContentSelectionManager selectionManager = heatMap
				.getContentSelectionManager();
		if (heatMap.isHideElements()) {

			contentElements -= selectionManager
					.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		contentSpacing.calculateContentSpacing(contentElements, heatMap
				.getStorageVA().size(), parameters.getSizeScaledX(), parameters
				.getSizeScaledY(), heatMapTemplate.getMinSelectedFieldHeight());
		heatMapTemplate.setContentSpacing(contentSpacing);

		// ((AContentRenderer) renderer).setContentSpacing(contentSpacing);
	}

	@Override
	public void render(final GL2 gl) {
}

	public float getYCoordinateByContentIndex(int contentIndex) {
		if (contentSpacing != null)
			return y
					- contentSpacing.getYDistances().get(contentIndex)
					- contentSpacing.getFieldHeight(heatMap.getContentVA().get(
							contentIndex)) / 2;
		return 0;
	}

	public float getXCoordinateByStorageIndex(int storageIndex) {
		return contentSpacing.getFieldWidth() * storageIndex;
	}

	@Override
	public String toString() {
		return "BarPlotRenderer";
	}

}
