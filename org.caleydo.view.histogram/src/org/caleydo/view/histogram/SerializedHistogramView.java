/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHistogramView extends ASerializedSingleTablePerspectiveBasedView {

	public SerializedHistogramView() {
	}

	public SerializedHistogramView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLHistogram.VIEW_TYPE;
	}

}
