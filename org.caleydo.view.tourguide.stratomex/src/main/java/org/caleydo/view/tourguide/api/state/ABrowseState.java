/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.tourguide.api.vis.TourGuideUtils;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;

/**
 * basic {@link IState} for browsing something in tour guides
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ABrowseState implements IState {
	private final ITourGuideAdapter adapter;
	private final String label;

	public ABrowseState(ITourGuideAdapter adapter, String label) {
		this.adapter = adapter;
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
		TourGuideUtils.showTourGuide(adapter.getSecondaryID());
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
	public void onUpdateStratification(TablePerspective tablePerspective, IReactions adapter) {

	}

	/**
	 * called when the user browsed for a pathway
	 *
	 * @param event
	 * @param adapter
	 */
	public void onUpdatePathway(PathwayGraph pathway, IReactions adapter) {

	}

	/**
	 * called when the user browsed for a numerical variable, i.e. other
	 *
	 * @param event
	 * @param adapter
	 */
	public void onUpdateOther(TablePerspective tablePerspective, IReactions adapter) {

	}
}
