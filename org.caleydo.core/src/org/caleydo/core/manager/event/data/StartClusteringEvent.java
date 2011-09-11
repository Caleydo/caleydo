package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
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

	public ClusterConfiguration getClusteConfiguration() {
		return clusterConfiguration;
	}

	@Override
	public boolean checkIntegrity() {
		if (clusterConfiguration == null)
			return false;
		return true;
	}

}
