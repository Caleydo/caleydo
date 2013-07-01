/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is a serialized form reduced to hold only the view-id. It should only be used until all views
 * have their own serialized form class.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedDummyView
	extends ASerializedSingleTablePerspectiveBasedView {

	public SerializedDummyView() {
	}


	@Override
	public String getViewType() {
		return null;
	}

}
