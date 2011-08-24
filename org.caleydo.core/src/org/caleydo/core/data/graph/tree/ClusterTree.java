package org.caleydo.core.data.graph.tree;

import org.caleydo.core.data.id.IDType;

public class ClusterTree
	extends Tree<ClusterNode> {

	public ClusterTree() {
		super();
	}

	public ClusterTree(IDType leaveIDType) {
		super(leaveIDType);
	}

//	public void createSubDataTables(DataTable table) {
//		getRoot().createSubDataTables(table);
//		NewSubDataTablesEvent event = new NewSubDataTablesEvent();
//		event.setDataDomainID(table.getDataDomain().getDataDomainID());
//		GeneralManager.get().getEventPublisher().triggerEvent(event);
//	}

}
