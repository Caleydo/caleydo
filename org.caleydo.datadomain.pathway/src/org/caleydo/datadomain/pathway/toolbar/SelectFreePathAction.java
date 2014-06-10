/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.datadomain.pathway.Activator;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;

/**
 * Event to enable free selection of pathway nodes.
 *
 * @author Christian Partl
 *
 */
public class SelectFreePathAction extends SimpleAction {

	public static final String LABEL = "Toggle free selection of nodes";
	public static final String ICON = "resources/path_selection.png";
	private String eventSpace;
	private SelectPathAction selectPathAction;

	public SelectFreePathAction(boolean isChecked, String eventSpace) {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		setChecked(isChecked);
		this.eventSpace = eventSpace;
	}

	public SelectFreePathAction(boolean isChecked, String eventSpace, SelectPathAction selectPathAction) {
		super(LABEL, ICON);
		setChecked(isChecked);
		this.eventSpace = eventSpace;
		this.selectPathAction = selectPathAction;
	}

	@Override
	public void run() {
		super.run();
		if (isChecked()) {
			EnablePathSelectionEvent event = new EnablePathSelectionEvent(false);
			event.setEventSpace(eventSpace);
			
			EventPublisher.trigger(event);
			if (selectPathAction != null)
				selectPathAction.setChecked(false);
		}

		EnableFreePathSelectionEvent e = new EnableFreePathSelectionEvent(isChecked());
		e.setEventSpace(eventSpace);
		
		EventPublisher.trigger(e);
	}

	/**
	 * @param selectPathAction
	 *            setter, see {@link selectPathAction}
	 */
	public void setSelectPathAction(SelectPathAction selectPathAction) {
		this.selectPathAction = selectPathAction;
	}

}
