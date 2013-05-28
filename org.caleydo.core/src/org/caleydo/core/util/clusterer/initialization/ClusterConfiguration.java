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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansClusterConfiguration2;
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
 * cluster number) are required, which is reflected through the sub-classes of
 * this abstract base class.
 * </p>
 * <p>
 * The clustering algorithm uses the sourcePerspectives (
 * {@link #sourceRecordPerspective} and {@link #sourceDimensionPerspective}) and
 * writes it into the target perspectives, which can either be the source
 * Perspectives (overriding the original data), specific targetPerspectives, as
 * defined in ({@link #optionalTargetRecordPerspective} and
 * {@link #optionalTargetDimensionPerspective}), or newly created perspectives
 * (if {@link #modifyExistingPerspective} is false)
 * </p>
 *
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
@XmlType
@XmlSeeAlso({ KMeansClusterConfiguration.class, AffinityClusterConfiguration.class,
 TreeClusterConfiguration.class,
		NominalClusterConfiguration.class, KMeansClusterConfiguration2.class })
public class ClusterConfiguration {

	private EClustererTarget clusterTarget;
	private EDistanceMeasure distanceMeasure;

	/** The algorithm-specific configurations */
	private AClusterAlgorithmConfiguration clusterAlgorithmConfiguration;

	/**
	 * The record perspective which provides the source information on what to
	 * cluster. If no {@link #optionalTargetRecordPerspective} is set, the
	 * contents will eventually be overwritten
	 */
	private Perspective sourceRecordPerspective;
	/**
	 * The recordPerspective to which the results should eventually be written.
	 * If this is null, the results will be written to
	 * {@link #sourceRecordPerspective}.
	 */
	private Perspective optionalTargetRecordPerspective;

	/** same as {@link #sourceRecordPerspective} for dimensions */
	private Perspective sourceDimensionPerspective;

	/** same as {@link #optionalTargetRecordPerspective} for dimensions */
	private Perspective optionalTargetDimensionPerspective;

	/**
	 * Flag determining whether the clustered perspective should be modified
	 * (true), or whether a new one should be added with the cluster result.
	 * Default to true. This flag is also automatically set to false if a
	 * optional target perspective is specified
	 */
	private boolean modifyExistingPerspective = true;

	public ClusterConfiguration() {

	}

	public ClusterConfiguration(EClustererTarget clusterTarget, EDistanceMeasure dist) {
		this.setClusterTarget(clusterTarget);
		this.setDistanceMeasure(dist);
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #sourceRecordPerspective}
	 */
	public void setSourceRecordPerspective(Perspective recordPerspective) {
		this.sourceRecordPerspective = recordPerspective;
	}

	/**
	 * @return the recordPerspective, see {@link #sourceRecordPerspective}
	 */
	public Perspective getSourceRecordPerspective() {
		return sourceRecordPerspective;
	}

	/**
	 * @param optionalTargetRecordPerspective
	 *            setter, see {@link #optionalTargetRecordPerspective}
	 */
	public void setOptionalTargetRecordPerspective(
			Perspective optionalTargetRecordPerspective) {
		this.optionalTargetRecordPerspective = optionalTargetRecordPerspective;
		modifyExistingPerspective = false;
	}

	/**
	 * @return the optionalTargetRecordPerspective, see
	 *         {@link #optionalTargetRecordPerspective}
	 */
	public Perspective getOptionalTargetRecordPerspective() {
		return optionalTargetRecordPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #sourceDimensionPerspective}
	 */
	public void setSourceDimensionPerspective(Perspective dimensionPerspective) {
		this.sourceDimensionPerspective = dimensionPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #sourceDimensionPerspective}
	 */
	public Perspective getSourceDimensionPerspective() {
		return sourceDimensionPerspective;
	}

	@XmlTransient
	public Perspective getSourcePerspective() {
		switch (clusterTarget) {
		case DIMENSION_CLUSTERING:
			return getSourceDimensionPerspective();
		case RECORD_CLUSTERING:
			return getSourceRecordPerspective();
		}
		return null;
	}

	/**
	 * @param targetDimensionPerspective
	 *            setter, see {@link #optionalTargetDimensionPerspective}
	 */
	public void setOptionalTargetDimensionPerspective(
			Perspective targetDimensionPerspective) {
		this.optionalTargetDimensionPerspective = targetDimensionPerspective;
		modifyExistingPerspective = false;
	}

	/**
	 * @return the optionalTargetDimensionPerspective, see
	 *         {@link #optionalTargetDimensionPerspective}
	 */
	public Perspective getOptionalTargetDimensionPerspective() {
		return optionalTargetDimensionPerspective;
	}

	/**
	 * @param modifyExistingPerspective
	 *            setter, see {@link #modifyExistingPerspective}
	 */
	public void setModifyExistingPerspective(boolean modifyExistingPerspective) {
		this.modifyExistingPerspective = modifyExistingPerspective;
	}

	/**
	 * @return the modifyExistingPerspective, see
	 *         {@link #modifyExistingPerspective}
	 */
	public boolean isModifyExistingPerspective() {
		return modifyExistingPerspective;
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

	/**
	 * @param clusterAlgorithmConfiguration setter, see {@link #clusterAlgorithmConfiguration}
	 */
	public void setClusterAlgorithmConfiguration(
			AClusterAlgorithmConfiguration clusterAlgorithmConfiguration) {
		this.clusterAlgorithmConfiguration = clusterAlgorithmConfiguration;
	}

	/**
	 * @return the clusterAlgorithmConfiguration, see {@link #clusterAlgorithmConfiguration}
	 */
	public AClusterAlgorithmConfiguration getClusterAlgorithmConfiguration() {
		return clusterAlgorithmConfiguration;
	}

	@Override
	public String toString() {
		if (clusterAlgorithmConfiguration != null)
			return clusterAlgorithmConfiguration.toString();
		else
			return "ClusterConfiguration, no algorithm set";
	}

}
