package org.caleydo.core.util.clusterer.initialization;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;

/**
 * <p>
 * Bean holding the information what should be clustered (sources), how it should be clustered (algorithms and
 * parameters) and where it should be written (targets). Typically a cluster state is user-generated using the
 * {@link StartClusteringAction}.
 * </p>
 * <p>
 * Depending on the selected algorithm different variables (cluster factor, cluster number) are required.
 * </p>
 * <p>
 * The clustering algorithm use the sourcePerspectives ({@link #sourceRecordPerspective} and
 * {@link #sourceDimensionPerspective}) and intended to be written into the targetPerspectives, which can
 * either be the source Perspectives (overriding the original data), or specific targetPerspectives, as
 * defined in ({@link #optionalTargetRecordPerspective} and {@link #optionalTargetDimensionPerspective}). The
 * methods {@link #getTargetRecordPerspective()} and {@link #getTargetDimensionPerspective()} will always
 * deliver the correct perspective, no matter whether the perspectives are intend to be overridden or other
 * perspectives were specified
 * </p>
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
@XmlType
public class ClusterConfiguration {

	private EClustererAlgo clustererAlgo;
	private ClustererType clustererType;
	private EDistanceMeasure distanceMeasure;
	private ETreeClustererAlgo treeClustererAlgo;
	private int kMeansNumberOfClustersForRecords;
	private int kMeansNumberOfClustersForDimensions;
	private float affinityPropClusterFactorGenes;
	private float affinityPropClusterFactorExperiments;

	/**
	 * The record perspective which provides the source information on what to cluster. If no
	 * {@link #optionalTargetRecordPerspective} is set, the contents will eventually be overwritten
	 */
	private RecordPerspective sourceRecordPerspective;
	/**
	 * The recordPerspective to which the results should eventually be written. If this is null, the results
	 * will be written to {@link #sourceRecordPerspective}.
	 */
	private RecordPerspective optionalTargetRecordPerspective;

	/** same as {@link #sourceRecordPerspective} for dimensions */
	private DimensionPerspective sourceDimensionPerspective;

	/** same as {@link #optionalTargetRecordPerspective} for dimensions */
	private DimensionPerspective optionalTargetDimensionPerspective;

	public ClusterConfiguration() {

	}

	public ClusterConfiguration(EClustererAlgo algo, ClustererType type, EDistanceMeasure dist) {
		this.setClustererAlgo(algo);
		this.setClustererType(type);
		this.setDistanceMeasure(dist);
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #sourceRecordPerspective}
	 */
	public void setSourceRecordPerspective(RecordPerspective recordPerspective) {
		this.sourceRecordPerspective = recordPerspective;
	}

	/**
	 * @return the recordPerspective, see {@link #sourceRecordPerspective}
	 */
	public RecordPerspective getSourceRecordPerspective() {
		return sourceRecordPerspective;
	}

	/**
	 * @param targetRecordPerspective
	 *            setter, see {@link #optionalTargetRecordPerspective}
	 */
	public void setTargetRecordPerspective(RecordPerspective targetRecordPerspective) {
		this.optionalTargetRecordPerspective = targetRecordPerspective;
	}

	/**
	 * Returns the {@link RecordPerspective} to which the results of the clustering algorithm should be
	 * written to. This is either the same as the source perspective (thereby overrideing data) or, if
	 * specified, a separate perspective.
	 * 
	 * @return the targetRecordPerspective, see {@link #optionalTargetRecordPerspective}
	 */
	public RecordPerspective getTargetRecordPerspective() {
		if (optionalTargetRecordPerspective != null)
			return optionalTargetRecordPerspective;

		return sourceRecordPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #sourceDimensionPerspective}
	 */
	public void setSourceDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.sourceDimensionPerspective = dimensionPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #sourceDimensionPerspective}
	 */
	public DimensionPerspective getSourceDimensionPerspective() {
		return sourceDimensionPerspective;
	}

	/**
	 * @param targetDimensionPerspective
	 *            setter, see {@link #optionalTargetDimensionPerspective}
	 */
	public void setOptionalTargetDimensionPerspective(DimensionPerspective targetDimensionPerspective) {
		this.optionalTargetDimensionPerspective = targetDimensionPerspective;
	}

	/**
	 * @return the targetDimensionPerspective, see {@link #optionalTargetDimensionPerspective}
	 */
	public DimensionPerspective getTargetDimensionPerspective() {
		if (optionalTargetDimensionPerspective != null)
			return optionalTargetDimensionPerspective;

		return sourceDimensionPerspective;
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

	/**
	 * @param kMeansNumberOfClustersForRecords
	 *            setter, see {@link #kMeansNumberOfClustersForRecords}
	 */
	public void setkMeansNumberOfClustersForRecords(int kMeansNumberOfClustersForRecords) {
		this.kMeansNumberOfClustersForRecords = kMeansNumberOfClustersForRecords;
	}

	/**
	 * @return the kMeansNumberOfClustersForRecords, see {@link #kMeansNumberOfClustersForRecords}
	 */
	public int getkMeansNumberOfClustersForRecords() {
		return kMeansNumberOfClustersForRecords;
	}

	/**
	 * @param kMeansNumberOfClustersForDimensions
	 *            setter, see {@link #kMeansNumberOfClustersForDimensions}
	 */
	public void setkMeansNumberOfClustersForDimensions(int kMeansNumberOfClustersForDimensions) {
		this.kMeansNumberOfClustersForDimensions = kMeansNumberOfClustersForDimensions;
	}

	/**
	 * @return the kMeansNumberOfClustersForDimensions, see {@link #kMeansNumberOfClustersForDimensions}
	 */
	public int getkMeansNumberOfClustersForDimensions() {
		return kMeansNumberOfClustersForDimensions;
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
