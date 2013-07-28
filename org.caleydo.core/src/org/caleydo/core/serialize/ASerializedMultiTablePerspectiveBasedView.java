/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;

/**
 * @author Alexander Lex
 *
 */
@XmlRootElement
@XmlType
public abstract class ASerializedMultiTablePerspectiveBasedView extends ASerializedView {

	/** The ID string of the data domain */
	protected String dataDomainID;

	@XmlElement
	private ArrayList<Pair<String, String>> dataDomainAndTablePerspectiveKeys;

	/**
	 * Default Constructor, for deserialization only
	 */
	public ASerializedMultiTablePerspectiveBasedView() {
	}

	public ASerializedMultiTablePerspectiveBasedView(IMultiTablePerspectiveBasedView view) {
		super(view);
		dataDomainAndTablePerspectiveKeys = new ArrayList<Pair<String, String>>();
		for (TablePerspective tablePerspective : view.getTablePerspectives()) {
			dataDomainAndTablePerspectiveKeys.add(new Pair<String, String>(tablePerspective
					.getDataDomain().getDataDomainID(), tablePerspective
					.getTablePerspectiveKey()));
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
	 * @return the dataDomainAndTablePerspectiveKeys, see
	 *         {@link #dataDomainAndTablePerspectiveKeys}
	 */
	public ArrayList<Pair<String, String>> getDataDomainAndTablePerspectiveKeys() {
		return dataDomainAndTablePerspectiveKeys;
	}
}
