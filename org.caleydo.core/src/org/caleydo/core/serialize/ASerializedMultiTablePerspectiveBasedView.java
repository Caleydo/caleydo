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
