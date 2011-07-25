package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.util.clusterer.ClusterState;

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

	private ClusterState clusterState;

	private Integer dataTableID;

	/**
	 * default no-arg constructor
	 */
	public StartClusteringEvent() {
		// nothing to initialize here
	}

	// public StartClusteringEvent(ClusterState state) {
	// this.ClusterState = state;
	// }

	public StartClusteringEvent(ClusterState state, Integer dataTableID) {
		this.clusterState = state;
		this.dataTableID = dataTableID;
	}

	public void setDataTable(Integer dataTableID) {
		this.dataTableID = dataTableID;
	}

	public Integer getDataTableID() {
		return dataTableID;
	}

	public ClusterState getClusterState() {
		return clusterState;
	}

	@Override
	public boolean checkIntegrity() {
		if (clusterState == null)
			return false;
		return true;
	}

}
