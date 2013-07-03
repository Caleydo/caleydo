/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.OpenViewHandler;

/**
 * basic {@link IState} for browsing something in tour guides
 * 
 * @author Samuel Gratzl
 * 
 */
public abstract class ABrowseState implements IState {
	private final EDataDomainQueryMode mode;
	private final String label;

	public ABrowseState(EDataDomainQueryMode mode, String label) {
		this.mode = mode;
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void onEnter() {
		OpenViewHandler.showTourGuide(mode);
	}

	@Override
	public void onLeave() {

	}

	/**
	 * called when the user browsed for a stratification
	 * 
	 * @param event
	 * @param adapter
	 */
	public void onUpdate(UpdateStratificationPreviewEvent event, IReactions adapter) {

	}

	/**
	 * called when the user browsed for a pathway
	 * 
	 * @param event
	 * @param adapter
	 */
	public void onUpdate(UpdatePathwayPreviewEvent event, IReactions adapter) {

	}

	/**
	 * called when the user browsed for a numerical variable, i.e. other
	 * 
	 * @param event
	 * @param adapter
	 */
	public void onUpdate(UpdateNumericalPreviewEvent event, IReactions adapter) {

	}
}
