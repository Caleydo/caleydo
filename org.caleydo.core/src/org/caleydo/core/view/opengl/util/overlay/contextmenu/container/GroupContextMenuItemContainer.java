package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.InterchangeGroupsItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.MergeClustersItem;

/**
 * Implementation of AItemContainer for groups/clusters. By passing a group number code all relevant context
 * menu items are constructed automatically
 * 
 * @author Bernhard Schlegl
 */
public class GroupContextMenuItemContainer
	extends AItemContainer {
	/**
	 * Constructor
	 * 
	 * @param groupNr
	 */
	public GroupContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase().getUseCaseMode() != EUseCaseMode.GENETIC_DATA)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	/**
	 * Sets a boolean to determine which groupList has to be used. True for genes, false for experiments
	 * 
	 * @param bGeneGroup
	 *            if true gene groups will be handled, if false experiment groups
	 */
	public void setGeneExperimentFlag(boolean bGeneGroup) {
		MergeClustersItem mergeClusters = new MergeClustersItem();
		mergeClusters.setGeneExperimentFlag(bGeneGroup);
		addContextMenuItem(mergeClusters);

		InterchangeGroupsItem interchangeGroups = new InterchangeGroupsItem();
		interchangeGroups.setGeneExperimentFlag(bGeneGroup);
		addContextMenuItem(interchangeGroups);

	}
}
