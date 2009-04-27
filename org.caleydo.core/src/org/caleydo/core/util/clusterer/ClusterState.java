package org.caleydo.core.util.clusterer;

public class ClusterState {

	private EClustererAlgo eClustererAlgo;
	private EClustererType eClustererType;
	private EDistanceMeasure eDistanceMeasure;
	private int iKMeansClusterCnt;
	private float fAffinityPropClusterFactor;

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

	public void setKMeansClusterCnt(int iKMeansClusterCnt) {
		this.iKMeansClusterCnt = iKMeansClusterCnt;
	}

	public int getKMeansClusterCnt() {
		return iKMeansClusterCnt;
	}

	public void setAffinityPropClusterFactor(float fAffinityPropClusterFactor) {
		this.fAffinityPropClusterFactor = fAffinityPropClusterFactor;
	}

	public float getAffinityPropClusterFactor() {
		return fAffinityPropClusterFactor;
	}

}
