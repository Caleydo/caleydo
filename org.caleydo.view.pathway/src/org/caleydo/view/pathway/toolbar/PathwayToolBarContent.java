package org.caleydo.view.pathway.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.pathway.GLPathway;

/**
 * ToolBarContent implementation for bucket specific toolbar items.
 * 
 * @author Werner Puff
 */
public class PathwayToolBarContent extends AToolBarContent {

	public static final String PATHWAY_IMAGE_PATH = "resources/icons/view/pathway/pathway.png";
	public static final String PATHWAY_VIEW_TITLE = "Pathways";

	PathwayToolBarMediator mediator;

	@Override
	public Class<?> getViewClass() {
		return GLPathway.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(createPathwayContainer());

		return list;
	}

	/**
	 * Creates and returns icons for pathway related toolbar box FIXME: pathway
	 * buttons do not work this way at the moment, because the related commands
	 * need a pathway-view-id, not a bucket id. instead of commands an event
	 * should be dispatched where all pathways are listening, too.
	 * 
	 * @return pathway related toolbar box
	 */
	private ToolBarContainer createPathwayContainer() {

		PathwayToolBarContainer container = new PathwayToolBarContainer();

		container.setImagePath(PATHWAY_IMAGE_PATH);
		container.setTitle(PATHWAY_VIEW_TITLE);

		container.setPathwayToolBarMediator(new PathwayToolBarMediator(targetViewData
				.getDataDomainID()));
		container.setTargetViewData(targetViewData);

		return container;
	}
}
