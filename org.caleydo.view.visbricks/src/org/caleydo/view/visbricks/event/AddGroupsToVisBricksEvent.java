package org.caleydo.view.visbricks.event;

import java.util.ArrayList;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.container.TableBasedDimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * <p>
 * The {@link AddGroupsToVisBricksEvent} is an event that signals to add one or
 * several {@link DimensionGroup}s to {@link GLVisBricks}.
 * </p>
 * <p>
 * The event takes certain configurations of information and creates the data
 * structures behind the {@link DimensionGroup}s, the
 * {@link ADimensionGroupData}s.
 * </p>
 * <p>
 * There are three ways to specify a group to be added:
 * <ol>
 * <li>by adding a list of pre-existing {@link ADimensionGroupData}s, using the
 * {@link #AddGroupsToVisBricksEvent(ArrayList)} constructor or the
 * {@link #setDimensionGroupData(ArrayList)} method,</li>
 * <li>by specifying exactly one {@link DimensionPerspective} and one
 * {@link RecordPerspective} using the
 * {@link #AddGroupsToVisBricksEvent(String, String, String)} constructor,</li>
 * <li>or by specifying a {@link DimensionPerspective}, a
 * {@link RecordPerspective} and a list of {@link ClusterNode}s. This creates a
 * new {@link ADimensionGroupData} for every node.</li>
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 * 
 */
public class AddGroupsToVisBricksEvent extends AEvent {

	private DataContainer dataContainer;
	private ArrayList<ClusterNode> selectedNodes;
	private boolean createFromNodes = true;

	ArrayList<ADimensionGroupData> dimensionGroupData = null;

	public AddGroupsToVisBricksEvent() {
		createFromNodes = false;
	}

	/**
	 * Specify a list of pre-existing {@link ADimensionGroupData}s to be added
	 * to VisBricks
	 * 
	 * @param dimensionGroupData
	 */
	public AddGroupsToVisBricksEvent(ArrayList<ADimensionGroupData> dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
		createFromNodes = false;
	}

	/**
	 * Specify a record and dimension perspective, from which a new
	 * {@link ADimensionGroupData} is created in this event.
	 * 
	 * @param dataDomainID
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 */
	public AddGroupsToVisBricksEvent(String dataDomainID, DataContainer dataContainer) {
		this.dataDomainID = dataDomainID;
		this.dataContainer = dataContainer;
		createFromNodes = false;
	}

	/**
	 * Specify a record and dimension perspective, plus a liust of
	 * {@link ClusterNode}s. For each ClusterNode, a new
	 * {@link ADimensionGroupData} is created.
	 * 
	 * @param dataDomainID
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 * @param selectedNodes
	 */
	public AddGroupsToVisBricksEvent(String dataDomainID, DataContainer dataContainer,
			ArrayList<ClusterNode> selectedNodes) {
		this.dataDomainID = dataDomainID;
		this.dataContainer = dataContainer;
		this.selectedNodes = selectedNodes;
	}

	public boolean checkIntegrity() {
		if (dimensionGroupData == null && (dataDomainID == null || dataContainer == null))
			return false;
		if (createFromNodes && selectedNodes == null)
			return false;

		return true;
	}

	public void setDimensionGroupData(ArrayList<ADimensionGroupData> dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
	}

	public ArrayList<ADimensionGroupData> getDimensionGroupData() {
		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager
				.get().getDataDomainByID(dataDomainID);
		// case 1: pre-existing dimension group data
		if (dimensionGroupData != null)
			return dimensionGroupData;

		// case 2: exactly one dimension group data, no clusterNode
		else if (!createFromNodes && selectedNodes == null) {
			dimensionGroupData = new ArrayList<ADimensionGroupData>(1);
			TableBasedDimensionGroupData data = new TableBasedDimensionGroupData(
					dataDomain, dataContainer);
			dimensionGroupData.add(data);
			return dimensionGroupData;

		}
		// case 3: build from clusterNodes
		else {
			dimensionGroupData = new ArrayList<ADimensionGroupData>(selectedNodes.size());

			for (ClusterNode node : selectedNodes) {
				if (node.isLeaf())
					continue;
				TableBasedDimensionGroupData data = new TableBasedDimensionGroupData(
						dataDomain, dataContainer, node,
						DimensionPerspective.class);
				dimensionGroupData.add(data);
				dataDomain.addDimensionGroup(data);
			}
			return dimensionGroupData;
		}
	}

}
