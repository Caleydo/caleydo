package org.caleydo.core.util.clusterer;

/**
 * Stores the cluster state which is determined in the {@link StartClusteringAction}. Depending on the
 * selected algorithm different variables (cluster factor, cluster number) are needed.
 * 
 * @author Bernhard Schlegl
 */
public class ClusterState {

	private EClustererAlgo eClustererAlgo;
	private EClustererType eClustererType;
	private EDistanceMeasure eDistanceMeasure;
	private int iKMeansClusterCntGenes;
	private int iKMeansClusterCntExperiments;
	private float fAffinityPropClusterFactorGenes;
	private float fAffinityPropClusterFactorExperiments;

	private int iContentVaId = 0;
	private int iStorageVaId = 0;
	
	public ClusterState() {

	}

	public ClusterState(EClustererAlgo algo, EClustererType type, EDistanceMeasure dist) {
		this.setClustererAlgo(algo);
		this.setClustererType(type);
		this.setDistanceMeasure(dist);
	}

	public void setClustererAlgo(EClustererAlgo eClustererAlgo) {
		this.eClustererAlgo = eClustererAlgo;
	}

	public EClustererAlgo getClustererAlgo() {
		return eClustererAlgo;
	}

	public void setClustererType(EClustererType eClustererType) {
		this.eClustererType = eClustererType;
	}

	public EClustererType getClustererType() {
		return eClustererType;
	}

	public void setDistanceMeasure(EDistanceMeasure eDistanceMeasure) {
		this.eDistanceMeasure = eDistanceMeasure;
	}

	public EDistanceMeasure getDistanceMeasure() {
		return eDistanceMeasure;
	}

	public void setKMeansClusterCntGenes(int iKMeansClusterCntGenes) {
		this.iKMeansClusterCntGenes = iKMeansClusterCntGenes;
	}

	public int getKMeansClusterCntGenes() {
		return iKMeansClusterCntGenes;
	}

	public void setKMeansClusterCntExperiments(int iKMeansClusterCntExperiments) {
		this.iKMeansClusterCntExperiments = iKMeansClusterCntExperiments;
	}

	public int getKMeansClusterCntExperiments() {
		return iKMeansClusterCntExperiments;
	}

	public void setAffinityPropClusterFactorGenes(float fAffinityPropClusterFactorGenes) {
		this.fAffinityPropClusterFactorGenes = fAffinityPropClusterFactorGenes;
	}

	public float getAffinityPropClusterFactorGenes() {
		return fAffinityPropClusterFactorGenes;
	}

	public void setAffinityPropClusterFactorExperiments(float fAffinityPropClusterFactorExperiments) {
		this.fAffinityPropClusterFactorExperiments = fAffinityPropClusterFactorExperiments;
	}

	public float getAffinityPropClusterFactorExperiments() {
		return fAffinityPropClusterFactorExperiments;
	}

	public void setContentVaId(int iContentVaId) {
		this.iContentVaId = iContentVaId;
	}

	public int getContentVaId() {
		return iContentVaId;
	}

	public void setStorageVaId(int iStorageVaId) {
		this.iStorageVaId = iStorageVaId;
	}

	public int getStorageVaId() {
		return iStorageVaId;
	}

}
