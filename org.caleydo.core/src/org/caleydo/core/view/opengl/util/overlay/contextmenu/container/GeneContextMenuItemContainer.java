package org.caleydo.core.view.opengl.util.overlay.contextmenu.container;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.AddToListItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.LoadPathwaysByGeneItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.ShowPathwaysByGeneItem;

/**
 * Implementation of AItemContainer for Genes. By passing a RefSeq int code all relevant context menu items
 * are constructed automatically
 * 
 * @author Alexander Lex
 */
public class GeneContextMenuItemContainer
	extends AItemContainer {
	/**
	 * Constructor that takes a refSeq Integer representation and creates all context menu items that are
	 * relevant for genes.
	 * 
	 * @TODO: RefSeq is probably not the best ID to use here.
	 * @param refSeqInt
	 *            a refSeq int representation
	 */
	public GeneContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase().getUseCaseMode() != EUseCaseMode.GENETIC_DATA)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	public void setStorageIndex(int iStorageIndex) {
		GeneticIDMappingHelper mappingHelper = GeneticIDMappingHelper.get();

		int davidID = mappingHelper.getDavidIDFromStorageIndex(iStorageIndex);
		createMenuContent(davidID);
	}

	public void setDavid(int davidID) {
		createMenuContent(davidID);
	}
	
	private void createMenuContent(int davidID) {
		addHeading(GeneticIDMappingHelper.get().getShortNameFromDavid(davidID));
		
		LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
		loadPathwaysByGeneItem.setDavid(davidID);
		addContextMenuItem(loadPathwaysByGeneItem);
		
		ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
		showPathwaysByGeneItem.setDavid(davidID);
		addContextMenuItem(showPathwaysByGeneItem);

		ArrayList<Integer> alStorageIndex = GeneticIDMappingHelper.get().getExpressionIndicesFromDavid(davidID);
		
		if (alStorageIndex == null)
			return;
		
		AddToListItem addToListItem =
			new AddToListItem(alStorageIndex);
		addContextMenuItem(addToListItem);
	}
}
