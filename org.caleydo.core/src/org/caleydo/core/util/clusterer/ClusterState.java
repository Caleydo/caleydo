package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlType;

/**
 * Stores the cluster state which is determined in the {@link StartClusteringAction}. Depending on the
 * selected algorithm different variables (cluster factor, cluster number) are needed.
 * 
 * @author Bernhard Schlegl
 */
@XmlType
public class ClusterState {

	private EClustererAlgo clustererAlgo;
	private EClustererType clustererType;
	private EDistanceMeasure distanceMeasure;
	private int kMeansClusterCntGenes;
	private int kMeansClusterCntExperiments;
	private float affinityPropClusterFactorGenes;
	private float affinityPropClusterFactorExperiments;

	private int contentVaId = 0;
	private int storageVaId = 0;
	
	public ClusterState() {

	}

	public ClusterState(EClustererAlgo algo, EClustererType type, EDistanceMeasure dist) {
		this.setClustererAlgo(algo);
		this.setClustererType(type);
		this.setDistanceMeasure(dist);
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

	public void setContentVaId(int iContentVaId) {
		this.contentVaId = iContentVaId;
	}

	public int getContentVaId() {
		return contentVaId;
	}

	public void setStorageVaId(int iStorageVaId) {
		this.storageVaId = iStorageVaId;
	}

	public int getStorageVaId() {
		return storageVaId;
	}

}
