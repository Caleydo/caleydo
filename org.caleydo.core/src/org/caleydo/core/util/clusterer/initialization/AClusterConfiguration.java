/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer.initialization;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.nominal.NominalClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeClusterConfiguration;

/**
 * <p>
 * Bean holding the information what should be clustered (sources), how it
 * should be clustered (algorithms and parameters) and where it should be
 * written (targets). Typically a cluster state is user-generated using the
 * {@link StartClusteringAction}.
 * </p>
 * <p>
 * Depending on the selected algorithm different variables (cluster factor,
 * cluster number) are required.
 * </p>
 * <p>
 * The clustering algorithm use the sourcePerspectives (
 * {@link #sourceRecordPerspective} and {@link #sourceDimensionPerspective}) and
 * intended to be written into the targetPerspectives, which can either be the
 * source Perspectives (overriding the original data), or specific
 * targetPerspectives, as defined in ({@link #optionalTargetRecordPerspective}
 * and {@link #optionalTargetDimensionPerspective}). The methods
 * {@link #getTargetRecordPerspective()} and
 * {@link #getTargetDimensionPerspective()} will always deliver the correct
 * perspective, no matter whether the perspectives are intend to be overridden
 * or other perspectives were specified
 * </p>
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
@XmlType
@XmlSeeAlso({ KMeansClusterConfiguration.class, AffinityClusterConfiguration.class,
		TreeClusterConfiguration.class, NominalClusterConfiguration.class })
public abstract class AClusterConfiguration {

	/**
	 * The name of the clustering algorithm, must be overriden in implementing
	 * classes
	 */
	protected String clusterAlgorithmName = "Unlabeled Clustering Algorithm";
	private EClustererTarget clusterTarget;
	private EDistanceMeasure distanceMeasure;

	/**
	 * The record perspective which provides the source information on what to
	 * cluster. If no {@link #optionalTargetRecordPerspective} is set, the
	 * contents will eventually be overwritten
	 */
	private RecordPerspective sourceRecordPerspective;
	/**
	 * The recordPerspective to which the results should eventually be written.
	 * If this is null, the results will be written to
	 * {@link #sourceRecordPerspective}.
	 */
	private RecordPerspective optionalTargetRecordPerspective;

	/** same as {@link #sourceRecordPerspective} for dimensions */
	private DimensionPerspective sourceDimensionPerspective;

	/** same as {@link #optionalTargetRecordPerspective} for dimensions */
	private DimensionPerspective optionalTargetDimensionPerspective;

	public AClusterConfiguration() {

	}

	public AClusterConfiguration(EClustererTarget clusterTarget, EDistanceMeasure dist) {
		this.setClusterTarget(clusterTarget);
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
	 * @param optionalTargetRecordPerspective
	 *            setter, see {@link #optionalTargetRecordPerspective}
	 */
	public void setOptionalTargetRecordPerspective(
			RecordPerspective optionalTargetRecordPerspective) {
		this.optionalTargetRecordPerspective = optionalTargetRecordPerspective;
	}

	/**
	 * Returns the {@link RecordPerspective} to which the results of the
	 * clustering algorithm should be written to. This is either the same as the
	 * source perspective (thereby overrideing data) or, if specified, a
	 * separate perspective.
	 * 
	 * @return the targetRecordPerspective, see
	 *         {@link #optionalTargetRecordPerspective}
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
	public void setOptionalTargetDimensionPerspective(
			DimensionPerspective targetDimensionPerspective) {
		this.optionalTargetDimensionPerspective = targetDimensionPerspective;
	}

	/**
	 * @return the targetDimensionPerspective, see
	 *         {@link #optionalTargetDimensionPerspective}
	 */
	public DimensionPerspective getTargetDimensionPerspective() {
		if (optionalTargetDimensionPerspective != null)
			return optionalTargetDimensionPerspective;

		return sourceDimensionPerspective;
	}

	/**
	 * @param clusterTarget
	 *            setter, see {@link #clusterTarget}
	 */
	public void setClusterTarget(EClustererTarget clusterTarget) {
		this.clusterTarget = clusterTarget;
	}

	/**
	 * @return the clusterTarget, see {@link #clusterTarget}
	 */
	public EClustererTarget getClusterTarget() {
		return clusterTarget;
	}

	/**
	 * @param distanceMeasure
	 *            setter, see {@link #distanceMeasure}
	 */
	public void setDistanceMeasure(EDistanceMeasure distanceMeasure) {
		this.distanceMeasure = distanceMeasure;
	}

	/**
	 * @return the distanceMeasure, see {@link #distanceMeasure}
	 */
	public EDistanceMeasure getDistanceMeasure() {
		return distanceMeasure;
	}

	@Override
	public String toString() {
		return clusterAlgorithmName;
	}

	/**
	 * @return the clusterAlgorithmName, see {@link #clusterAlgorithmName}
	 */
	public String getClusterAlgorithmName() {
		return clusterAlgorithmName;
	}
}
