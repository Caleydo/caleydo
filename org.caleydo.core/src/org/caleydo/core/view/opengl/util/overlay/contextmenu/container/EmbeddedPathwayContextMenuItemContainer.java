package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.LoadPathwaysByPathwayIDItem;

/**
 * Implementation of AItemContainer for embedded pathways. By passing a pathway ID all relevant context menu
 * items are constructed automatically
 * 
 * @author Marc Streit
 */
public class EmbeddedPathwayContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public EmbeddedPathwayContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) == null)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	public void setPathway(PathwayGraph pathway) {
		createMenuContent(pathway);
	}

	private void createMenuContent(PathwayGraph pathway) {
		addHeading("Load embedded pathway");
		addContextMenuItem(new LoadPathwaysByPathwayIDItem(pathway.getID()));
	}
}
