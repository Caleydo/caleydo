/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;

/**
 * Serialized VisBricks view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedStratomexView extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedStratomexView() {
	}

	public SerializedStratomexView(GLStratomex stratomeX) {
		super(stratomeX);
	}

	@Override
	public String getViewType() {
		return GLStratomex.VIEW_TYPE;
	}

}
