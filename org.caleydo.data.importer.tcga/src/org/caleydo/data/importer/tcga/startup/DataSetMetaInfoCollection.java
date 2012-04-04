/**
 * 
 */
package org.caleydo.data.importer.tcga.startup;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.importing.DataSetDescription;

/**
 * Collection of all @DataTypeSet objects that are needed for loading multiple TCGA data sets.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetMetaInfoCollection {

	ArrayList<DataSetDescription> dataTypeSetCollection = new ArrayList<DataSetDescription>();

	public ArrayList<DataSetDescription> getDataTypeSetCollection() {
		return dataTypeSetCollection;
	}

	public void setDataTypeSetCollection(ArrayList<DataSetDescription> dataTypeSetCollection) {
		this.dataTypeSetCollection = dataTypeSetCollection;
	}
}
