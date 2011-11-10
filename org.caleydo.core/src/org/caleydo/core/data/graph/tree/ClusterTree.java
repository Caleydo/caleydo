package org.caleydo.core.data.graph.tree;

import org.caleydo.core.data.id.IDType;

public class ClusterTree
	extends Tree<ClusterNode> {

	/**
	 * This should only be used for de-serialization
	 */
	public ClusterTree() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param leaveIDType
	 */
	public ClusterTree(IDType leaveIDType, int expectedSize) {
		super(leaveIDType, expectedSize);
	}

	// public void createSubDataTables(DataTable table) {
	// getRoot().createSubDataTables(table);
	// NewSubDataTablesEvent event = new NewSubDataTablesEvent();
	// event.setDataDomainID(table.getDataDomain().getDataDomainID());
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }

}
