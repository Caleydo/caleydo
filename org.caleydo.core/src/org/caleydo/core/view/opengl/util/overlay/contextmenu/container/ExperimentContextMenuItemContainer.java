package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;

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

		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) == null)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	/**
	 * Set the experiment index
	 */
	public void setID(int experimentIndex) {
		createMenuContent(experimentIndex);
	}

	private void createMenuContent(int experimentIndex) {
		String sExperimentTitle =
			GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).getSet().get(experimentIndex)
				.getLabel();

		addHeading(sExperimentTitle);

		BookmarkItem addToListItem = new BookmarkItem(EIDType.EXPERIMENT_INDEX, experimentIndex);
		addContextMenuItem(addToListItem);
	}
}
