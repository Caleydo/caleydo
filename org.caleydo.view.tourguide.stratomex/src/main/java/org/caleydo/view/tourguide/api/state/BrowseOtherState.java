/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseOtherState extends ABrowseState {
	protected Perspective underlying;

	public BrowseOtherState(ITourGuideAdapter adapter, String label) {
		super(adapter, label);
	}

	/**
	 * @param underlying
	 *            setter, see {@link underlying}
	 */
	public void setUnderlying(Perspective underlying) {
		this.underlying = underlying;
	}

	@Override
	public void onUpdateOther(TablePerspective tablePerspective, IReactions adapter) {
		show(tablePerspective, adapter);
	}

	protected void show(TablePerspective numerical, IReactions adapter) {
		adapter.replaceOtherTemplate(underlying, numerical, false, true);
	}


}
