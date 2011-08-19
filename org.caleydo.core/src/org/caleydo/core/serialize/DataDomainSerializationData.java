package org.caleydo.core.serialize;

import java.util.HashMap;

import org.caleydo.core.data.collection.table.DimensionPerspective;
import org.caleydo.core.data.collection.table.RecordPerspective;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;

/**
 * Bean that holds the initialization data for new started Caleydo application. Used to store and restore
 * project or to sync remote clients.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class DataDomainSerializationData {

	/** defines the type of usage of the application */
	private ATableBasedDataDomain dataDomain;

	/** content of the set file the application is based on, only used to sync remote clients */
	private byte[] setFileContent;

	/** gene cluster information, only used to sync remote clients */
	private String geneClusterTree;

	/** experiment cluster information, only used to sync remote clients */
	private String experimentClusterTree;

	/** virtual arrays of this application stored in relation with their their-key */
	private HashMap<String, RecordPerspective> recordDataMap;
	private HashMap<String, DimensionPerspective> dimensionDataMap;

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public byte[] getTableFileContent() {
		return setFileContent;
	}

	public void setTableFileContent(byte[] setFileContent) {
		this.setFileContent = setFileContent;
	}

	public HashMap<String, RecordPerspective> getRecordDataMap() {
		return recordDataMap;
	}

	public void setRecordDataMap(HashMap<String, RecordPerspective> recordDataMap) {
		this.recordDataMap = recordDataMap;
	}

	public HashMap<String, DimensionPerspective> getDimensionVAMap() {
		return dimensionDataMap;
	}

	public void setDimensionDataMap(HashMap<String, DimensionPerspective> dimensionDataMap) {
		this.dimensionDataMap = dimensionDataMap;
	}

	public String getGeneClusterTree() {
		return geneClusterTree;
	}

	public void setGeneClusterTree(String geneClusterTree) {
		this.geneClusterTree = geneClusterTree;
	}

	public String getExperimentClusterTree() {
		return experimentClusterTree;
	}

	public void setExperimentClusterTree(String experimentClusterTree) {
		this.experimentClusterTree = experimentClusterTree;
	}
}
