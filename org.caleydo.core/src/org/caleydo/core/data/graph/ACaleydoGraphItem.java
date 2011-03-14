package org.caleydo.core.data.graph;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.item.GraphItem;

public class ACaleydoGraphItem
	extends GraphItem
	implements ICaleydoGraphItem {
	/**
	 * Constructor
	 */
	public ACaleydoGraphItem(EGraphItemKind kind) {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.GRAPH_ITEM), kind);
	}

	@Override
	public int getID() {
		return super.getId();
	}

}
