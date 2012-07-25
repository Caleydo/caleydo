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

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IMultiDataContainerBasedView;

/**
 * @author Alexander Lex
 * 
 */
@XmlRootElement
@XmlType
public abstract class ASerializedMultiDataContainerBasedView extends ASerializedView {

	/** The ID string of the data domain */
	protected String dataDomainID;

	@XmlElement
	private ArrayList<Pair<String, String>> dataDomainAndDataContainerKeys;

	/**
	 * Default Constructor, for deserialization only
	 */
	public ASerializedMultiDataContainerBasedView() {
	}

	public ASerializedMultiDataContainerBasedView(IMultiDataContainerBasedView view) {
		dataDomainAndDataContainerKeys = new ArrayList<Pair<String, String>>();
		for (DataContainer dataContainer : view.getDataContainers()) {
			dataDomainAndDataContainerKeys.add(new Pair<String, String>(dataContainer
					.getDataDomain().getDataDomainID(), dataContainer
					.getDataContainerKey()));
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
	 * @return the dataDomainAndDataContainerKeys, see
	 *         {@link #dataDomainAndDataContainerKeys}
	 */
	public ArrayList<Pair<String, String>> getDataDomainAndDataContainerKeys() {
		return dataDomainAndDataContainerKeys;
	}
	

}
