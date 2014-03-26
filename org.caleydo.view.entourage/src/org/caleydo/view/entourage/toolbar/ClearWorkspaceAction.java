/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.entourage.Activator;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.event.ClearWorkspaceEvent;

/**
 * @author Christian
 *
 */
public class ClearWorkspaceAction extends SimpleAction {
	public static final String LABEL = "Remove all pathways";
	public static final String ICON = "resources/icons/clear_pathways.png";

	private final GLEntourage entourage;

	/**
	 * Constructor.
	 */
	public ClearWorkspaceAction(GLEntourage entourage) {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		this.entourage = entourage;
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		// PathwayPathSelectionEvent pathEvent = new PathwayPathSelectionEvent();
		// pathEvent.setPathSegments(new ArrayList<PathwayPath>());
		// pathEvent.setSender(this);
		// pathEvent.setEventSpace(entourage.getPathEventSpace());
		// GeneralManager.get().getEventPublisher().triggerEvent(pathEvent);

		ClearWorkspaceEvent clearWorkspaceEvent = new ClearWorkspaceEvent();
		clearWorkspaceEvent.to(entourage);
		EventPublisher.INSTANCE.triggerEvent(clearWorkspaceEvent);
	}
}
