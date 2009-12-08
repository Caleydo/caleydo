package org.caleydo.core.serialize;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.usecase.AUseCase;

/**
 * Simple bean that holds the initialization data for new started caleydo application. Used to store and
 * restore project or to sync remote clients.
 * 
 * @author Werner Puff
 */
@XmlType
@XmlRootElement
public class ApplicationInitData {

	/** defines the type of usage of the application */
	private AUseCase useCase;

	/** content of the set file the application is based on, only used to sync remote clients */
	private byte[] setFileContent;

	/** gene cluster information, only used to sync remote clients */
	private String geneClusterTree;

	/** experiment cluster information, only used to sync remote clients */
	private String experimentClusterTree;

	/** virtual arrays of this application stored in relation with their their-key */
	private HashMap<EVAType, VirtualArray> virtualArrayMap;

	/** list of views in use, not used to sync remote clients */
	private List<ASerializedView> views;

	public AUseCase getUseCase() {
		return useCase;
	}

	public void setUseCase(AUseCase useCase) {
		this.useCase = useCase;
	}

	public byte[] getSetFileContent() {
		return setFileContent;
	}

	public void setSetFileContent(byte[] setFileContent) {
		this.setFileContent = setFileContent;
	}

	public HashMap<EVAType, VirtualArray> getVirtualArrayMap() {
		return virtualArrayMap;
	}

	public void setVirtualArrayMap(HashMap<EVAType, VirtualArray> virtualArrayMap) {
		this.virtualArrayMap = virtualArrayMap;
	}

	public List<ASerializedView> getViews() {
		return views;
	}

	public void setViews(List<ASerializedView> views) {
		this.views = views;
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
