package org.caleydo.view.dataflipper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

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

	public void init() {
		initialContainedViews = new ArrayList<ASerializedView>();

		// ((DataTableBasedDataDomain)
		// DataDomainManager.getInstance().getDataDomain(
		// "org.caleydo.datadomain.clinical")).updateSetInViews();

		// SerializedParallelCoordinatesView parCoords = new
		// SerializedParallelCoordinatesView();
		// parCoords.setDataDomainType("org.caleydo.datadomain.genetic");
		// initialContainedViews.add(parCoords);
		//
		// SerializedTissueViewBrowserView tissueViewBrowser = new
		// SerializedTissueViewBrowserView();
		// tissueViewBrowser.setDataDomainType("org.caleydo.datadomain.tissue");
		// initialContainedViews.add(tissueViewBrowser);
		//
		// SerializedHierarchicalHeatMapView heatMap = new
		// SerializedHierarchicalHeatMapView();
		// heatMap.setDataDomainType("org.caleydo.datadomain.genetic");
		// initialContainedViews.add(heatMap);
		//
		// parCoords = new SerializedParallelCoordinatesView();
		// parCoords.setDataDomainType("org.caleydo.datadomain.clinical");
		// initialContainedViews.add(parCoords);
		//
		// SerializedPathwayViewBrowserView pathwayViewBrowser = new
		// SerializedPathwayViewBrowserView();
		// pathwayViewBrowser.setDataDomainType("org.caleydo.datadomain.pathway");
		// initialContainedViews.add(pathwayViewBrowser);

		// SerializedGlyphView glyph = new SerializedGlyphView();
		// glyph.setDataDomainType("org.caleydo.datadomain.clinical");
		// initialContainedViews.add(glyph);
	}

	@XmlElementWrapper
	public List<ASerializedView> getInitialContainedViews() {
		return initialContainedViews;
	}

	@Override
	public String getViewType() {
		return GLDataFlipper.VIEW_TYPE;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return new ViewFrustum(CameraProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 1.9f,
				100);
	}
}
