/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

/**
 * Event that signals the start of a clustering algorithm. The parameters are specified in the ClusterState
 * parameter
 *
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class StartClusteringEvent
	extends AEvent {

	private ClusterConfiguration clusterConfiguration;

	public StartClusteringEvent() {
	}

	public StartClusteringEvent(ClusterConfiguration state) {
		this.clusterConfiguration = state;
	}

	/**
	 * @param clusterConfiguration
	 *            setter, see {@link #clusterConfiguration}
	 */
	public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
		this.clusterConfiguration = clusterConfiguration;
	}

	/**
	 * @return the clusterConfiguration, see {@link #clusterConfiguration}
	 */
	public ClusterConfiguration getClusterConfiguration() {
		return clusterConfiguration;
	}

	@Override
	public boolean checkIntegrity() {
		if (clusterConfiguration == null)
			return false;
		return true;
	}

}
