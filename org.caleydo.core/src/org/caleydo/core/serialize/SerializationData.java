/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
