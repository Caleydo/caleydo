package org.caleydo.view.datagraph.nodelayout;

import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.node.ADataNode;

public abstract class ADataNodeLayout extends ANodeLayout {
	
	protected ADataNode node;

	public ADataNodeLayout(ADataNode node, GLDataGraph view,
			DragAndDropController dragAndDropController) {
		super(view, dragAndDropController);
		this.node = node;
		// TODO Auto-generated constructor stub
	}

	

}
