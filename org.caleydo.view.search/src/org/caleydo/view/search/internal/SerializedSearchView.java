/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.internal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized gene search view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedSearchView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedSearchView() {
	}

	@Override
	public String getViewType() {
		return RcpSearchView.VIEW_TYPE;
	}
}
