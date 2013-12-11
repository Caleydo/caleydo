/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.table;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;

/**
 * Serialized <INSERT VIEW NAME> view.
 *
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedTableView extends ASerializedSingleTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTableView() {
	}

	public SerializedTableView(ISingleTablePerspectiveBasedView view) {
		super(view);
	}

	/**
	 * @param tablePerspective
	 */
	public SerializedTableView(TablePerspective tablePerspective) {
		setDataDomainID(tablePerspective.getDataDomain().getDataDomainID());
		setTablePerspectiveKey(tablePerspective.getTablePerspectiveKey());
	}

	@Override
	public String getViewType() {
		return TableView.VIEW_TYPE;
	}
}
