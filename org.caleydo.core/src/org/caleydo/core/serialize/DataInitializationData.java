package org.caleydo.core.serialize;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;

/**
 * Bean that holds the initialization data for new started caleydo application. Used to store and
 * restore project or to sync remote clients.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class DataInitializationData {

	/** defines the type of usage of the application */
	private ASetBasedDataDomain dataDomain;

	/** content of the set file the application is based on, only used to sync remote clients */
	private byte[] setFileContent;

	/** gene cluster information, only used to sync remote clients */
	private String geneClusterTree;

	/** experiment cluster information, only used to sync remote clients */
	private String experimentClusterTree;

	/** virtual arrays of this application stored in relation with their their-key */
	private HashMap<ContentVAType, ContentVirtualArray> contentVAMap;
	private HashMap<StorageVAType, StorageVirtualArray> storageVAMap;

	/** list of views in use, not used to sync remote clients */
	private List<String> views;

	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public byte[] getSetFileContent() {
		return setFileContent;
	}

	public void setSetFileContent(byte[] setFileContent) {
		this.setFileContent = setFileContent;
	}

	public HashMap<ContentVAType, ContentVirtualArray> getContentVAMap() {
		return contentVAMap;
	}

	public void setContentVAMap(HashMap<ContentVAType, ContentVirtualArray> contentVAMap) {
		this.contentVAMap = contentVAMap;
	}

	public HashMap<StorageVAType, StorageVirtualArray> getStorageVAMap() {
		return storageVAMap;
	}

	public void setStorageVAMap(HashMap<StorageVAType, StorageVirtualArray> storageVAMap) {
		this.storageVAMap = storageVAMap;
	}

	public List<String> getViewIDs() {
		return views;
	}

	public void setViews(List<String> viewIDs) {
		this.views = viewIDs;
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
