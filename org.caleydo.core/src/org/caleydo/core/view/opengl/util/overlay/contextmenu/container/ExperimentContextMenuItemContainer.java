package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.ShowPathwaysByGeneItem;

/**
 * Implementation of AItemContainer for Experiments. You need to pass an ID of the Gene Category, which has
 * the datatype int.
 * 
 * @author Alexander Lex
 */
public class ExperimentContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public ExperimentContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase().getUseCaseMode() != EUseCaseMode.GENETIC_DATA)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	/**
	 * Set the experiment index
	 */
	public void setID(int experimentIndex) {
		createMenuContent(experimentIndex);
	}

	private void createMenuContent(int experimentIndex) {
		String sExperimentTitle = GeneralManager.get().getUseCase().getSet().get(experimentIndex).getLabel();

		addHeading(sExperimentTitle);

		BookmarkItem addToListItem = new BookmarkItem(EIDType.EXPERIMENT_INDEX, experimentIndex);
		addContextMenuItem(addToListItem);
	}
}
