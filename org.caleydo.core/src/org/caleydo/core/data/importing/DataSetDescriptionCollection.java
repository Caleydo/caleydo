/**
 * 
 */
package org.caleydo.core.data.importing;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Collection of all @DataTypeSet objects that are needed for loading multiple
 * TCGA data sets.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataSetDescriptionCollection {

	ArrayList<DataSetDescription> dataSetDescriptionCollection = new ArrayList<DataSetDescription>();


	/**
	 * @param dataSetDescriptionCollection setter, see {@link #dataSetDescriptionCollection}
	 */
	public void setDataSetDescriptionCollection(
			ArrayList<DataSetDescription> dataSetDescriptionCollection) {
		this.dataSetDescriptionCollection = dataSetDescriptionCollection;
	}
	
	/**
	 * @return the dataSetDescriptionCollection, see {@link #dataSetDescriptionCollection}
	 */
	public ArrayList<DataSetDescription> getDataSetDescriptionCollection() {
		return dataSetDescriptionCollection;
	}
}
