package org.caleydo.rcp.views.swt.toolbar.content.pathway;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.action.toolbar.view.pathway.GeneMappingAction;
import org.caleydo.rcp.action.toolbar.view.pathway.TextureAction;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;

/**
 * Widget based toolbar container to display pathway related toolbar content. 
 * @author Marc Streit
 */
public class PathwayToolBarContainer
	extends ToolBarContainer {

	/** Mediator to handle actions triggered by the contributed elements */ 
	PathwayToolBarMediator pathwayToolBarMediator;
	
	/**
	 * Creates a the pathway selection box and add the pathway toolbar items.
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();
		
		elements.add(new TextureAction(pathwayToolBarMediator));
		elements.add(new GeneMappingAction(-1)); // FIXME: What should we provide as view ID?

		// TODO: neighborhood currently broken 
		//elements.add(new NeighborhoodAction(pathwayToolBarMediator));		
		
		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox("");
		pathwaySearchBox.setPathwayToolBarMediator(pathwayToolBarMediator);
		elements.add(pathwaySearchBox);
		
		return elements;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}
}
