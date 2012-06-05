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
package org.caleydo.core.io;

import java.util.ArrayList;

import org.caleydo.core.util.clusterer.initialization.AClusterConfiguration;

/**
 * <p>
 * This class contains information on how to process the loaded data. Common
 * examples are clustering of rows or columns or filtering, sampling.
 * </p>
 * <p>
 * Currently only clustering is implemented
 * </p>
 * 
 * 
 * @author Alexander Lex
 * 
 */
public class DataProcessingDescription {

	/**
	 * <p>
	 * A list of configurations on how to cluster the column data. Only the
	 * {@link AClusterConfiguration#setDistanceMeasure(org.caleydo.core.util.clusterer.initialization.EDistanceMeasure)}
	 * and the specific parameters of the concrete implementation of the cluster
	 * configuration need to be set, <b>but not the source or target
	 * dimensions/records</b>.
	 * </p>
	 * <p>
	 * Optional.
	 * </p>
	 */
	private ArrayList<AClusterConfiguration> columnClusterConfigurations;

	/** Same as {@link #columnClusterConfigurations} */
	private ArrayList<AClusterConfiguration> rowClusterConfigurations;

	/**
	 * @param columnClusterConfigurations
	 *            setter, see {@link #columnClusterConfigurations}
	 */
	public void setColumnClusterConfigurations(
			ArrayList<AClusterConfiguration> columnClusterConfigurations) {
		this.columnClusterConfigurations = columnClusterConfigurations;
	}

	/**
	 * @return the columnClusterConfigurations, see
	 *         {@link #columnClusterConfigurations}
	 */
	public ArrayList<AClusterConfiguration> getColumnClusterConfigurations() {
		return columnClusterConfigurations;
	}

	/** Adds a cluster configuration to {@link #columnClusterConfigurations} */
	public void addColumnClusterConfiguration(
			AClusterConfiguration columnClusterConfiguration) {
		if (columnClusterConfiguration != null) {
			columnClusterConfigurations = new ArrayList<AClusterConfiguration>(3);
		}
		columnClusterConfigurations.add(columnClusterConfiguration);
	}

	/**
	 * @param rowClusterConfigurations
	 *            setter, see {@link #rowClusterConfigurations}
	 */
	public void setRowClusterConfigurations(
			ArrayList<AClusterConfiguration> rowClusterConfigurations) {
		this.rowClusterConfigurations = rowClusterConfigurations;
	}

	/**
	 * @return the rowClusterConfigurations, see
	 *         {@link #rowClusterConfigurations}
	 */
	public ArrayList<AClusterConfiguration> getRowClusterConfigurations() {
		return rowClusterConfigurations;
	}

	/** Adds a cluster configuration to {@link #rowClusterConfigurations} */
	public void addRowClusterConfiguration(AClusterConfiguration rowClusterConfiguration) {
		if (rowClusterConfiguration != null) {
			rowClusterConfigurations = new ArrayList<AClusterConfiguration>(3);
		}
		rowClusterConfigurations.add(rowClusterConfiguration);
	}

}
