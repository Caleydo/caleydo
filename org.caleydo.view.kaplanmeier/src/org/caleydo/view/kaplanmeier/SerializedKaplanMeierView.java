/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;

/**
 * Serialized Kaplan Meier view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedKaplanMeierView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedKaplanMeierView() {
	}

	@Override
	public String getViewType() {
		return GLKaplanMeier.VIEW_TYPE;
	}
}
