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
 * Implementation of AItemContainer for Genes. By passing a RefSeq int code all relevant context menu items
 * are constructed automatically
 * 
 * @author Alexander Lex
 */
public class GeneContextMenuItemContainer
	extends AItemContainer {

	/**
	 * Constructor.
	 */
	public GeneContextMenuItemContainer() {
		super();

		if (GeneralManager.get().getUseCase().getUseCaseMode() != EUseCaseMode.GENETIC_DATA)
			throw new IllegalStateException("This context menu container is only valid for genetic data");

	}

	public void setStorageIndex(int iStorageIndex) {
		Integer davidID =
			GeneralManager.get().getIDMappingManager().getID(EIDType.EXPRESSION_INDEX, EIDType.DAVID,
				iStorageIndex);
		if(davidID == null)
			davidID = -1;
		// mappingHelper.getDavidIDFromStorageIndex(iStorageIndex);
		createMenuContent(davidID);
	}

	public void setDavid(int davidID) {
		createMenuContent(davidID);
	}

	private void createMenuContent(int davidID) {
		String sGeneSymbol =
			GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID, EIDType.GENE_SYMBOL, davidID);
		if (sGeneSymbol == "" || sGeneSymbol == null)
			sGeneSymbol = "Unkonwn Gene";
		addHeading(sGeneSymbol);

		LoadPathwaysByGeneItem loadPathwaysByGeneItem = new LoadPathwaysByGeneItem();
		loadPathwaysByGeneItem.setDavid(davidID);
		addContextMenuItem(loadPathwaysByGeneItem);

		ShowPathwaysByGeneItem showPathwaysByGeneItem = new ShowPathwaysByGeneItem();
		showPathwaysByGeneItem.setDavid(davidID);
		addContextMenuItem(showPathwaysByGeneItem);

		Set<Integer> setExpIndex =
			GeneralManager.get().getIDMappingManager().getID(EIDType.DAVID, EIDType.EXPRESSION_INDEX, davidID);
		
		if (setExpIndex == null)
			return;
		
		ArrayList<Integer> alStorageIndex = new ArrayList<Integer>();
		alStorageIndex.addAll(setExpIndex);
//			GeneticIDMappingHelper.get().getExpressionIndicesFromDavid(davidID);
//
//		if (alStorageIndex == null)
//			return;

		BookmarkItem addToListItem = new BookmarkItem(alStorageIndex);
		addContextMenuItem(addToListItem);
	}
}
