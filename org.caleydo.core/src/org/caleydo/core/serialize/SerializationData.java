package org.caleydo.core.serialize;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Bean that holds the initialization data for new started caleydo application. Used to store and restore
 * project or to sync remote clients.
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class SerializationData {

	/** list of serialization data that makes up a data domain **/
	List<DataDomainSerializationData> dataSerializationDataList = new ArrayList<DataDomainSerializationData>();
	
	public void addDataDomainSerializationData(DataDomainSerializationData dataSerializationData) {
		dataSerializationDataList.add(dataSerializationData);
	}
	
	public List<DataDomainSerializationData> getDataDomainSerializationDataList() {
		return dataSerializationDataList;
	}
}
