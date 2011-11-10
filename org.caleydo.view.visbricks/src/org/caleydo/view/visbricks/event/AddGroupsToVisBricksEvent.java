package org.caleydo.view.visbricks.event;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.event.AEvent;
import org.caleydo.view.visbricks.GLVisBricks;

/**
 * <p>
 * The {@link AddGroupsToVisBricksEvent} is an event that signals to add one or
 * several {@link DataContainer}s as DimensionGroups to {@link GLVisBricks}.
 * </p>
 * <p>
 * There are two ways to specify a group to be added:
 * <ol>
 * <li>by adding a list of pre-existing {@link DataContainer}s</li>
 * <li>by specifying exactly one {@link DataContainer}</li>
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 * 
 */
public class AddGroupsToVisBricksEvent extends AEvent {

	private DataContainer dataContainer;
	// private ArrayList<ClusterNode> selectedNodes;
	// private boolean createFromNodes = true;

	ArrayList<DataContainer> subDataContainers = null;

	public AddGroupsToVisBricksEvent() {
		// createFromNodes = false;
	}

	/**
	 * Specify a list of pre-existing {@link ADimensionGroupData}s to be added
	 * to VisBricks
	 * 
	 * @param subDataContainers
	 */
	public AddGroupsToVisBricksEvent(ArrayList<DataContainer> subDataContainers) {
		this.subDataContainers = subDataContainers;
		// createFromNodes = false;
	}

	/**
	 * Specify a record and dimension perspective, from which a new
	 * {@link ADimensionGroupData} is created in this event.
	 * 
	 * @param dataDomainID
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 */
	public AddGroupsToVisBricksEvent(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
		// createFromNodes = false;
	}

	/**
	 * Specify a record and dimension perspective, plus a liust of
	 * {@link ClusterNode}s. For each ClusterNode, a new {@link DataContainer}
	 * is created.
	 * 
	 * @param dataDomainID
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 * @param selectedNodes
	 */
	// public AddGroupsToVisBricksEvent(String dataDomainID, DataContainer
	// dataContainer,
	// ArrayList<ClusterNode> selectedNodes) {
	// this.dataDomainID = dataDomainID;
	// this.dataContainer = dataContainer;
	// this.selectedNodes = selectedNodes;
	// }

	@Override
	public boolean checkIntegrity() {
		if (subDataContainers == null && (dataDomainID == null || dataContainer == null))
			return false;
		// if (createFromNodes && selectedNodes == null)
		// return false;

		return true;
	}

	public void setDataContainers(ArrayList<DataContainer> dataContainers) {
		this.subDataContainers = dataContainers;
	}

	public ArrayList<DataContainer> getDataContainers() {

		// case 1: pre-existing dimension group data list
		if (subDataContainers != null)
			return subDataContainers;

		// case 2: exactly one dimension group data
		else if (dataContainer != null) {
			subDataContainers = new ArrayList<DataContainer>(1);
			subDataContainers.add(dataContainer);
			return subDataContainers;

		} else {
			throw new IllegalStateException("Event illegaly initialized");
		}
		// case 3: build from clusterNodes
		// else {
		// subDataContainers = new
		// ArrayList<DataContainer>(selectedNodes.size());
		//
		// for (ClusterNode node : selectedNodes) {
		// // FIXME: this should be removed as soon as the selected nodes are
		// correct
		// if (node.isLeaf())
		// continue;
		//
		// DimensionPerspective dimensionPerspective = node.getSubPerspective(
		// DimensionPerspective.class, dataDomain);
		//
		//
		// DataContainer subDataContainer = new DataContainer(dataDomain,
		// dataContainer.getRecordPerspective(), dimensionPerspective);
		// subDataContainers.add(subDataContainer);
		// }
		// return subDataContainers;
		// }
	}

}
