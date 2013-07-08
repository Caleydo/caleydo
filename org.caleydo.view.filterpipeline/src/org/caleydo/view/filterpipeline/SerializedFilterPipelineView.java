/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized filter pipeline view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedFilterPipelineView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedFilterPipelineView() {
	}

	public SerializedFilterPipelineView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLFilterPipeline.VIEW_TYPE;
	}
}
