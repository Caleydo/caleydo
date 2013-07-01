/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized form of a heatmap view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHeatMapView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedHeatMapView() {

	}

	public SerializedHeatMapView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	public SerializedHeatMapView(int viewID, String dataDomainID, String tablePerspectiveKey) {
		super(viewID, dataDomainID, tablePerspectiveKey);
	}

	@Override
	public String getViewType() {
		return GLHeatMap.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLHeatMap.class.getName();
	}
}
