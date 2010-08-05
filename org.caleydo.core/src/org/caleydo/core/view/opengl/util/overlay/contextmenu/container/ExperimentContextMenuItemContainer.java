package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
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
	}

	/**
	 * Set the experiment index
	 */
	public void setID(IDType idType, int experimentIndex) {
		createMenuContent(idType, experimentIndex);
	}

	private void createMenuContent(IDType idType, int experimentIndex) {
		String sExperimentTitle =
			((ISetBasedDataDomain) DataDomainManager.getInstance().getDataDomain(
				"org.caleydo.datadomain.genetic")).getSet().get(experimentIndex).getLabel();

		addHeading(sExperimentTitle);

		BookmarkItem addToListItem = new BookmarkItem(idType, experimentIndex);
		addContextMenuItem(addToListItem);
	}
}
