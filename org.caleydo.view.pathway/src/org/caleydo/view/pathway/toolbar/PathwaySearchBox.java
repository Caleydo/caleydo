/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.toolbar;

import java.util.Collection;

import org.caleydo.core.gui.util.SearchBox;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwayEvent;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.GLPathway;
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
public class PathwaySearchBox extends ControlContribution {

	public static final int TOOLBAR_WIDTH = 500;

	private GLPathway glPathwayView;

	/**
	 * constructor as requested by ControlContribution
	 *
	 */
	public PathwaySearchBox(GLPathway glPathwayView) {
		super("");

		this.glPathwayView = glPathwayView;
	}

	@Override
	protected Control createControl(Composite parent) {

		final SearchBox pathwaySearchBox = new SearchBox(parent, SWT.BORDER);
		pathwaySearchBox.setToolTipText("Select which pathway to show");
		String items[] = { "No pathways available!" };
		pathwaySearchBox.setItems(items);
		pathwaySearchBox.setTextLimit(90);

		// if (PathwayManager.get().hasItem(pathwayID)) {
		// PathwayGraph pathway = PathwayManager.get().getItem(pathwayID);
		// pathwaySearchBox.setText(pathway.getTitle() + " (" + pathway.getType().getName() + ")");
		// }

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

	/**
	 * Method gets a pathway title and tries to determine the pathway ID. If this is successful the load pathway event
	 * is triggered.
	 *
	 * @param entity
	 *            Pathway search title
	 * @return
	 */
	private boolean loadPathway(String entity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (entity.contains(EPathwayDatabaseType.KEGG.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		} else if (entity.contains(EPathwayDatabaseType.WIKIPATHWAYS.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.WIKIPATHWAYS;
		} else
			return false;

		entity = entity.substring(0, entity.indexOf(" ("));

		PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(entity, ePathwayDatabaseType);

		if (pathway == null)
			return false;

		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathway.getID());

		GeneralManager.get().getEventPublisher().triggerEvent(event);

		return true;
	}
}
