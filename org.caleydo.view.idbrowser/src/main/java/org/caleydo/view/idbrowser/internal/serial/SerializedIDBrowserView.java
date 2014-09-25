/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.idbrowser.internal.serial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.idbrowser.internal.IDBrowserView;

/**
 *
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlType
public class SerializedIDBrowserView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedIDBrowserView() {
	}


	@Override
	public String getViewType() {
		return IDBrowserView.VIEW_TYPE;
	}
}
