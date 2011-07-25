package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;

/**
 * Stores the cluster state which is determined in the {@link StartClusteringAction}. Depending on the
 * selected algorithm different variables (cluster factor, cluster number) are needed.
 * 
 * @author Bernhard Schlegl
 */
@XmlType
public class ClusterState {

	private IDType contentIDType;
	private IDType storageIDType;
	private EClustererAlgo clustererAlgo;
	private EClustererType clustererType;
	private EDistanceMeasure distanceMeasure;
	private ETreeClustererAlgo treeClustererAlgo;
	private int kMeansClusterCntGenes;
	private int kMeansClusterCntExperiments;
	private float affinityPropClusterFactorGenes;
	private float affinityPropClusterFactorExperiments;

	private String contentVAType = DataTable.RECORD;
	private String storageVAType = DataTable.DIMENSION;

	private ContentVirtualArray contentVA;
	private DimensionVirtualArray storageVA;

	public ClusterState() {

	}

	public ClusterState(EClustererAlgo algo, EClustererType type, EDistanceMeasure dist) {
		this.setClustererAlgo(algo);
		this.setClustererType(type);
		this.setDistanceMeasure(dist);
	}

	public void setContentIDType(IDType contentIDType) {
		this.contentIDType = contentIDType;
	}

	public IDType getContentIDType() {
		return contentIDType;
	}

	public void setStorageIDType(IDType storageIDType) {
		this.storageIDType = storageIDType;
	}

	public IDType getStorageIDType() {
		return storageIDType;
	}

	public void setContentVAType(String contentVAType) {
		this.contentVAType = contentVAType;
	}

	public String getContentVAType() {
		return contentVAType;
	}

	public void setStorageVAType(String storageVAType) {
		this.storageVAType = storageVAType;
	}

	public String getStorageVAType() {
		return storageVAType;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public void setStorageVA(DimensionVirtualArray storageVA) {
		this.storageVA = storageVA;
	}

	public DimensionVirtualArray getStorageVA() {
		return storageVA;
	}

	public void setClustererAlgo(EClustererAlgo eClustererAlgo) {
		this.clustererAlgo = eClustererAlgo;
	}

	public EClustererAlgo getClustererAlgo() {
		return clustererAlgo;
	}

	public void setClustererType(EClustererType eClustererType) {
		this.clustererType = eClustererType;
	}

	public EClustererType getClustererType() {
		return clustererType;
	}

	public void setDistanceMeasure(EDistanceMeasure eDistanceMeasure) {
		this.distanceMeasure = eDistanceMeasure;
	}

	public EDistanceMeasure getDistanceMeasure() {
		return distanceMeasure;
	}

	public void setKMeansClusterCntGenes(int iKMeansClusterCntGenes) {
		this.kMeansClusterCntGenes = iKMeansClusterCntGenes;
	}

	public int getKMeansClusterCntGenes() {
		return kMeansClusterCntGenes;
	}

	public void setKMeansClusterCntExperiments(int iKMeansClusterCntExperiments) {
		this.kMeansClusterCntExperiments = iKMeansClusterCntExperiments;
	}

	public int getKMeansClusterCntExperiments() {
		return kMeansClusterCntExperiments;
	}

	public void setAffinityPropClusterFactorGenes(float fAffinityPropClusterFactorGenes) {
		this.affinityPropClusterFactorGenes = fAffinityPropClusterFactorGenes;
	}

	public float getAffinityPropClusterFactorGenes() {
		return affinityPropClusterFactorGenes;
	}

	public void setAffinityPropClusterFactorExperiments(float fAffinityPropClusterFactorExperiments) {
		this.affinityPropClusterFactorExperiments = fAffinityPropClusterFactorExperiments;
	}

	public float getAffinityPropClusterFactorExperiments() {
		return affinityPropClusterFactorExperiments;
	}

	public void setTreeClustererAlgo(ETreeClustererAlgo treeClustererAlgo) {
		this.treeClustererAlgo = treeClustererAlgo;
	}

	public ETreeClustererAlgo getTreeClustererAlgo() {
		return treeClustererAlgo;
	}

}
