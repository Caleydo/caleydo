package org.caleydo.core.util.clusterer;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DimensionPerspective;
import org.caleydo.core.data.collection.table.RecordPerspective;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;

/**
 * Stores the cluster state which is determined in the {@link StartClusteringAction}. Depending on the
 * selected algorithm different variables (cluster factor, cluster number) are needed.
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
@XmlType
public class ClusterState {

//	private String dimensionPerspectiveID;
//	private String recordPerspectiveID;
	private IDType recordIDType;
	private IDType dimensionIDType;
	private EClustererAlgo clustererAlgo;
	private ClustererType clustererType;
	private EDistanceMeasure distanceMeasure;
	private ETreeClustererAlgo treeClustererAlgo;
	private int kMeansClusterCntGenes;
	private int kMeansClusterCntExperiments;
	private float affinityPropClusterFactorGenes;
	private float affinityPropClusterFactorExperiments;

	private RecordPerspective recordPerspective;
	private DimensionPerspective dimensionPerspective;

	public ClusterState() {

	}

	public ClusterState(EClustererAlgo algo, ClustererType type, EDistanceMeasure dist) {
		this.setClustererAlgo(algo);
		this.setClustererType(type);
		this.setDistanceMeasure(dist);
	}

//	public void setRecordPerspectiveID(String recordPerspectiveID) {
//		this.recordPerspectiveID = recordPerspectiveID;
//	}
//
//	public String getRecordPerspectiveID() {
//		return recordPerspectiveID;
//	}
//
//	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
//		this.dimensionPerspectiveID = dimensionPerspectiveID;
//	}
//
//	public String getDimensionPerspectiveID() {
//		return dimensionPerspectiveID;
//	}

	public void setRecordIDType(IDType recordIDType) {
		this.recordIDType = recordIDType;
	}

	public IDType getRecordIDType() {
		return recordIDType;
	}

	public void setDimensionIDType(IDType dimensionIDType) {
		this.dimensionIDType = dimensionIDType;
	}

	public IDType getDimensionIDType() {
		return dimensionIDType;
	}

	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	public void setClustererAlgo(EClustererAlgo eClustererAlgo) {
		this.clustererAlgo = eClustererAlgo;
	}

	public EClustererAlgo getClustererAlgo() {
		return clustererAlgo;
	}

	public void setClustererType(ClustererType eClustererType) {
		this.clustererType = eClustererType;
	}

	public ClustererType getClustererType() {
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
