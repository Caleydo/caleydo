/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.pathway.toolbar;

import java.util.Collection;

import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.util.SearchBox;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Drop down style toolbar-contribution to select pathway.
 * 
 * @author Marc Streit
 */
public class PathwaySearchBox
	extends ControlContribution
	implements IToolBarItem {

	public static final int TOOLBAR_WIDTH = 500;

	/** mediator to handle actions triggered by the contributed element */
	private PathwayToolBarMediator pathwayToolBarMediator;

	private int pathwayID = -1;

	/**
	 * constructor as requested by ControlContribution
	 * 
	 * @param str
	 */
	public PathwaySearchBox(String str, int pathwayID) {
		super(str);
		this.pathwayID = pathwayID;
	}

	@Override
	protected Control createControl(Composite parent) {

		final SearchBox pathwaySearchBox = new SearchBox(parent, SWT.BORDER);
		pathwaySearchBox.setToolTipText("Select which pathway to show");
		String items[] = { "No pathways available!" };
		pathwaySearchBox.setItems(items);
		pathwaySearchBox.setTextLimit(90);

		if (PathwayManager.get().hasItem(pathwayID)) {
			PathwayGraph pathway = PathwayManager.get().getItem(pathwayID);
			pathwaySearchBox.setText(pathway.getTitle() + " (" + pathway.getType().getName() + ")");
		}

		pathwaySearchBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {

				Collection<PathwayGraph> allPathways = PathwayManager.get().getAllItems();
				String[] searchItems = new String[allPathways.size()];
				int iIndex = 0;
				String sPathwayTitle = "";
				for (PathwayGraph pathway : allPathways) {
					sPathwayTitle = pathway.getTitle();

					// if (sPathwayTitle.length() >
					// MAX_PATHWAY_TITLE_LENGTH)
					// sPathwayTitle = sPathwayTitle.substring(0,
					// MAX_PATHWAY_TITLE_LENGTH) + "... ";

					// sArSearchItems[iIndex] = pathway.getType().toString()
					// + " - " + sPathwayTitle;

					searchItems[iIndex] = sPathwayTitle + " (" + pathway.getType().getName() + ")";
					iIndex++;
				}

				pathwaySearchBox.setItems(searchItems);
				pathwaySearchBox.removeFocusListener(this);
			}
		});

		pathwaySearchBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sSearchEntity = pathwaySearchBox.getItem(pathwaySearchBox.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				loadPathway(sSearchEntity);
			}
		});

		// pathwaySearchBox.addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyReleased(KeyEvent e) {
		//
		// if (e.character != SWT.CR)
		// return;
		//
		// String sSearchEntity =
		// pathwaySearchBox.getItem(pathwaySearchBox.getSelectionIndex());
		// loadPathway(sSearchEntity);
		// }
		// });

		return pathwaySearchBox;
	}

	@Override
	protected int computeWidth(Control control) {

		return TOOLBAR_WIDTH;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	/**
	 * Method gets a pathway title and tries to determine the pathway ID. If this is successful the load pathway event
	 * is triggered.
	 * 
	 * @param entity Pathway search title
	 * @return
	 */
	private boolean loadPathway(String entity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (entity.contains(EPathwayDatabaseType.KEGG.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		}
		else if (entity.contains(EPathwayDatabaseType.BIOCARTA.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		}
		else
			return false;

		entity = entity.substring(0, entity.indexOf(" ("));

		PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(entity, ePathwayDatabaseType);

		if (pathway == null)
			return false;

		pathwayToolBarMediator.loadPathway(pathway);

		ChangeURLEvent event = new ChangeURLEvent();
		event.setSender(this);
		event.setUrl(pathway.getExternalLink());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		return true;
	}
}
