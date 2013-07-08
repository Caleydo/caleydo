/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized filter view view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedFilterView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedFilterView() {
	}

	public SerializedFilterView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return RcpFilterView.VIEW_TYPE;
	}
}
