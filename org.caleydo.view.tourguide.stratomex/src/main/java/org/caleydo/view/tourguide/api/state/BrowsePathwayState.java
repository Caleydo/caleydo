/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowsePathwayState extends ABrowseState {
	protected Perspective underlying;

	public BrowsePathwayState(String adapter, String label) {
		super(adapter, label);
	}

	/**
	 * @param underlying
	 *            setter, see {@link underlying}
	 */
	public final void setUnderlying(Perspective underlying) {
		this.underlying = underlying;
	}

	@Override
	public void onUpdatePathway(PathwayGraph pathway, IReactions adapter) {
		adapter.replacePathwayTemplate(underlying, pathway, false, true);
	}
}
