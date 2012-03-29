/**
 * 
 */
package org.caleydo.data.importer.jkubioinfo.startup;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Collection of all @DataTypeSet objects that are needed for loading multiple TCGA data sets.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetMetaInfoCollection {

	ArrayList<DataSetMetaInfo> dataTypeSetCollection = new ArrayList<DataSetMetaInfo>();

	public ArrayList<DataSetMetaInfo> getDataTypeSetCollection() {
		return dataTypeSetCollection;
	}

	public void setDataTypeSetCollection(ArrayList<DataSetMetaInfo> dataTypeSetCollection) {
		this.dataTypeSetCollection = dataTypeSetCollection;
	}
}
