/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IView;

/**
 * Serialized DVI view.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDVIView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedDVIView() {
	}

	public SerializedDVIView(IView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLDataViewIntegrator.VIEW_TYPE;
	}
}
