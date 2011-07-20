package org.caleydo.core.serialize;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Bean that holds the initialization data for new started caleydo application. Used to store and restore
 * project or to sync remote clients.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class SerializationData {

	/** list of serialization data that makes up a data domain **/
	List<DataDomainSerializationData> dataSerializationDataList = new ArrayList<DataDomainSerializationData>();
	
	/** list of views in use, not used to sync remote clients */
	List<String> views;
	
	public List<String> getViewIDs() {
		return views;
	}

	public void setViews(List<String> viewIDs) {
		this.views = viewIDs;
	}
	
	public void addDataSerializationData(DataDomainSerializationData dataSerializationData) {
		dataSerializationDataList.add(dataSerializationData);
	}
	
	public List<DataDomainSerializationData> getDataSerializationDataList() {
		return dataSerializationDataList;
	}
}
