package org.caleydo.view.pathway.contextmenu;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;

/**
 * Implementation of AItemContainer for embedded pathways. By passing a pathway
 * ID all relevant context menu items are constructed automatically
 * 
 * @author Marc Streit
 */
public class EmbeddedPathwayContextMenuItemContainer extends AItemContainer {

	/**
	 * Constructor.
	 */
	public EmbeddedPathwayContextMenuItemContainer() {
		super();

		if (DataDomainManager.getInstance().getDataDomain("org.caleydo.datadomain.genetic") == null)
			throw new IllegalStateException(
					"This context menu container is only valid for genetic data");

	}

	public void setPathway(PathwayGraph pathway) {
		createMenuContent(pathway);
	}

	private void createMenuContent(PathwayGraph pathway) {
		addHeading("Load embedded pathway");
		addContextMenuItem(new LoadPathwaysByPathwayIDItem(pathway.getID()));
	}
}
