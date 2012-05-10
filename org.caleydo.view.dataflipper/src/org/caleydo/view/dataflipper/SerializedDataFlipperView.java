/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
