package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVAType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;

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

	private ContentVAType contentVAType = ContentVAType.CONTENT;
	private StorageVAType storageVAType = StorageVAType.STORAGE;

	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;

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

	public void setContentVAType(ContentVAType contentVAType) {
		this.contentVAType = contentVAType;
	}

	public ContentVAType getContentVAType() {
		return contentVAType;
	}

	public void setStorageVAType(StorageVAType storageVAType) {
		this.storageVAType = storageVAType;
	}

	public StorageVAType getStorageVAType() {
		return storageVAType;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public void setStorageVA(StorageVirtualArray storageVA) {
		this.storageVA = storageVA;
	}

	public StorageVirtualArray getStorageVA() {
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
