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

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;

/**
 * Abstract class for all serialized view representations that handle a single {@link ATableBasedDataDomain}
 * (In contrast to container views that hold multiple of those views).
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public abstract class ASerializedTopLevelDataView
	extends ASerializedView {

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ASerializedTopLevelDataView() {
	}

	public ASerializedTopLevelDataView(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	/** The ID string of the data domain */
	protected String dataDomainID;

	/**
	 * Specifies which {@link DimensionPerspective} is used to view the data in the {@link DataTable}
	 */
	protected String dimensionPerspectiveID;

	/**
	 * Specifies which {@link recordData} is used to view the data in the {@link DataTable}
	 */
	protected String recordPerspectiveID;

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

	/** Set the {@link #recordPerspectiveID} */
	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	/** Get the {@link #recordPerspectiveID} */
	public String getRecordPerspectiveID() {
		return recordPerspectiveID;
	}

	/** Set the {@link #dimensionPerspectiveID} */
	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}

	/** Set the {@link #dimensionPerspectiveID} */
	public String getDimensionPerspectiveID() {
		return dimensionPerspectiveID;
	}
	
	
}
