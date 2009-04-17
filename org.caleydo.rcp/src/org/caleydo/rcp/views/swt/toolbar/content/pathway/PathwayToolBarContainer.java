package org.caleydo.rcp.views.swt.toolbar.content.pathway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.action.toolbar.view.pathway.GeneMappingAction;
import org.caleydo.rcp.action.toolbar.view.pathway.NeighborhoodAction;
import org.caleydo.rcp.action.toolbar.view.pathway.TextureAction;
import org.caleydo.rcp.util.search.SearchBox;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;
import org.caleydo.rcp.views.swt.toolbar.content.browser.BrowserToolBarMediator;
import org.caleydo.rcp.views.swt.toolbar.content.browser.QueryTypeRadioButton;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;

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
