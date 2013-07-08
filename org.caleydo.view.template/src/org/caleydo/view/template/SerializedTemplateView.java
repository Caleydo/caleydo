/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;

/**
 * Serialized <INSERT VIEW NAME> view.
 *
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedTemplateView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTemplateView() {
	}

	@Override
	public String getViewType() {
		return GLTemplateView.VIEW_TYPE;
	}
}
