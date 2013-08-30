/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.util.ArrayList;

import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.function.AdvancedFloatStatistics;

/**
 * <p>
 * This class contains information on how to process the loaded data. Common examples are clustering of rows or columns
 * or filtering, sampling.
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class DataProcessingDescription {

	/**
	 * <p>
	 * A list of configurations on how to cluster the column data. Only the
	 * {@link ClusterConfiguration#setDistanceMeasure(org.caleydo.core.util.clusterer.initialization.EDistanceMeasure)}
	 * and the specific parameters of the concrete implementation of the cluster configuration need to be set, <b>but
	 * not the source or target dimensions/records</b>.
	 * </p>
	 * <p>
	 * Optional.
	 * </p>
	 */
	private ArrayList<ClusterConfiguration> columnClusterConfigurations;

	/** Same as {@link #columnClusterConfigurations} */
	private ArrayList<ClusterConfiguration> rowClusterConfigurations;

	/**
	 * Setting this variable triggers sampling of columns to the number of elements specified. If null no sampling is
	 * done. Sampling uses the most variable elements, using Median Absolute Deviation (see
	 * {@link AdvancedFloatStatistics})
	 */
	private Integer nrColumnsInSample = null;

	/** Same as {@link #nrColumnsInSample} for rows */
	private Integer nrRowsInSample = null;

	/**
	 * @param columnClusterConfigurations
	 *            setter, see {@link #columnClusterConfigurations}
	 */
	public void setColumnClusterConfigurations(ArrayList<ClusterConfiguration> columnClusterConfigurations) {
		this.columnClusterConfigurations = columnClusterConfigurations;
	}

	/**
	 * @return the columnClusterConfigurations, see {@link #columnClusterConfigurations}
	 */
	public ArrayList<ClusterConfiguration> getColumnClusterConfigurations() {
		return columnClusterConfigurations;
	}

	/** Adds a cluster configuration to {@link #columnClusterConfigurations} */
	public void addColumnClusterConfiguration(ClusterConfiguration columnClusterConfiguration) {
		if (columnClusterConfiguration != null) {
			columnClusterConfigurations = new ArrayList<ClusterConfiguration>(3);
		}
		columnClusterConfigurations.add(columnClusterConfiguration);
	}

	/**
	 * @param rowClusterConfigurations
	 *            setter, see {@link #rowClusterConfigurations}
	 */
	public void setRowClusterConfigurations(ArrayList<ClusterConfiguration> rowClusterConfigurations) {
		this.rowClusterConfigurations = rowClusterConfigurations;
	}

	/**
	 * @return the rowClusterConfigurations, see {@link #rowClusterConfigurations}
	 */
	public ArrayList<ClusterConfiguration> getRowClusterConfigurations() {
		return rowClusterConfigurations;
	}

	/** Adds a cluster configuration to {@link #rowClusterConfigurations} */
	public void addRowClusterConfiguration(ClusterConfiguration rowClusterConfiguration) {
		if (rowClusterConfiguration != null) {
			rowClusterConfigurations = new ArrayList<ClusterConfiguration>(3);
		}
		rowClusterConfigurations.add(rowClusterConfiguration);
	}

	/**
	 * @return the nrColumnsInSample, see {@link #nrColumnsInSample}
	 */
	public Integer getNrColumnsInSample() {
		return nrColumnsInSample;
	}

	/**
	 * @param nrColumnsInSample
	 *            setter, see {@link #nrColumnsInSample}
	 */
	public void setNrColumnsInSample(Integer nrColumnsInSample) {
		this.nrColumnsInSample = nrColumnsInSample;
	}

	/**
	 * @return the nrRowsInSample, see {@link #nrRowsInSample}
	 */
	public Integer getNrRowsInSample() {
		return nrRowsInSample;
	}

	/**
	 * @param nrRowsInSample
	 *            setter, see {@link #nrRowsInSample}
	 */
	public void setNrRowsInSample(Integer nrRowsInSample) {
		this.nrRowsInSample = nrRowsInSample;
	}

}
