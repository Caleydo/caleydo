/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;

/**
 * Serialized <INSERT VIEW NAME> view.
 * 
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedEnRoutePathwayView extends ASerializedMultiTablePerspectiveBasedView {
	
	private boolean fitToViewWidth;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedEnRoutePathwayView() {
		setFitToViewWidth(true);
	}

	@Override
	public String getViewType() {
		return GLEnRoutePathway.VIEW_TYPE;
	}
	
	@Override
	public String getViewClassType() {
		return GLEnRoutePathway.class.getName();
	}
	
	/**
	 * @return the fitToViewWidth, see {@link #fitToViewWidth}
	 */
	public boolean isFitToViewWidth() {
		return fitToViewWidth;
	}
	
	/**
	 * @param fitToViewWidth setter, see {@link #fitToViewWidth}
	 */
	public void setFitToViewWidth(boolean fitToViewWidth) {
		this.fitToViewWidth = fitToViewWidth;
	}
}
