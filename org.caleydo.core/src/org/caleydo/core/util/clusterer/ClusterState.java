package org.caleydo.core.util.clusterer;

public class ClusterState {

	private EClustererAlgo eClustererAlgo;
	private EClustererType eClustererType;
	private EDistanceMeasure eDistanceMeasure;

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

}
