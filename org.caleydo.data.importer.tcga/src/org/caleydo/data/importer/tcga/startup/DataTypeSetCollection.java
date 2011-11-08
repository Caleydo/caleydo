/**
 * 
 */
package org.caleydo.data.importer.tcga.startup;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Collection of all @DataTypeSet objects that are needed for loading multiple TCGA data sets.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataTypeSetCollection {

	ArrayList<DataTypeSet> dataTypeSetCollection = new ArrayList<DataTypeSet>();

	public ArrayList<DataTypeSet> getDataTypeSetCollection() {
		return dataTypeSetCollection;
	}

	public void setDataTypeSetCollection(ArrayList<DataTypeSet> dataTypeSetCollection) {
		this.dataTypeSetCollection = dataTypeSetCollection;
	}
}
