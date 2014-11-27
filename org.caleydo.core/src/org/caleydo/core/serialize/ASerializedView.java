/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.view.IView;

/**
 * Basic abstract class for all serialized view representations. A serialized
 * view is used to store a view to disk or transmit it over network.
 *
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public abstract class ASerializedView {

	private String viewLabel = "NOT SET viewLabel";
	private boolean isLabelDefault = true;

	public ASerializedView() {
	}

	public ASerializedView(IView view) {
		isLabelDefault = view.isLabelDefault();
		viewLabel = view.getLabel();
	}

	/**
	 * Retrieves the id of the view as used within the GUI-framework.
	 *
	 * @return GUI-related view-id.
	 */
	@XmlTransient
	public abstract String getViewType();

	/**
	 * @return the viewLabel, see {@link #viewLabel}
	 */
	public final String getViewLabel() {
		return viewLabel;
	}

	/**
	 * @param viewLabel
	 *            setter, see {@link viewLabel}
	 */
	public final void setViewLabel(String viewLabel) {
		this.viewLabel = viewLabel;
	}

	/**
	 * @return the isLabelDefault, see {@link #isLabelDefault}
	 */
	public final boolean isLabelDefault() {
		return isLabelDefault;
	}

	/**
	 * @param isLabelDefault
	 *            setter, see {@link isLabelDefault}
	 */
	public final void setLabelDefault(boolean isLabelDefault) {
		this.isLabelDefault = isLabelDefault;
	}
}
