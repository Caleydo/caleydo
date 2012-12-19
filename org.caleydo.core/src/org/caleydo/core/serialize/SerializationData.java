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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Bean that holds the initialization data for new started caleydo application. Used to store and restore
 * project or to sync remote clients.
 *
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class SerializationData {

	/** list of serialization data that makes up a data domain **/
	List<DataDomainSerializationData> dataSerializationDataList =
		new ArrayList<DataDomainSerializationData>();

	public void addDataDomainSerializationData(DataDomainSerializationData dataSerializationData) {
		dataSerializationDataList.add(dataSerializationData);
	}

	public List<DataDomainSerializationData> getDataDomainSerializationDataList() {
		return dataSerializationDataList;
	}

	private Map<String, Object> addonData = new HashMap<>();

	public Object getAddonData(String key) {
		return addonData.get(key);
	}

	public void setAddonData(String key, Object addon) {
		this.addonData.put(key, addon);
	}
}
