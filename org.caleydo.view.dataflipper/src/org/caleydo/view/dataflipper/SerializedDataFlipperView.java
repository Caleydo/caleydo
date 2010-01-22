package org.caleydo.view.dataflipper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.view.heatmap.SerializedHierarchicalHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathwaybrowser.SerializedPathwayViewBrowserView;
import org.caleydo.view.tissuebrowser.SerializedTissueViewBrowserView;

/**
 * Serialized form of the data flipper view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDataFlipperView extends ASerializedView {

	/** list of initially contained view-ids */
	private List<ASerializedView> initialContainedViews;

	/**
	 * No-Arg Constructor to create a serialized data flipper view with default
	 * parameters.
	 */
	public SerializedDataFlipperView() {
		init();
	}

	public SerializedDataFlipperView(EDataDomain dataDomain) {
		super(dataDomain);
		init();
	}

	public void init() {
		initialContainedViews = new ArrayList<ASerializedView>();

		SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
		parCoords.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(parCoords);

		SerializedTissueViewBrowserView tissueViewBrowser = new SerializedTissueViewBrowserView();
		tissueViewBrowser.setDataDomain(EDataDomain.TISSUE_DATA);
		initialContainedViews.add(tissueViewBrowser);

		SerializedHierarchicalHeatMapView heatMap = new SerializedHierarchicalHeatMapView();
		heatMap.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(heatMap);

		parCoords = new SerializedParallelCoordinatesView();
		parCoords.setDataDomain(EDataDomain.CLINICAL_DATA);
		initialContainedViews.add(parCoords);

		SerializedPathwayViewBrowserView pathwayViewBrowser = new SerializedPathwayViewBrowserView();
		pathwayViewBrowser.setDataDomain(EDataDomain.PATHWAY_DATA);
		initialContainedViews.add(pathwayViewBrowser);

		SerializedGlyphView glyph = new SerializedGlyphView();
		glyph.setDataDomain(EDataDomain.CLINICAL_DATA);
		initialContainedViews.add(glyph);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@XmlElementWrapper
	public List<ASerializedView> getInitialContainedViews() {
		return initialContainedViews;
	}

	@Override
	public String getViewType() {
		return GLDataFlipper.VIEW_ID;
	}
}
