package org.caleydo.core.util.clusterer;

public class ClusterState {

	private EClustererAlgo eClustererAlgo;
	private EClustererType eClustererType;
	private EDistanceMeasure eDistanceMeasure;
	private int iKMeansClusterCntGenes;
	private int iKMeansClusterCntExperiments;
	private float fAffinityPropClusterFactorGenes;
	private float fAffinityPropClusterFactorExperiments;

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

}
