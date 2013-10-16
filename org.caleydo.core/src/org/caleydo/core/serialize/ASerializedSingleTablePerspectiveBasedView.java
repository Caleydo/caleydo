/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Abstract class for all serialized view representations that handle a single
 * {@link ATableBasedDataDomain} (In contrast to container views that hold
 * multiple of those views).
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public abstract class ASerializedSingleTablePerspectiveBasedView extends ASerializedView {

	/** The ID string of the data domain */
	protected String dataDomainID;

	/** The key of the tablePerspective */
	protected String tablePerspectiveKey;

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ASerializedSingleTablePerspectiveBasedView() {
	}

	/**
	 * Constructor using a reference to {@link ISingleTablePerspectiveBasedView}
	 * from which the view ID and the data are automatically initialized
	 */
	public ASerializedSingleTablePerspectiveBasedView(
			ISingleTablePerspectiveBasedView singleTablePerspectiveBasedView) {
		super(singleTablePerspectiveBasedView);
		if (singleTablePerspectiveBasedView.getDataDomain() != null) {
			this.dataDomainID = singleTablePerspectiveBasedView.getDataDomain()
					.getDataDomainID();

			if (singleTablePerspectiveBasedView.getTablePerspective() != null) {
				this.tablePerspectiveKey = singleTablePerspectiveBasedView
						.getTablePerspective().getTablePerspectiveKey();
			}
		}
	}

	/**
	 * Sets the data domain associated with a view
	 *
	 * @param dataDomain
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	/**
	 * Returns the data domain a view is associated with
	 *
	 * @return
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/**
	 * @return the tablePerspectiveKey, see {@link #tablePerspectiveKey}
	 */
	public String getTablePerspectiveKey() {
		return tablePerspectiveKey;
	}

	/**
	 * @param tablePerspectiveKey
	 *            setter, see {@link #tablePerspectiveKey}
	 */
	public void setTablePerspectiveKey(String tablePerspectiveKey) {
		this.tablePerspectiveKey = tablePerspectiveKey;
	}
}
