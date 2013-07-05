/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.selectionbrowser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized of the selection browser view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedSelectionBrowserView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedSelectionBrowserView() {
	}


	@Override
	public String getViewType() {
		return RcpSelectionBrowserView.VIEW_TYPE;
	}
}
