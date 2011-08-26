package org.caleydo.view.grouper.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupsToVisBricksItem extends AContextMenuItem {

	public AddGroupsToVisBricksItem(ATableBasedDataDomain dataDomain,
			String dimensionPerspectiveID, String recordPerspectiveID,
			ArrayList<ClusterNode> selectedNodes) {

		setLabel("Show Groups In VisBricks");

		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(
				dataDomain.getDataDomainID(), dimensionPerspectiveID,
				recordPerspectiveID, selectedNodes);
		event.setSender(this);

		registerEvent(event);
	}
}
