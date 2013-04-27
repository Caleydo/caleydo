/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		this.viewID = singleTablePerspectiveBasedView.getID();
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
	 * Constructor setting the viewID, dataDomainID, tablePerspectiveKey
	 */
	public ASerializedSingleTablePerspectiveBasedView(int viewID, String dataDomainID,
			String tablePerspectiveKey) {
		this.viewID = viewID;
		this.dataDomainID = dataDomainID;
		this.tablePerspectiveKey = tablePerspectiveKey;
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
