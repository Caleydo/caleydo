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
import org.caleydo.core.view.ISingleDataContainerBasedView;

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
public abstract class ASerializedSingleDataContainerBasedView extends ASerializedView {

	/** The ID string of the data domain */
	protected String dataDomainID;

	/** The key of the tablePerspective */
	protected String dataContainerKey;

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ASerializedSingleDataContainerBasedView() {
	}

	/**
	 * Constructor using a reference to {@link ISingleDataContainerBasedView}
	 * from which the view ID and the data are automatically initialized
	 */
	public ASerializedSingleDataContainerBasedView(
			ISingleDataContainerBasedView singleDataContainerBasedView) {
		this.viewID = singleDataContainerBasedView.getID();
		this.dataDomainID = singleDataContainerBasedView.getDataDomain()
				.getDataDomainID();
		this.dataContainerKey = singleDataContainerBasedView.getDataContainer()
				.getDataContainerKey();
	}

	/**
	 * Constructor setting the viewID, dataDomainID, dataContainerKey
	 */
	public ASerializedSingleDataContainerBasedView(int viewID, String dataDomainID,
			String dataContainerKey) {
		this.viewID = viewID;
		this.dataDomainID = dataDomainID;
		this.dataContainerKey = dataContainerKey;
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
	 * @return the dataContainerKey, see {@link #dataContainerKey}
	 */
	public String getDataContainerKey() {
		return dataContainerKey;
	}
	
	/**
	 * @param dataContainerKey setter, see {@link #dataContainerKey}
	 */
	public void setDataContainerKey(String dataContainerKey) {
		this.dataContainerKey = dataContainerKey;
	}
}
