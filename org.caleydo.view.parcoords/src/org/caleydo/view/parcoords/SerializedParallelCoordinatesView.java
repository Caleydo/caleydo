/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized form of a parallel-coordinates-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedParallelCoordinatesView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedParallelCoordinatesView() {
	}

	public SerializedParallelCoordinatesView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLParallelCoordinates.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLParallelCoordinates.class.getName();
	}
}
